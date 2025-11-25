package impl;

import framework.lang.Type;
import framework.project4.Project4SemanticError;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcLexer;
import generated.Splc.SplcParser;
import generated.Splc.SplcParser.ExpressionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ExprVisitor extends SplcBaseVisitor<Void> {

    private final Function<String, VariableSymbol> variableResolver;
    private final Function<String, FunctionSymbol> functionResolver;
    private final Function<String, StructType> structResolver;

    private enum ValueCategory { LVALUE, RVALUE }

    private record TypeInfo(Type type, ValueCategory category) {}

    public ExprVisitor(Function<String, VariableSymbol> variableResolver,
                       Function<String, FunctionSymbol> functionResolver,
                       Function<String, StructType> structResolver) {
        this.variableResolver = Objects.requireNonNull(variableResolver);
        this.functionResolver = Objects.requireNonNull(functionResolver);
        this.structResolver = Objects.requireNonNull(structResolver);
    }

    public ExprVisitor() {
        throw new UnsupportedOperationException("ExprVisitor requires resolvers");
    }

    @Override
    public Void visitExpression(SplcParser.ExpressionContext ctx) {
        TypeInfo info = evaluate(ctx);
        enforceStatementContext(ctx, normalize(info.type));
        return null;
    }

    private TypeInfo evaluate(ExpressionContext ctx) {
        if(ctx == null){
            return new TypeInfo(new IntType(), ValueCategory.RVALUE);
        }
        if(isParenthesized(ctx)){
            return evaluate(ctx.expression(0));
        }
        if(isNumberLiteral(ctx)){
            return new TypeInfo(new IntType(), ValueCategory.RVALUE);
        }
        if(isIdentifierOnly(ctx)){
            return resolveIdentifier(ctx);
        }
        if(isFunctionCall(ctx)){
            return handleFunctionCall(ctx);
        }
        if(isPrefixIncDec(ctx)){
            return handleIncDec(ctx);
        }
        if(isPostfixIncDec(ctx)){
            return handleIncDec(ctx);
        }
        if(isUnaryPlusOrMinus(ctx)){
            return handleUnaryPlusMinus(ctx);
        }
        if(ctx.NOT()!=null && ctx.expression().size()==1){
            return handleLogicalNot(ctx);
        }
        if(isUnaryDeref(ctx)){
            return handleUnaryDeref(ctx);
        }
        if(isAddressOf(ctx)){
            return handleAddressOf(ctx);
        }
        if(ctx.ASSIGN()!=null && ctx.expression().size()==2){
            return handleAssignment(ctx);
        }
        if(ctx.OR()!=null){
            return handleLogical(ctx, ctx.OR().getSymbol());
        }
        if(ctx.AND()!=null){
            return handleLogical(ctx, ctx.AND().getSymbol());
        }
        if(hasEqualityOp(ctx)){
            return handleEquality(ctx);
        }
        if(hasRelationalOp(ctx)){
            return handleRelational(ctx);
        }
        if(hasAdditiveOp(ctx)){
            return handleAdditive(ctx);
        }
        if(hasMultiplicativeOp(ctx)){
            return handleMultiplicative(ctx);
        }
        if(ctx.LBRACK()!=null){
            return handleArrayAccess(ctx);
        }
        if(ctx.DOT()!=null){
            return handleStructMember(ctx, false);
        }
        if(ctx.ARROW()!=null){
            return handleStructMember(ctx, true);
        }
        throw new IllegalStateException("Unhandled expression: " + ctx.getText());
    }

    private TypeInfo resolveIdentifier(ExpressionContext ctx){
        String name = ctx.Identifier().getText();
        VariableSymbol symbol = variableResolver.apply(name);
        if(symbol == null){
            if(functionResolver.apply(name) != null){
                Project4SemanticError.identifierNotVariable(ctx, name).throwException();
            }
            Project4SemanticError.identifierNotVariable(ctx, name).throwException();
        }
        return new TypeInfo(normalize(symbol.typeContainer), ValueCategory.LVALUE);
    }

    private TypeInfo handleFunctionCall(ExpressionContext ctx){
        String name = ctx.Identifier().getText();
        FunctionSymbol function = functionResolver.apply(name);
        if(function == null){
            Project4SemanticError.identifierNotFunction(ctx, name).throwException();
        }
        List<TypeInfo> arguments = new ArrayList<>();
        for(ExpressionContext argCtx : ctx.expression()){
            arguments.add(evaluate(argCtx));
        }
        if(function.params.size() != arguments.size()){
            Project4SemanticError.badParamCount(ctx, function.params.size(), arguments.size()).throwException();
        }
        for(int i = 0; i < arguments.size(); i++){
            Type expected = normalize(function.params.get(i));
            if(!typeEquals(expected, arguments.get(i).type)){
                Project4SemanticError.badParamType(ctx, i+1).throwException();
            }
        }
        return new TypeInfo(normalize(function.returnType), ValueCategory.RVALUE);
    }

    private TypeInfo handleIncDec(ExpressionContext ctx){
        TypeInfo operand = evaluate(ctx.expression(0));
        ensureLvalue(ctx.expression(0), operand);
        Type operandType = normalize(operand.type);
        if(!isInt(operandType) && !isPointer(operandType)){
            Project4SemanticError.unexpectedType(ctx, operandType).throwException();
        }
        return new TypeInfo(operandType, ValueCategory.RVALUE);
    }

    private TypeInfo handleUnaryPlusMinus(ExpressionContext ctx){
        TypeInfo operand = evaluate(ctx.expression(0));
        Type operandType = normalize(operand.type);
        if(!isInt(operandType)){
            Project4SemanticError.unexpectedType(ctx, operandType).throwException();
        }
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleLogicalNot(ExpressionContext ctx){
        TypeInfo operand = evaluate(ctx.expression(0));
        ensureLogicalCompatible(ctx.expression(0), operand.type);
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleUnaryDeref(ExpressionContext ctx){
        TypeInfo operand = evaluate(ctx.expression(0));
        Type operandType = normalize(operand.type);
        if(operandType instanceof PointerType pointerType){
            return new TypeInfo(normalize(pointerType.type), ValueCategory.LVALUE);
        }
        Project4SemanticError.unexpectedType(ctx, operandType).throwException();
        return new TypeInfo(operandType, ValueCategory.LVALUE);
    }

    private TypeInfo handleAddressOf(ExpressionContext ctx){
        TypeInfo operand = evaluate(ctx.expression(0));
        ensureLvalue(ctx.expression(0), operand);
        return new TypeInfo(new PointerType(normalize(operand.type)), ValueCategory.RVALUE);
    }

    private TypeInfo handleAssignment(ExpressionContext ctx){
        TypeInfo lhs = evaluate(ctx.expression(0));
        TypeInfo rhs = evaluate(ctx.expression(1));
        ensureLvalue(ctx.expression(0), lhs);
        Type leftType = normalize(lhs.type);
        Type rightType = normalize(rhs.type);
        Token op = ctx.ASSIGN().getSymbol();
        boolean leftInt = isInt(leftType);
        boolean rightInt = isInt(rightType);
        boolean leftPtr = isPointer(leftType);
        boolean rightPtr = isPointer(rightType);
        if(leftInt && rightInt){
            return new TypeInfo(rightType, ValueCategory.RVALUE);
        }
        if(leftPtr && rightPtr){
            if(!typeEquals(leftType, rightType)){
                Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
            }
            return new TypeInfo(rightType, ValueCategory.RVALUE);
        }
        Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
        return new TypeInfo(rightType, ValueCategory.RVALUE);
    }

    private TypeInfo handleLogical(ExpressionContext ctx, Token op){
        TypeInfo left = evaluate(ctx.expression(0));
        ensureLogicalCompatible(ctx.expression(0), left.type);
        TypeInfo right = evaluate(ctx.expression(1));
        ensureLogicalCompatible(ctx.expression(1), right.type);
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleEquality(ExpressionContext ctx){
        Token op = ctx.EQ()!=null ? ctx.EQ().getSymbol() : ctx.NEQ().getSymbol();
        TypeInfo left = evaluate(ctx.expression(0));
        TypeInfo right = evaluate(ctx.expression(1));
        Type leftType = normalize(left.type);
        Type rightType = normalize(right.type);
        boolean leftInt = isInt(leftType);
        boolean rightInt = isInt(rightType);
        boolean leftPtr = isPointer(leftType);
        boolean rightPtr = isPointer(rightType);
        if(leftInt && rightInt){
            return new TypeInfo(new IntType(), ValueCategory.RVALUE);
        }
        if(leftPtr && rightPtr){
            if(!typeEquals(leftType, rightType)){
                Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
            }
            return new TypeInfo(new IntType(), ValueCategory.RVALUE);
        }
        Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleRelational(ExpressionContext ctx){
        Token op = firstToken(ctx.LT(), ctx.LE(), ctx.GT(), ctx.GE());
        TypeInfo left = evaluate(ctx.expression(0));
        ensureIntType(ctx.expression(0), left.type);
        TypeInfo right = evaluate(ctx.expression(1));
        ensureIntType(ctx.expression(1), right.type);
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleAdditive(ExpressionContext ctx){
        Token op = ctx.PLUS()!=null ? ctx.PLUS().getSymbol() : ctx.MINUS().getSymbol();
        TypeInfo left = evaluate(ctx.expression(0));
        TypeInfo right = evaluate(ctx.expression(1));
        Type leftType = normalize(left.type);
        Type rightType = normalize(right.type);
        boolean leftInt = isInt(leftType);
        boolean rightInt = isInt(rightType);
        boolean leftPtr = isPointer(leftType);
        boolean rightPtr = isPointer(rightType);
        if(leftInt && rightInt){
            return new TypeInfo(new IntType(), ValueCategory.RVALUE);
        }
        if(ctx.PLUS()!=null){
            if(leftPtr && rightInt){
                return new TypeInfo(leftType, ValueCategory.RVALUE);
            }
            if(leftInt && rightPtr){
                return new TypeInfo(rightType, ValueCategory.RVALUE);
            }
        } else {
            if(leftPtr && rightInt){
                return new TypeInfo(leftType, ValueCategory.RVALUE);
            }
            if(leftPtr && rightPtr){
                if(!typeEquals(leftType, rightType)){
                    Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
                }
                return new TypeInfo(new IntType(), ValueCategory.RVALUE);
            }
        }
        Project4SemanticError.unmatchedTypeForBinaryOP(ctx, op, leftType, rightType).throwException();
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleMultiplicative(ExpressionContext ctx){
        Token op = firstToken(ctx.STAR(), ctx.DIV(), ctx.MOD());
        TypeInfo left = evaluate(ctx.expression(0));
        ensureIntType(ctx.expression(0), left.type);
        TypeInfo right = evaluate(ctx.expression(1));
        ensureIntType(ctx.expression(1), right.type);
        return new TypeInfo(new IntType(), ValueCategory.RVALUE);
    }

    private TypeInfo handleArrayAccess(ExpressionContext ctx){
        TypeInfo base = evaluate(ctx.expression(0));
        TypeInfo index = evaluate(ctx.expression(1));
        ensureIntType(ctx.expression(1), index.type);
        Type baseType = normalize(base.type);
        Type elementType;
        if(baseType instanceof ArrayType arrayType){
            ensureLvalue(ctx.expression(0), base);
            elementType = normalize(arrayType.type);
        }else if(baseType instanceof PointerType pointerType){
            elementType = normalize(pointerType.type);
        }else{
            Project4SemanticError.unexpectedType(ctx.expression(0), baseType).throwException();
            return new TypeInfo(baseType, ValueCategory.LVALUE);
        }
        return new TypeInfo(elementType, ValueCategory.LVALUE);
    }

    private TypeInfo handleStructMember(ExpressionContext ctx, boolean pointerAccess){
        TypeInfo base = evaluate(ctx.expression(0));
        Type baseType = normalize(base.type);
        StructType structType;
        if(pointerAccess){
            if(baseType instanceof PointerType pointerType){
                structType = ensureStructType(normalize(pointerType.type), ctx.expression(0));
            }else{
                Project4SemanticError.unexpectedType(ctx.expression(0), baseType).throwException();
                return new TypeInfo(baseType, ValueCategory.LVALUE);
            }
        }else{
            ensureLvalue(ctx.expression(0), base);
            structType = ensureStructType(baseType, ctx.expression(0));
        }
        String memberName = ctx.getChild(ctx.getChildCount()-1).getText();
        VariableSymbol member = findStructMember(structType, memberName);
        if(member == null){
            Project4SemanticError.badMember(ctx, structType, memberName).throwException();
        }
        return new TypeInfo(normalize(member.typeContainer), ValueCategory.LVALUE);
    }

    private void enforceStatementContext(ExpressionContext ctx, Type type){
        ParserRuleContext parent = ctx.getParent();
        if(parent instanceof SplcParser.ReturnStmtContext){
            ensureIntType(ctx, type);
        } else if(parent instanceof SplcParser.IfStmtContext || parent instanceof SplcParser.WhileStmtContext){
            ensureLogicalCompatible(ctx, type);
        }
    }

    private void ensureLvalue(ExpressionContext ctx, TypeInfo info){
        if(info.category != ValueCategory.LVALUE){
            Project4SemanticError.lvalueRequired(ctx).throwException();
        }
    }

    private void ensureIntType(ExpressionContext ctx, Type type){
        Type normalized = normalize(type);
        if(!isInt(normalized)){
            Project4SemanticError.unexpectedType(ctx, normalized).throwException();
        }
    }

    private void ensureLogicalCompatible(ExpressionContext ctx, Type type){
        Type normalized = normalize(type);
        if(!isInt(normalized) && !isPointer(normalized)){
            Project4SemanticError.unexpectedType(ctx, normalized).throwException();
        }
    }

    private StructType ensureStructType(Type type, ExpressionContext ctx){
        Type normalized = normalize(type);
        if(normalized instanceof StructType structType){
            return structType;
        }
        Project4SemanticError.unexpectedType(ctx, normalized).throwException();
        return null;
    }

    private VariableSymbol findStructMember(StructType structType, String memberName){
        if(structType.scope == null){
            return null;
        }
        for(VariableSymbol symbol : structType.scope.declaredSymbols()){
            if(symbol.identifier != null && memberName.equals(symbol.identifier.getText())){
                return symbol;
            }
        }
        return null;
    }

    private boolean isParenthesized(ExpressionContext ctx){
        return ctx.getChildCount()==3 && ctx.LPAREN()!=null && ctx.RPAREN()!=null && ctx.expression().size()==1;
    }

    private boolean isNumberLiteral(ExpressionContext ctx){
        return ctx.Number()!=null || ctx.Char()!=null;
    }

    private boolean isIdentifierOnly(ExpressionContext ctx){
        return ctx.Identifier()!=null && ctx.getChildCount()==1;
    }

    private boolean isFunctionCall(ExpressionContext ctx){
        return ctx.Identifier()!=null && ctx.LPAREN()!=null && ctx.RPAREN()!=null && ctx.getChildCount()>=3 && !isIdentifierOnly(ctx);
    }

    private boolean isPrefixIncDec(ExpressionContext ctx){
        if(ctx.expression().size()!=1){
            return false;
        }
        ParseTree first = ctx.getChild(0);
        if(first instanceof TerminalNode terminal){
            int type = terminal.getSymbol().getType();
            return type == SplcLexer.INC || type == SplcLexer.DEC;
        }
        return false;
    }

    private boolean isPostfixIncDec(ExpressionContext ctx){
        if(ctx.expression().size()!=1){
            return false;
        }
        ParseTree last = ctx.getChild(ctx.getChildCount()-1);
        if(last instanceof TerminalNode terminal){
            int type = terminal.getSymbol().getType();
            return type == SplcLexer.INC || type == SplcLexer.DEC;
        }
        return false;
    }

    private boolean isUnaryPlusOrMinus(ExpressionContext ctx){
        return ctx.expression().size()==1 && (ctx.PLUS()!=null || ctx.MINUS()!=null);
    }

    private boolean isUnaryDeref(ExpressionContext ctx){
        return ctx.expression().size()==1 && ctx.STAR()!=null && ctx.getChild(0) instanceof TerminalNode;
    }

    private boolean isAddressOf(ExpressionContext ctx){
        return ctx.expression().size()==1 && ctx.AMP()!=null;
    }

    private boolean hasEqualityOp(ExpressionContext ctx){
        return ctx.EQ()!=null || ctx.NEQ()!=null;
    }

    private boolean hasRelationalOp(ExpressionContext ctx){
        return ctx.LT()!=null || ctx.LE()!=null || ctx.GT()!=null || ctx.GE()!=null;
    }

    private boolean hasAdditiveOp(ExpressionContext ctx){
        return ctx.expression().size()==2 && (ctx.PLUS()!=null || ctx.MINUS()!=null);
    }

    private boolean hasMultiplicativeOp(ExpressionContext ctx){
        return ctx.expression().size()==2 && (ctx.STAR()!=null || ctx.DIV()!=null || ctx.MOD()!=null);
    }

    private Token firstToken(TerminalNode... nodes){
        for(TerminalNode node : nodes){
            if(node != null){
                return node.getSymbol();
            }
        }
        return null;
    }

    private Type normalize(Type type){
        Type current = unwrap(type);
        if(current instanceof StructRefType ref){
            StructType resolved = structResolver.apply(ref.identifier.getText());
            if(resolved != null){
                return resolved;
            }
        }
        return current;
    }

    private Type unwrap(Type type){
        Type current = type;
        while(current instanceof TypeContainer tc && tc.type != null){
            current = tc.type;
        }
        return current;
    }

    private boolean isInt(Type type){
        Type normalized = unwrap(type);
        return normalized instanceof IntType || normalized instanceof CharType;
    }

    private boolean isPointer(Type type){
        return unwrap(type) instanceof PointerType;
    }

    private boolean isArrayType(Type type){
        return unwrap(type) instanceof ArrayType;
    }

    private boolean typeEquals(Type left, Type right){
        Type a = normalize(left);
        Type b = normalize(right);
        if(isInt(a) && isInt(b)){
            return true;
        }
        if(a instanceof PointerType pa && b instanceof PointerType pb){
            return typeEquals(pa.type, pb.type);
        }
        if(a instanceof ArrayType aa && b instanceof ArrayType bb){
            return aa.length == bb.length && typeEquals(aa.type, bb.type);
        }
        if(a instanceof StructType sa && b instanceof StructType sb){
            return structName(sa).equals(structName(sb));
        }
        return false;
    }

    private String structName(StructType type){
        return type.identifier == null ? "" : type.identifier.getText();
    }
}
