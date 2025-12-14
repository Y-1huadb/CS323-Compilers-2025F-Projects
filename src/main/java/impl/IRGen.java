package impl;

import framework.AbstractGrader;
import framework.llvm.BasicBlockBuilder;
import framework.llvm.FunctionBuilder;
import framework.llvm.IRBuilder;
import framework.llvm.IRType;
import framework.llvm.IRValue;
import framework.llvm.LLVMIcmpPredicate;
import org.antlr.v4.runtime.misc.Pair;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;


class VariableScope {
    // Symbols table
    LinkedHashMap<String, IRType> typeMap = new LinkedHashMap<>();
    LinkedHashMap<String, IRValue> valueMap = new LinkedHashMap<>();

    // Parent Scope
    VariableScope parent;
    //Child Scope;
    ArrayList<VariableScope> children = new ArrayList<>();

    public VariableScope(VariableScope parent) {
        this.parent = parent;
        if(parent != null){
            parent.children.add(this);
        }
    }

    public void define(String var, IRType type, IRValue value){
        typeMap.put(var, type);
        valueMap.put(var, value);
    }

    public IRType lookupType(String identifier){
        IRType type = typeMap.get(identifier);
        if (type != null) return type;
        if (parent != null) return parent.lookupType(identifier);
        return null;
    }

    public IRValue lookupValue(String identifier){
        IRValue value = valueMap.get(identifier);
        if(value != null) return value;
        if(parent != null) return parent.lookupValue(identifier);
        return null;
    }

}

/**
 * Minimal IR generator skeleton for Project 5.
 *
 * Responsibilities:
 * - Hold a single global IRBuilder instance.
 * - Traverse the parsed AST and emit structures, globals, and functions.
 * - Provide a simple, incremental starting point you can extend.
 *
 * Note: Keep logic independent of any local modifications to framework classes.
 */
public class IRGen extends SplcBaseVisitor<Void> {
    private final AbstractGrader grader;
    private final IRBuilder ir;
    private final LinkedHashMap<String, LinkedHashMap<String, IRType>> definedStructures = new LinkedHashMap<>();
    private FunctionBuilder currentFunction;
    private BasicBlockBuilder currentBlock;
    
    // Symbol table: variable name -> allocated pointer (IRValue)
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
        IRType returnType = resolveSpecifierType(spec);
        List<Pair<String, IRType>> args = buildFunctionArgs(ctx.funcArgs());

        if (ctx.LBRACE() == null) {
            ir.declareFunction(name, returnType, args);
            return;
        }

        FunctionBuilder fb = ir.defineFunction(name, returnType, args);
        BasicBlockBuilder entry = fb.rootBlock();

        this.currentFunction = fb;
        this.currentBlock = entry;


        this.curVariableScope = new VariableScope(curVariableScope);

        if (args != null) {
            for (Pair<String, IRType> arg : args) {
                IRValue paramPtr = fb.param(arg.a);
                curVariableScope.define(arg.a, arg.b, paramPtr);
            }
        }

        List<SplcParser.StatementContext> statementContexts = ctx.statement();
        for(SplcParser.StatementContext statementContext : statementContexts){
            visit(statementContext);
        }

        this.curVariableScope = curVariableScope.parent;
        this.currentFunction = null;
        this.currentBlock = null;
    }

    private void handleGlobalVariable(SplcParser.SpecifierContext spec, SplcParser.VarDecContext declarator) {
        IRType baseType = resolveSpecifierType(spec);
        VarInfo info = resolveDeclarator(baseType, declarator);
        ir.defineGlobalVar(info.name(), info.type());
    }

    private List<Pair<String, IRType>> buildFunctionArgs(SplcParser.FuncArgsContext ctx) {
        List<Pair<String, IRType>> args = new ArrayList<>();
        if (ctx == null) {
            return args;
        }

        List<SplcParser.SpecifierContext> specs = ctx.specifier();
        List<SplcParser.VarDecContext> decls = ctx.varDec();
        for (int i = 0; i < specs.size(); i++) {
            IRType baseType = resolveSpecifierType(specs.get(i));
            VarInfo info = resolveDeclarator(baseType, decls.get(i));
            args.add(new Pair<>(info.name(), info.type()));
        }
        return args;
    }

    private IRType resolveSpecifierType(SplcParser.SpecifierContext specCtx) {
        if (specCtx.INT() != null) {
            return IRType.int32();
        }
        if (specCtx.CHAR() != null) {
            return IRType.int32();
        }
        if (specCtx.STRUCT() != null) {
            String name = specCtx.Identifier().getText();
            if (specCtx.LBRACE() != null && !definedStructures.containsKey(name)) {
                List<IRType> fieldTypes = new ArrayList<>();
                LinkedHashMap<String, IRType> members = new LinkedHashMap<>();
                List<SplcParser.SpecifierContext> fieldSpecs = specCtx.specifier();
                List<SplcParser.VarDecContext> fieldDecls = specCtx.varDec();
                for (int i = 0; i < fieldSpecs.size(); i++) {
                    IRType fieldBase = resolveSpecifierType(fieldSpecs.get(i));
                    VarInfo fieldInfo = resolveDeclarator(fieldBase, fieldDecls.get(i));
                    fieldTypes.add(fieldInfo.type());
                    // TODO: Check here definedStructures
                    System.out.println(fieldDecls.get(i).getText());
                    members.put(fieldDecls.get(i).getText(), fieldBase);
                }
                ir.defineStructure(name, fieldTypes);
                definedStructures.put(name, members);
            }
            return IRType.structure(name);
        }
        throw new IllegalStateException("Unsupported specifier: " + specCtx.getText());
    }

    private VarInfo resolveDeclarator(IRType type, SplcParser.VarDecContext declarator) {
        if (declarator.Identifier() != null) {
            return new VarInfo(declarator.Identifier().getText(), type);
        }

        SplcParser.VarDecContext inner = nextVarDec(declarator);
        if (declarator.LPAREN() != null) {
            return resolveDeclarator(type, inner);
        }
        if (declarator.STAR() != null) {
            return resolveDeclarator(IRType.pointer(), inner);
        }
        if (declarator.LBRACK() != null) {
            int size = Integer.parseInt(declarator.Number().getText());
            IRType arrayType = IRType.array(type, size);
            return resolveDeclarator(arrayType, inner);
        }
        throw new IllegalStateException("Unsupported declarator: " + declarator.getText());
    }

    private SplcParser.VarDecContext nextVarDec(SplcParser.VarDecContext ctx) {
        // TODO check here: List<SplcParser.VarDecContext> children = ctx.varDec();
        SplcParser.VarDecContext children = ctx.varDec();
        if (children == null) {
            throw new IllegalStateException("Missing nested declarator for: " + ctx.getText());
        }
        return children;
    }

    private static final class VarInfo {
        private final String name;
        private final IRType type;

        private VarInfo(String name, IRType type) {
            this.name = name;
            this.type = type;
        }

        private String name() {
            return name;
        }

        private IRType type() {
            return type;
        }
    }

    // Extend with statement translations as needed.

    @Override
    public Void visitCodeBlock(SplcParser.CodeBlockContext ctx) {
        for (SplcParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        
        return null;
    }

    @Override
    public Void visitVarDecStmt(SplcParser.VarDecStmtContext ctx) {
        // Get the type and variable declaration
        IRType baseType = resolveSpecifierType(ctx.specifier());
        VarInfo varInfo = resolveDeclarator(baseType, ctx.varDec());

        // Allocate space on the stack for the variable
        IRValue allocaPtr = currentBlock.alloca(varInfo.type(), varInfo.name());

        curVariableScope.define(varInfo.name, varInfo.type, allocaPtr);
        
        // If there's an initialization expression
        if (ctx.expression() != null) {
            System.out.println(ctx.expression().getText());
            // Evaluate the expression to get its value
            IRValue initValue = evaluateExpression(ctx.expression());
            // Store the value to the allocated pointer
            currentBlock.store(allocaPtr, varInfo.type(), initValue);
        }

        return null;
    }

    @Override
    public Void visitIfStmt(SplcParser.IfStmtContext ctx) {

        // TODO: Evaluate condition expression
         IRValue condition = evaluateExpression(ctx.expression());

        // Create basic blocks for then, else (if exists), and merge
        BasicBlockBuilder thenBlock = currentFunction.newBasicBlock("if.then");
        BasicBlockBuilder elseBlock = ctx.ELSE() != null ?
                currentFunction.newBasicBlock("if.else") : null;
        BasicBlockBuilder mergeBlock = currentFunction.newBasicBlock("if.end");

         if (elseBlock != null) {
             currentBlock.condBr(condition, thenBlock, elseBlock);
         } else {
             currentBlock.condBr(condition, thenBlock, mergeBlock);
         }

        // Translate then branch
        currentBlock = thenBlock;
        visit(ctx.statement(0));
        if(!currentBlock.hasTerminated()) {
            currentBlock.br(mergeBlock);
        }

        // Translate else branch if exists
        if (elseBlock != null) {
            currentBlock = elseBlock;
            visit(ctx.statement(1));
            if(!currentBlock.hasTerminated()) {
                currentBlock.br(mergeBlock);
            }
        }

        // Continue with merge block
        currentBlock = mergeBlock;
        return null;
    }

    @Override
    public Void visitWhileStmt(SplcParser.WhileStmtContext ctx) {
        // Create basic blocks for condition, body, and exit
        BasicBlockBuilder condBlock = currentFunction.newBasicBlock("while.cond");
        BasicBlockBuilder bodyBlock = currentFunction.newBasicBlock("while.body");
        BasicBlockBuilder exitBlock = currentFunction.newBasicBlock("while.exit");

        // Branch to condition block
        currentBlock.br(condBlock);

        // Translate condition
        currentBlock = condBlock;
        // TODO: Evaluate condition expression
         IRValue condition = evaluateExpression(ctx.expression());
         currentBlock.condBr(condition, bodyBlock, exitBlock);

        // Translate loop body
        currentBlock = bodyBlock;
        visit(ctx.statement());
        currentBlock.br(condBlock); // Loop back to condition

        // Continue with exit block
        currentBlock = exitBlock;
        return null;
    }

    @Override
    public Void visitReturnStmt(SplcParser.ReturnStmtContext ctx) {
         IRValue returnValue = evaluateExpression(ctx.expression());
         currentBlock.ret(returnValue);
        return null;
    }

    @Override
    public Void visitExprStmt(SplcParser.ExprStmtContext ctx) {
        evaluateExpression(ctx.expression());
        return null;
    }

    private IRValue evaluateExpression(SplcParser.ExpressionContext ctx) {
        if (ctx.LPAREN() != null && ctx.expression().size() == 1 && ctx.Identifier() == null) {
            // Parenthesized expression
            return evaluateExpression(ctx.expression(0));
        }

        if (ctx.Number() != null) {
            // Constant integer
            int value = Integer.parseInt(ctx.Number().getText());
            return IRValue.consti32(value);
        }
        
        if (ctx.Identifier() != null && ctx.getChildCount() == 1) {
            // Variable reference
            String varName = ctx.Identifier().getText();
            IRValue varPtr = curVariableScope.lookupValue(varName);
            if (varPtr == null) {
                throw new RuntimeException("Variable not found: " + varName);
            }
            return currentBlock.load(varPtr, IRType.int32(), null);
        }

        if (ctx.Identifier() != null && ctx.LPAREN() != null) {
            return evaluateCall(ctx);
        }

        // Array access: a[i]
        if (ctx.LBRACK() != null) {
            return evaluateArrayAccess(ctx);
        }
        
        // Struct/pointer field access: s.field or p->field
        if (ctx.DOT() != null || ctx.ARROW() != null) {
            return evaluateStructAccess(ctx);
        }

        if (ctx.EQ() != null || ctx.NEQ() != null ||
            ctx.LT() != null || ctx.LE() != null ||
            ctx.GT() != null || ctx.GE() != null) {
            return evaluateRelOp(ctx);
        }
        
        // Binary operations
        if (ctx.PLUS() != null || ctx.MINUS() != null || ctx.STAR() != null || 
            ctx.DIV() != null || ctx.MOD() != null) {
            return evaluateBinaryOp(ctx);
        }
        
        if (ctx.ASSIGN() != null) {
            // Assignment
            return evaluateAssignment(ctx);
        }
        
        // TODO: Add support for more expressions (pointer dereference, etc.)
        throw new RuntimeException("Unsupported expression: " + ctx.getText());
    }

    private IRValue evaluateArrayAccess(SplcParser.ExpressionContext ctx) {
        SplcParser.ExpressionContext arrayExpr = ctx.expression(0);
        IRValue arrayPtr = evaluateLValue(arrayExpr);

        SplcParser.ExpressionContext indexExpr = ctx.expression(1);
        IRValue index = evaluateExpression(indexExpr);

        IRType elementType = IRType.int32();
        IRValue elementPtr = currentBlock.gep(arrayPtr, elementType, index, null);

        return currentBlock.load(elementPtr, elementType, null);
    }

    private IRValue evaluateStructAccess(SplcParser.ExpressionContext ctx) {
        // 获取结构体或指针
        SplcParser.ExpressionContext structExpr = ctx.expression(0);
        IRValue structPtr = evaluateLValue(structExpr);
        
        // 获取字段名称
        String fieldName = ctx.Identifier().getText();
        
        // TODO: 根据字段名称获取字段索引（需要维护结构体定义信息）
        int fieldIndex = 0;  // 占位符
        IRType fieldType = IRType.int32();  // 占位符
        
        // 使用 GEP 计算字段地址
        IRValue fieldPtr = currentBlock.gep(structPtr, fieldType, 0, 
                                           IRValue.consti32(fieldIndex), null);

        // 加载字段值
        return currentBlock.load(fieldPtr, fieldType, null);
    }

    private IRValue evaluateLValue(SplcParser.ExpressionContext ctx) {
        if (ctx.Identifier() != null && ctx.getChildCount() == 1) {
            // 变量引用
            String varName = ctx.Identifier().getText();
            IRValue varPtr = curVariableScope.lookupValue(varName);

            if (varPtr == null) {
                throw new RuntimeException("Variable not found: " + varName);
            }
            return varPtr;
        }
        
        if (ctx.LBRACK() != null) {
            SplcParser.ExpressionContext arrayExpr = ctx.expression(0);
            IRValue arrayPtr = evaluateLValue(arrayExpr);
            
            SplcParser.ExpressionContext indexExpr = ctx.expression(1);
            IRValue index = evaluateExpression(indexExpr);
            
            IRType elementType = IRType.int32();
            return currentBlock.gep(arrayPtr, elementType, index, null);
        }

        throw new RuntimeException("Not an lvalue: " + ctx.getText());
    }

    private IRValue evaluateCall(SplcParser.ExpressionContext ctx) {
        String callee = ctx.Identifier().getText();

        List<IRValue> args = new ArrayList<>();
        if (ctx.expression() != null && !ctx.expression().isEmpty()) {
            for (SplcParser.ExpressionContext e : ctx.expression()) {
                args.add(evaluateExpression(e));
            }
        }

        IRType retType = IRType.int32();

        return currentBlock.call(retType, callee, args, null);
    }


    private IRValue evaluateRelOp(SplcParser.ExpressionContext ctx) {
        IRValue lhs = evaluateExpression(ctx.expression(0));
        IRValue rhs = evaluateExpression(ctx.expression(1));

        // TODO Whether there should consider unsigned int
        LLVMIcmpPredicate pred;
        if (ctx.EQ() != null) pred = LLVMIcmpPredicate.Equals;
        else if (ctx.NEQ() != null) pred = LLVMIcmpPredicate.NotEquals;
        else if (ctx.LT() != null) pred = LLVMIcmpPredicate.SignedLT;
        else if (ctx.LE() != null) pred = LLVMIcmpPredicate.SignedLE;
        else if (ctx.GT() != null) pred = LLVMIcmpPredicate.SignedGT;
        else if (ctx.GE() != null) pred = LLVMIcmpPredicate.SignedLE;
        else throw new RuntimeException("Unknown relop");

        return currentBlock.icmp(lhs, pred, rhs, null);
    }

    private IRValue evaluateBinaryOp(SplcParser.ExpressionContext ctx) {
        IRValue lhs = evaluateExpression(ctx.expression(0));
        if(ctx.expression().size() == 1){
            if(ctx.PLUS() != null){
                return lhs;
            } else if (ctx.MINUS() != null) {
                IRValue tmp = IRValue.consti32(0);
                return currentBlock.sub(tmp, lhs, null);
            }
            throw new RuntimeException("Unknown unary operator");
        }
        IRValue rhs = evaluateExpression(ctx.expression(1));
        
        if (ctx.PLUS() != null) {
            return currentBlock.add(lhs, rhs, null);
        } else if (ctx.MINUS() != null) {
            return currentBlock.sub(lhs, rhs, null);
        } else if (ctx.STAR() != null) {
            return currentBlock.mul(lhs, rhs, null);
        } else if (ctx.DIV() != null) {
            return currentBlock.div(lhs, rhs, null);
        } else if (ctx.MOD() != null) {
            return currentBlock.rem(lhs, rhs, null);
        }
        
        throw new RuntimeException("Unknown binary operator");
    }
    

    private IRValue evaluateAssignment(SplcParser.ExpressionContext ctx) {
        // LHS is an lvalue expression
        SplcParser.ExpressionContext lhsExpr = ctx.expression(0);
        IRValue lhsPtr = evaluateLValue(lhsExpr);  // 获取左值指针
        
        // Evaluate RHS
        IRValue rhsValue = evaluateExpression(ctx.expression(1));
        
        // 确定要赋值的类型（大多数情况是 i32，但可能是其他类型）
        IRType assignType = IRType.int32();  // TODO: 从类型系统中获取实际类型
        
        // Store RHS value to the LHS location
        currentBlock.store(lhsPtr, assignType, rhsValue);
        
        return rhsValue;
    }

    // Add more overrides: visitVarDecStmt, visitIfStmt, visitWhileStmt, visitReturnStmt, visitExprStmt...
}
