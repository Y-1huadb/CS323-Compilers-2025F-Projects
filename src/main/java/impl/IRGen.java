package impl;

import framework.AbstractGrader;
import framework.llvm.BasicBlockBuilder;
import framework.llvm.FunctionBuilder;
import framework.llvm.IRBuilder;
import framework.llvm.IRType;
import framework.llvm.IRValue;
import framework.llvm.LLVMIcmpPredicate;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcLexer;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


class VariableScope {
    LinkedHashMap<String, IRType> typeMap = new LinkedHashMap<>();
    LinkedHashMap<String, IRValue> valueMap = new LinkedHashMap<>();
    LinkedHashMap<String, IRGen.TypeDescriptor> typeInfoMap = new LinkedHashMap<>();

    VariableScope parent;
    ArrayList<VariableScope> children = new ArrayList<>();

    public VariableScope(VariableScope parent) {
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void define(String var, IRGen.TypeDescriptor type, IRValue value) {
        typeMap.put(var, type.irType());
        typeInfoMap.put(var, type);
        valueMap.put(var, value);
    }

    public IRType lookupType(String identifier) {
        IRType type = typeMap.get(identifier);
        if (type != null) return type;
        if (parent != null) return parent.lookupType(identifier);
        return null;
    }

    public IRGen.TypeDescriptor lookupTypeInfo(String identifier) {
        IRGen.TypeDescriptor type = typeInfoMap.get(identifier);
        if (type != null) return type;
        if (parent != null) return parent.lookupTypeInfo(identifier);
        return null;
    }

    public IRValue lookupValue(String identifier) {
        IRValue value = valueMap.get(identifier);
        if (value != null) return value;
        if (parent != null) return parent.lookupValue(identifier);
        return null;
    }
}

public class IRGen extends SplcBaseVisitor<Void> {
    private record FunctionSignature(TypeDescriptor returnType, List<TypeDescriptor> params) {}

    static final class TypeDescriptor {
        private final IRType irType;
        private final TypeDescriptor element;
        private final Integer arraySize;
        private final String structName;

        private TypeDescriptor(IRType irType, TypeDescriptor element, Integer arraySize, String structName) {
            this.irType = irType;
            this.element = element;
            this.arraySize = arraySize;
            this.structName = structName;
        }

        static TypeDescriptor simple(IRType irType) {
            return new TypeDescriptor(irType, null, null, irType.isStructure() ? null : null);
        }

        static TypeDescriptor struct(String name) {
            return new TypeDescriptor(IRType.structure(name), null, null, name);
        }

        static TypeDescriptor pointerTo(TypeDescriptor elem) {
            return new TypeDescriptor(IRType.pointer(), elem, null, null);
        }

        static TypeDescriptor arrayOf(TypeDescriptor elem, int size) {
            return new TypeDescriptor(IRType.array(elem.irType, size), elem, size, null);
        }

        boolean isPointer() {
            return irType.isPointer();
        }

        boolean isArray() {
            return arraySize != null;
        }

        boolean isStruct() {
            return irType.isStructure();
        }

        IRType irType() {
            return irType;
        }

        TypeDescriptor element() {
            return element;
        }

        String structName() {
            if (structName != null) {
                return structName;
            }
            if (element != null) {
                return element.structName();
            }
            return null;
        }
    }

    private static final class EvalResult {
        private final TypeDescriptor typeDesc;
        private final IRValue address;
        private final boolean isLValue;
        private IRValue value;

        private EvalResult(TypeDescriptor typeDesc, IRValue value, IRValue address, boolean isLValue) {
            this.typeDesc = typeDesc;
            this.value = value;
            this.address = address;
            this.isLValue = isLValue;
        }

        static EvalResult rvalue(TypeDescriptor desc, IRValue value) {
            return new EvalResult(desc, value, null, false);
        }

        static EvalResult lvalue(TypeDescriptor desc, IRValue address) {
            return new EvalResult(desc, null, address, true);
        }

        IRValue asRValue(BasicBlockBuilder block) {
            if (!isLValue) {
                return value;
            }
            if (value == null) {
                value = block.load(address, typeDesc.irType, null);
            }
            return value;
        }
    }

    private static final TypeDescriptor INT32 = TypeDescriptor.simple(IRType.int32());

    private final AbstractGrader grader;
    private final IRBuilder ir;
    private final LinkedHashMap<String, LinkedHashMap<String, TypeDescriptor>> definedStructures = new LinkedHashMap<>();
    private final Map<String, FunctionSignature> functions = new LinkedHashMap<>();
    private FunctionBuilder currentFunction;
    private FunctionSignature currentSignature;
    private BasicBlockBuilder currentBlock;
    private VariableScope curVariableScope = new VariableScope(null);

    public IRGen(AbstractGrader grader) {
        this.grader = grader;
        this.ir = new IRBuilder();
    }

    public IRBuilder getIRBuilder() {
        return ir;
    }

    public AbstractGrader getGrader() {
        return grader;
    }

    @Override
    public Void visitProgram(SplcParser.ProgramContext ctx) {
        for (SplcParser.GlobalDefContext def : ctx.globalDef()) {
            visit(def);
        }
        return null;
    }

    @Override
    public Void visitGlobalDef(SplcParser.GlobalDefContext ctx) {
        SplcParser.SpecifierContext spec = ctx.specifier();

        if (ctx.Identifier() != null) {
            handleFunction(spec, ctx);
            return null;
        }

        if (ctx.varDec() != null) {
            handleGlobalVariable(spec, ctx.varDec());
            return null;
        }

        if (spec != null) {
            resolveSpecifierType(spec);
        }
        return null;
    }

    private void handleFunction(SplcParser.SpecifierContext spec, SplcParser.GlobalDefContext ctx) {
        String name = ctx.Identifier().getText();
        TypeDescriptor returnDesc = resolveSpecifierType(spec);
        List<VarInfo> argInfos = buildFunctionArgs(ctx.funcArgs());

        List<Pair<String, IRType>> argsForIR = new ArrayList<>();
        for (VarInfo info : argInfos) {
            argsForIR.add(new Pair<>(info.name(), info.type().irType));
        }

        FunctionSignature signature = new FunctionSignature(returnDesc, argInfos.stream().map(VarInfo::type).collect(Collectors.toList()));
        functions.put(name, signature);

        if (ctx.LBRACE() == null) {
            ir.declareFunction(name, returnDesc.irType, argsForIR);
            return;
        }

        FunctionBuilder fb = ir.defineFunction(name, returnDesc.irType, argsForIR);
        BasicBlockBuilder entry = fb.rootBlock();

        this.currentFunction = fb;
        this.currentSignature = signature;
        this.currentBlock = entry;

        this.curVariableScope = new VariableScope(curVariableScope);

        for (VarInfo arg : argInfos) {
            IRValue paramPtr = fb.param(arg.name());
            curVariableScope.define(arg.name(), arg.type(), paramPtr);
        }

        for (SplcParser.StatementContext statementContext : ctx.statement()) {
            visit(statementContext);
        }

        this.curVariableScope = curVariableScope.parent;
        this.currentFunction = null;
        this.currentSignature = null;
        this.currentBlock = null;
    }

    private void handleGlobalVariable(SplcParser.SpecifierContext spec, SplcParser.VarDecContext declarator) {
        TypeDescriptor baseType = resolveSpecifierType(spec);
        VarInfo info = resolveDeclarator(baseType, declarator);
        IRValue gv = ir.defineGlobalVar(info.name(), info.type().irType);
        curVariableScope.define(info.name(), info.type(), gv);
    }

    private List<VarInfo> buildFunctionArgs(SplcParser.FuncArgsContext ctx) {
        List<VarInfo> args = new ArrayList<>();
        if (ctx == null) {
            return args;
        }

        List<SplcParser.SpecifierContext> specs = ctx.specifier();
        List<SplcParser.VarDecContext> decls = ctx.varDec();
        for (int i = 0; i < specs.size(); i++) {
            TypeDescriptor baseType = resolveSpecifierType(specs.get(i));
            VarInfo info = resolveDeclarator(baseType, decls.get(i));
            args.add(info);
        }
        return args;
    }

    private TypeDescriptor resolveSpecifierType(SplcParser.SpecifierContext specCtx) {
        if (specCtx.INT() != null) {
            return INT32;
        }
        if (specCtx.CHAR() != null) {
            return INT32;
        }
        if (specCtx.STRUCT() != null) {
            String name = specCtx.Identifier().getText();
            if (specCtx.LBRACE() != null && !definedStructures.containsKey(name)) {
                List<IRType> fieldTypes = new ArrayList<>();
                LinkedHashMap<String, TypeDescriptor> members = new LinkedHashMap<>();
                List<SplcParser.SpecifierContext> fieldSpecs = specCtx.specifier();
                List<SplcParser.VarDecContext> fieldDecls = specCtx.varDec();
                for (int i = 0; i < fieldSpecs.size(); i++) {
                    TypeDescriptor fieldBase = resolveSpecifierType(fieldSpecs.get(i));
                    VarInfo fieldInfo = resolveDeclarator(fieldBase, fieldDecls.get(i));
                    fieldTypes.add(fieldInfo.type().irType);
                    members.put(fieldInfo.name(), fieldInfo.type());
                }
                ir.defineStructure(name, fieldTypes);
                definedStructures.put(name, members);
            }
            return TypeDescriptor.struct(name);
        }
        throw new IllegalStateException("Unsupported specifier: " + specCtx.getText());
    }

    private VarInfo resolveDeclarator(TypeDescriptor type, SplcParser.VarDecContext declarator) {
        if (declarator.Identifier() != null) {
            return new VarInfo(declarator.Identifier().getText(), type);
        }

        SplcParser.VarDecContext inner = nextVarDec(declarator);
        if (declarator.LPAREN() != null) {
            return resolveDeclarator(type, inner);
        }
        if (declarator.STAR() != null) {
            return resolveDeclarator(TypeDescriptor.pointerTo(type), inner);
        }
        if (declarator.LBRACK() != null) {
            int size = Integer.parseInt(declarator.Number().getText());
            TypeDescriptor arrayType = TypeDescriptor.arrayOf(type, size);
            return resolveDeclarator(arrayType, inner);
        }
        throw new IllegalStateException("Unsupported declarator: " + declarator.getText());
    }

    private SplcParser.VarDecContext nextVarDec(SplcParser.VarDecContext ctx) {
        SplcParser.VarDecContext children = ctx.varDec();
        if (children == null) {
            throw new IllegalStateException("Missing nested declarator for: " + ctx.getText());
        }
        return children;
    }

    private static final class VarInfo {
        private final String name;
        private final TypeDescriptor type;

        private VarInfo(String name, TypeDescriptor type) {
            this.name = name;
            this.type = type;
        }

        private String name() {
            return name;
        }

        private TypeDescriptor type() {
            return type;
        }
    }

    @Override
    public Void visitCodeBlock(SplcParser.CodeBlockContext ctx) {
        curVariableScope = new VariableScope(curVariableScope);
        for (SplcParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        curVariableScope = curVariableScope.parent;
        return null;
    }

    @Override
    public Void visitVarDecStmt(SplcParser.VarDecStmtContext ctx) {
        TypeDescriptor baseType = resolveSpecifierType(ctx.specifier());
        VarInfo varInfo = resolveDeclarator(baseType, ctx.varDec());

        IRValue allocaPtr = currentBlock.alloca(varInfo.type().irType, varInfo.name());
        curVariableScope.define(varInfo.name(), varInfo.type(), allocaPtr);

        if (ctx.expression() != null) {
            EvalResult init = evaluateExpression(ctx.expression(), varInfo.type());
            IRValue initValue = convertValue(init, varInfo.type());
            currentBlock.store(allocaPtr, varInfo.type().irType, initValue);
        }

        return null;
    }

    @Override
    public Void visitIfStmt(SplcParser.IfStmtContext ctx) {
        IRValue condition = toBoolean(evaluateExpression(ctx.expression()));

        BasicBlockBuilder thenBlock = currentFunction.newBasicBlock("if.then");
        BasicBlockBuilder elseBlock = ctx.ELSE() != null ? currentFunction.newBasicBlock("if.else") : null;
        BasicBlockBuilder mergeBlock = currentFunction.newBasicBlock("if.end");

        if (elseBlock != null) {
            currentBlock.condBr(condition, thenBlock, elseBlock);
        } else {
            currentBlock.condBr(condition, thenBlock, mergeBlock);
        }

        currentBlock = thenBlock;
        visit(ctx.statement(0));
        if (!currentBlock.hasTerminated()) {
            currentBlock.br(mergeBlock);
        }

        if (elseBlock != null) {
            currentBlock = elseBlock;
            visit(ctx.statement(1));
            if (!currentBlock.hasTerminated()) {
                currentBlock.br(mergeBlock);
            }
        }

        currentBlock = mergeBlock;
        return null;
    }

    @Override
    public Void visitWhileStmt(SplcParser.WhileStmtContext ctx) {
        BasicBlockBuilder condBlock = currentFunction.newBasicBlock("while.cond");
        BasicBlockBuilder bodyBlock = currentFunction.newBasicBlock("while.body");
        BasicBlockBuilder exitBlock = currentFunction.newBasicBlock("while.exit");

        currentBlock.br(condBlock);

        currentBlock = condBlock;
        IRValue condition = toBoolean(evaluateExpression(ctx.expression()));
        currentBlock.condBr(condition, bodyBlock, exitBlock);

        currentBlock = bodyBlock;
        visit(ctx.statement());
        if (!currentBlock.hasTerminated()) {
            currentBlock.br(condBlock);
        }

        currentBlock = exitBlock;
        return null;
    }

    @Override
    public Void visitReturnStmt(SplcParser.ReturnStmtContext ctx) {
        TypeDescriptor expected = currentSignature != null ? currentSignature.returnType() : INT32;
        EvalResult result = evaluateExpression(ctx.expression(), expected);
        IRValue returnValue = convertValue(result, expected);
        currentBlock.ret(returnValue);
        return null;
    }

    @Override
    public Void visitExprStmt(SplcParser.ExprStmtContext ctx) {
        evaluateExpression(ctx.expression());
        return null;
    }

    private EvalResult evaluateExpression(SplcParser.ExpressionContext ctx) {
        return evaluateExpression(ctx, null);
    }

    private EvalResult evaluateExpression(SplcParser.ExpressionContext ctx, TypeDescriptor expectedType) {
        if (ctx == null) {
            return EvalResult.rvalue(INT32, IRValue.consti32(0));
        }
        if (ctx.LPAREN() != null && ctx.expression().size() == 1 && ctx.Identifier() == null) {
            return evaluateExpression(ctx.expression(0), expectedType);
        }

        if (ctx.Number() != null) {
            int value = Integer.parseInt(ctx.Number().getText());
            if (expectedType != null && expectedType.isPointer() && value == 0) {
                return EvalResult.rvalue(expectedType, IRValue.constNull());
            }
            return EvalResult.rvalue(INT32, IRValue.consti32(value));
        }

        if (ctx.Char() != null) {
            int value = parseCharLiteral(ctx.Char().getText());
            return EvalResult.rvalue(INT32, IRValue.consti32(value));
        }

        if (isIdentifierOnly(ctx)) {
            String varName = ctx.Identifier().getText();
            IRValue varPtr = curVariableScope.lookupValue(varName);
            TypeDescriptor desc = curVariableScope.lookupTypeInfo(varName);
            if (varPtr == null || desc == null) {
                throw new RuntimeException("Variable not found: " + varName);
            }
            return EvalResult.lvalue(desc, varPtr);
        }

        if (isFunctionCall(ctx)) {
            return evaluateCall(ctx);
        }

        if (ctx.LBRACK() != null) {
            return evaluateArrayAccess(ctx);
        }

        if (ctx.DOT() != null || ctx.ARROW() != null) {
            return evaluateStructAccess(ctx);
        }

        if (isPostfixIncDec(ctx)) {
            return handleIncDec(ctx, false);
        }

        if (isPrefixIncDec(ctx)) {
            return handleIncDec(ctx, true);
        }

        if (isUnaryPlusOrMinus(ctx)) {
            return handleUnaryPlusMinus(ctx);
        }

        if (ctx.NOT() != null && ctx.expression().size() == 1) {
            return handleLogicalNot(ctx);
        }

        if (isUnaryDeref(ctx)) {
            return handleDeref(ctx);
        }

        if (isAddressOf(ctx)) {
            return handleAddressOf(ctx);
        }

        if (ctx.ASSIGN() != null) {
            return evaluateAssignment(ctx);
        }

        if (ctx.OR() != null) {
            return handleLogical(ctx, true);
        }
        if (ctx.AND() != null) {
            return handleLogical(ctx, false);
        }

        if (ctx.EQ() != null || ctx.NEQ() != null || ctx.LT() != null || ctx.LE() != null || ctx.GT() != null || ctx.GE() != null) {
            return evaluateRelOp(ctx);
        }

        if (ctx.PLUS() != null || ctx.MINUS() != null || ctx.STAR() != null || ctx.DIV() != null || ctx.MOD() != null) {
            return evaluateBinaryOp(ctx);
        }

        throw new RuntimeException("Unsupported expression: " + ctx.getText());
    }

    private EvalResult evaluateArrayAccess(SplcParser.ExpressionContext ctx) {
        EvalResult array = evaluateExpression(ctx.expression(0));
        EvalResult indexRes = evaluateExpression(ctx.expression(1));
        IRValue index = ensureInt32(indexRes);

        if (array.typeDesc.isArray()) {
            IRValue elementPtr = currentBlock.gep(array.address, array.typeDesc.irType, 0, index, null);
            return EvalResult.lvalue(array.typeDesc.element(), elementPtr);
        }

        if (array.typeDesc.isPointer()) {
            TypeDescriptor elem = array.typeDesc.element() != null ? array.typeDesc.element() : INT32;
            IRValue ptrValue = array.asRValue(currentBlock);
            IRValue elementPtr = currentBlock.gep(ptrValue, elem.irType, index, null);
            return EvalResult.lvalue(elem, elementPtr);
        }

        throw new RuntimeException("Array access on non-array: " + ctx.getText());
    }

    private EvalResult evaluateStructAccess(SplcParser.ExpressionContext ctx) {
        EvalResult base = evaluateExpression(ctx.expression(0));
        boolean viaPointer = ctx.ARROW() != null;

        TypeDescriptor structDesc;
        IRValue structPtr;

        if (viaPointer) {
            if (!base.typeDesc.isPointer() || base.typeDesc.element() == null) {
                throw new RuntimeException("Arrow operator on non-pointer struct");
            }
            structDesc = base.typeDesc.element();
            structPtr = base.asRValue(currentBlock);
        } else {
            structDesc = base.typeDesc;
            structPtr = base.address;
        }

        String structName = structDesc.structName();
        if (structName == null || !definedStructures.containsKey(structName)) {
            throw new RuntimeException("Unknown struct: " + structName);
        }

        String fieldName = ctx.Identifier().getText();
        LinkedHashMap<String, TypeDescriptor> members = definedStructures.get(structName);
        int index = -1;
        TypeDescriptor fieldType = null;
        int cur = 0;
        for (Map.Entry<String, TypeDescriptor> entry : members.entrySet()) {
            if (entry.getKey().equals(fieldName)) {
                index = cur;
                fieldType = entry.getValue();
                break;
            }
            cur++;
        }
        if (index < 0 || fieldType == null) {
            throw new RuntimeException("Unknown field: " + fieldName);
        }

        IRValue fieldPtr = currentBlock.gep(structPtr, structDesc.irType, 0, IRValue.consti32(index), null);
        return EvalResult.lvalue(fieldType, fieldPtr);
    }

    private EvalResult evaluateCall(SplcParser.ExpressionContext ctx) {
        String callee = ctx.Identifier().getText();
        FunctionSignature signature = functions.get(callee);

        List<IRValue> args = new ArrayList<>();
        if (ctx.expression() != null && !ctx.expression().isEmpty()) {
            for (int i = 0; i < ctx.expression().size(); i++) {
                TypeDescriptor expected = null;
                if (signature != null && i < signature.params().size()) {
                    expected = signature.params().get(i);
                }
                EvalResult res = evaluateExpression(ctx.expression(i), expected);
                args.add(convertValue(res, expected != null ? expected : res.typeDesc));
            }
        }

        TypeDescriptor retType = signature != null ? signature.returnType() : INT32;
        IRValue retVal = currentBlock.call(retType.irType, callee, args, null);
        return EvalResult.rvalue(retType, retVal);
    }

    private EvalResult evaluateRelOp(SplcParser.ExpressionContext ctx) {
        EvalResult lhsRes = evaluateExpression(ctx.expression(0));
        EvalResult rhsRes = evaluateExpression(ctx.expression(1));

        if (lhsRes.typeDesc.isPointer() && !rhsRes.typeDesc.isPointer()) {
            rhsRes = evaluateExpression(ctx.expression(1), lhsRes.typeDesc);
        }
        if (rhsRes.typeDesc.isPointer() && !lhsRes.typeDesc.isPointer()) {
            lhsRes = evaluateExpression(ctx.expression(0), rhsRes.typeDesc);
        }

        IRValue lhs = lhsRes.asRValue(currentBlock);
        IRValue rhs = rhsRes.asRValue(currentBlock);

        LLVMIcmpPredicate pred;
        if (ctx.EQ() != null) pred = LLVMIcmpPredicate.Equals;
        else if (ctx.NEQ() != null) pred = LLVMIcmpPredicate.NotEquals;
        else if (ctx.LT() != null) pred = LLVMIcmpPredicate.SignedLT;
        else if (ctx.LE() != null) pred = LLVMIcmpPredicate.SignedLE;
        else if (ctx.GT() != null) pred = LLVMIcmpPredicate.SignedGT;
        else if (ctx.GE() != null) pred = LLVMIcmpPredicate.SignedGE;
        else throw new RuntimeException("Unknown relop");

        IRValue cmp = currentBlock.icmp(lhs, pred, rhs, null);
        IRValue zext = currentBlock.zext(cmp, IRType.int32(), null);
        return EvalResult.rvalue(INT32, zext);
    }

    private EvalResult evaluateBinaryOp(SplcParser.ExpressionContext ctx) {
        if (ctx.expression().size() == 1) {
            EvalResult operand = evaluateExpression(ctx.expression(0));
            if (ctx.PLUS() != null) {
                return EvalResult.rvalue(operand.typeDesc, operand.asRValue(currentBlock));
            }
            if (ctx.MINUS() != null) {
                IRValue zero = IRValue.consti32(0);
                IRValue val = operand.asRValue(currentBlock);
                IRValue neg = currentBlock.sub(zero, val, null);
                return EvalResult.rvalue(INT32, neg);
            }
            throw new RuntimeException("Unknown unary operator");
        }

        EvalResult lhsRes = evaluateExpression(ctx.expression(0));
        EvalResult rhsRes = evaluateExpression(ctx.expression(1));

        boolean lhsPtr = lhsRes.typeDesc.isPointer();
        boolean rhsPtr = rhsRes.typeDesc.isPointer();

        if (ctx.PLUS() != null) {
            if (lhsPtr && !rhsPtr) {
                return pointerAdd(lhsRes, ensureInt32(rhsRes));
            }
            if (rhsPtr && !lhsPtr) {
                return pointerAdd(rhsRes, ensureInt32(lhsRes));
            }
            IRValue lhs = ensureInt32(lhsRes);
            IRValue rhs = ensureInt32(rhsRes);
            return EvalResult.rvalue(INT32, currentBlock.add(lhs, rhs, null));
        }

        if (ctx.MINUS() != null) {
            if (lhsPtr && !rhsPtr) {
                IRValue index = ensureInt32(rhsRes);
                IRValue neg = currentBlock.sub(IRValue.consti32(0), index, null);
                return pointerAdd(lhsRes, neg);
            }
            if (lhsPtr && rhsPtr) {
                throw new RuntimeException("Pointer subtraction not supported without pointer casts");
            }
            IRValue lhs = ensureInt32(lhsRes);
            IRValue rhs = ensureInt32(rhsRes);
            return EvalResult.rvalue(INT32, currentBlock.sub(lhs, rhs, null));
        }

        IRValue lhs = ensureInt32(lhsRes);
        IRValue rhs = ensureInt32(rhsRes);

        if (ctx.STAR() != null) {
            return EvalResult.rvalue(INT32, currentBlock.mul(lhs, rhs, null));
        }
        if (ctx.DIV() != null) {
            return EvalResult.rvalue(INT32, currentBlock.div(lhs, rhs, null));
        }
        if (ctx.MOD() != null) {
            return EvalResult.rvalue(INT32, currentBlock.rem(lhs, rhs, null));
        }

        throw new RuntimeException("Unknown binary operator");
    }

    private EvalResult handleIncDec(SplcParser.ExpressionContext ctx, boolean prefix) {
        EvalResult target = evaluateExpression(ctx.expression(0));
        if (!target.isLValue) {
            throw new RuntimeException("++/-- requires lvalue");
        }
        boolean isInc = ctx.INC() != null;
        int delta = isInc ? 1 : -1;

        if (target.typeDesc.isPointer()) {
            TypeDescriptor elem = target.typeDesc.element() != null ? target.typeDesc.element() : INT32;
            IRValue cur = target.asRValue(currentBlock);
            IRValue offset = IRValue.consti32(delta);
            IRValue updated = currentBlock.gep(cur, elem.irType, offset, null);
            currentBlock.store(target.address, target.typeDesc.irType, updated);
            IRValue resultVal = prefix ? updated : cur;
            return EvalResult.rvalue(target.typeDesc, resultVal);
        }

        IRValue cur = target.asRValue(currentBlock);
        IRValue updated = delta == 1 ? currentBlock.add(cur, IRValue.consti32(1), null) : currentBlock.sub(cur, IRValue.consti32(1), null);
        currentBlock.store(target.address, target.typeDesc.irType, updated);
        IRValue resultVal = prefix ? updated : cur;
        return EvalResult.rvalue(target.typeDesc, resultVal);
    }

    private EvalResult handleUnaryPlusMinus(SplcParser.ExpressionContext ctx) {
        EvalResult operand = evaluateExpression(ctx.expression(0));
        IRValue val = ensureInt32(operand);
        if (ctx.PLUS() != null) {
            return EvalResult.rvalue(INT32, val);
        }
        IRValue neg = currentBlock.sub(IRValue.consti32(0), val, null);
        return EvalResult.rvalue(INT32, neg);
    }

    private EvalResult handleLogicalNot(SplcParser.ExpressionContext ctx) {
        IRValue boolVal = toBoolean(evaluateExpression(ctx.expression(0)));
        IRValue notVal = currentBlock.icmp(boolVal, LLVMIcmpPredicate.Equals, IRValue.constFalse(), null);
        IRValue asInt = currentBlock.zext(notVal, IRType.int32(), null);
        return EvalResult.rvalue(INT32, asInt);
    }

    private EvalResult handleDeref(SplcParser.ExpressionContext ctx) {
        EvalResult pointer = evaluateExpression(ctx.expression(0));
        if (!pointer.typeDesc.isPointer()) {
            throw new RuntimeException("Cannot dereference non-pointer");
        }
        TypeDescriptor elem = pointer.typeDesc.element() != null ? pointer.typeDesc.element() : INT32;
        IRValue addr = pointer.asRValue(currentBlock);
        return EvalResult.lvalue(elem, addr);
    }

    private EvalResult handleAddressOf(SplcParser.ExpressionContext ctx) {
        EvalResult value = evaluateExpression(ctx.expression(0));
        if (!value.isLValue) {
            throw new RuntimeException("Cannot take address of rvalue");
        }
        TypeDescriptor ptrType = TypeDescriptor.pointerTo(value.typeDesc);
        return EvalResult.rvalue(ptrType, value.address);
    }

    private EvalResult handleLogical(SplcParser.ExpressionContext ctx, boolean isOr) {
        IRValue resultPtr = currentBlock.alloca(IRType.int32(), "logic.tmp");

        IRValue leftCond = toBoolean(evaluateExpression(ctx.expression(0)));

        BasicBlockBuilder rhsBlock = currentFunction.newBasicBlock(isOr ? "lor.rhs" : "land.rhs");
        BasicBlockBuilder shortBlock = currentFunction.newBasicBlock(isOr ? "lor.short" : "land.short");
        BasicBlockBuilder endBlock = currentFunction.newBasicBlock(isOr ? "lor.end" : "land.end");

        if (isOr) {
            currentBlock.condBr(leftCond, shortBlock, rhsBlock);
            currentBlock = shortBlock;
            currentBlock.store(resultPtr, IRType.int32(), IRValue.consti32(1));
            currentBlock.br(endBlock);

            currentBlock = rhsBlock;
            IRValue rightCond = toBoolean(evaluateExpression(ctx.expression(1)));
            IRValue asInt = currentBlock.zext(rightCond, IRType.int32(), null);
            currentBlock.store(resultPtr, IRType.int32(), asInt);
            currentBlock.br(endBlock);
        } else {
            currentBlock.condBr(leftCond, rhsBlock, shortBlock);
            currentBlock = shortBlock;
            currentBlock.store(resultPtr, IRType.int32(), IRValue.consti32(0));
            currentBlock.br(endBlock);

            currentBlock = rhsBlock;
            IRValue rightCond = toBoolean(evaluateExpression(ctx.expression(1)));
            IRValue asInt = currentBlock.zext(rightCond, IRType.int32(), null);
            currentBlock.store(resultPtr, IRType.int32(), asInt);
            currentBlock.br(endBlock);
        }

        currentBlock = endBlock;
        IRValue result = currentBlock.load(resultPtr, IRType.int32(), null);
        return EvalResult.rvalue(INT32, result);
    }

    private EvalResult evaluateAssignment(SplcParser.ExpressionContext ctx) {
        EvalResult lhs = evaluateExpression(ctx.expression(0));
        if (!lhs.isLValue) {
            throw new RuntimeException("Assignment requires lvalue");
        }
        EvalResult rhs = evaluateExpression(ctx.expression(1), lhs.typeDesc);

        IRValue rhsValue = convertValue(rhs, lhs.typeDesc);
        currentBlock.store(lhs.address, lhs.typeDesc.irType, rhsValue);
        return EvalResult.rvalue(lhs.typeDesc, rhsValue);
    }

    private IRValue convertValue(EvalResult value, TypeDescriptor target) {
        if (value == null) {
            return IRValue.consti32(0);
        }
        TypeDescriptor sourceType = value.typeDesc;
        IRValue raw = value.asRValue(currentBlock);

        if (target == null || sourceType.irType.typeEquals(target.irType)) {
            return raw;
        }

        if (target.isPointer() && sourceType.irType.isInteger()) {
            return raw.name().isEmpty() && raw.type().isInteger() && raw.llvmName().equals("0") ? IRValue.constNull() : raw;
        }

        if (target.irType.isInteger() && raw.type().isBoolean()) {
            return currentBlock.zext(raw, IRType.int32(), null);
        }

        return raw;
    }

    private IRValue toBoolean(EvalResult value) {
        IRType type = value.typeDesc.irType;
        if (type.isBoolean()) {
            return value.asRValue(currentBlock);
        }
        if (type.isInteger()) {
            IRValue cmp = currentBlock.icmp(value.asRValue(currentBlock), LLVMIcmpPredicate.NotEquals, IRValue.consti32(0), null);
            return cmp;
        }
        if (type.isPointer()) {
            IRValue cmp = currentBlock.icmp(value.asRValue(currentBlock), LLVMIcmpPredicate.NotEquals, IRValue.constNull(), null);
            return cmp;
        }
        throw new RuntimeException("Cannot convert to boolean");
    }

    private EvalResult pointerAdd(EvalResult pointer, IRValue index) {
        TypeDescriptor elem = pointer.typeDesc.element() != null ? pointer.typeDesc.element() : INT32;
        IRValue base = pointer.asRValue(currentBlock);
        IRValue ptr = currentBlock.gep(base, elem.irType, index, null);
        return EvalResult.rvalue(pointer.typeDesc, ptr);
    }

    private IRValue ensureInt32(EvalResult result) {
        if (result.typeDesc.irType.isInteger()) {
            return result.asRValue(currentBlock);
        }
        if (result.typeDesc.irType.isBoolean()) {
            return currentBlock.zext(result.asRValue(currentBlock), IRType.int32(), null);
        }
        throw new RuntimeException("Expected integer value");
    }

    private boolean isIdentifierOnly(SplcParser.ExpressionContext ctx) {
        return ctx.Identifier() != null && ctx.expression().isEmpty() && ctx.LPAREN() == null && ctx.LBRACK() == null && ctx.DOT() == null && ctx.ARROW() == null && ctx.ASSIGN() == null;
    }

    private boolean isFunctionCall(SplcParser.ExpressionContext ctx) {
        return ctx.Identifier() != null && ctx.LPAREN() != null && ctx.RPAREN() != null && !isIdentifierOnly(ctx);
    }

    private boolean isIncDec(SplcParser.ExpressionContext ctx) {
        return ctx.INC() != null || ctx.DEC() != null;
    }

    private boolean isPrefixIncDec(SplcParser.ExpressionContext ctx) {
        if (!isIncDec(ctx) || ctx.expression().size() != 1) {
            return false;
        }
        Token start = ctx.getStart();
        int type = start.getType();
        return type == SplcLexer.INC || type == SplcLexer.DEC;
    }

    private boolean isPostfixIncDec(SplcParser.ExpressionContext ctx) {
        return isIncDec(ctx) && ctx.expression().size() == 1 && !isPrefixIncDec(ctx);
    }

    private boolean isUnaryPlusOrMinus(SplcParser.ExpressionContext ctx) {
        return ctx.expression().size() == 1 && (ctx.PLUS() != null || ctx.MINUS() != null);
    }

    private boolean isUnaryDeref(SplcParser.ExpressionContext ctx) {
        return ctx.expression().size() == 1 && ctx.STAR() != null && ctx.getStart().getType() == SplcLexer.STAR;
    }

    private boolean isAddressOf(SplcParser.ExpressionContext ctx) {
        return ctx.expression().size() == 1 && ctx.AMP() != null && ctx.getStart().getType() == SplcLexer.AMP;
    }

    private int parseCharLiteral(String text) {
        if (text == null || text.length() < 3) {
            return 0;
        }
        String content = text.substring(1, text.length() - 1);
        if (content.length() == 1 && content.charAt(0) != '\\') {
            return content.charAt(0);
        }
        if (content.startsWith("\\")) {
            char esc = content.charAt(1);
            return switch (esc) {
                case 'n' -> '\n';
                case 't' -> '\t';
                case '\\' -> '\\';
                case '\'' -> '\'';
                case '0' -> 0;
                default -> esc;
            };
        }
        return 0;
    }
}
