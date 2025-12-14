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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Set<String> definedStructures = new HashSet<>();
    private FunctionBuilder currentFunction;
    private BasicBlockBuilder currentBlock;
    
    // Symbol table: variable name -> allocated pointer (IRValue)
    private Map<String, IRValue> symbolTable = new HashMap<>();

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
        System.out.println(ctx.getText());
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
        
        // TODO: Process function body statements
        List<SplcParser.StatementContext> statementContexts = ctx.statement();
        for(SplcParser.StatementContext statementContext : statementContexts){
            // Translate each statement
            // visitStatement(statementContext, fb, entry);
            visit(statementContext);
        }
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
            if (specCtx.LBRACE() != null && definedStructures.add(name)) {
                List<IRType> fieldTypes = new ArrayList<>();
                List<SplcParser.SpecifierContext> fieldSpecs = specCtx.specifier();
                List<SplcParser.VarDecContext> fieldDecls = specCtx.varDec();
                for (int i = 0; i < fieldSpecs.size(); i++) {
                    IRType fieldBase = resolveSpecifierType(fieldSpecs.get(i));
                    VarInfo fieldInfo = resolveDeclarator(fieldBase, fieldDecls.get(i));
                    fieldTypes.add(fieldInfo.type());
                }
                ir.defineStructure(name, fieldTypes);
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
        
        // Register the variable in symbol table
        symbolTable.put(varInfo.name(), allocaPtr);
        
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
        System.out.println(ctx.getText());

        System.out.println(ctx.statement(0).getText());
        System.out.println(ctx.statement(1).getText());

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

    /**
     * Evaluate an expression and return its IRValue (right-hand side value).
     * For example:
     *   - "1 + 1" returns an IRValue representing the sum
     *   - "a" returns the loaded value from variable a
     *   - "a = 5" stores 5 to a and returns the stored value
     */
    private IRValue evaluateExpression(SplcParser.ExpressionContext ctx) {
        if (ctx.Number() != null) {
            // Constant integer: e.g., 1, 42, 100
            int value = Integer.parseInt(ctx.Number().getText());
            return IRValue.consti32(value);
        }
        
        if (ctx.Identifier() != null && ctx.getChildCount() == 1) {
            // Variable reference: e.g., a, x, count
            String varName = ctx.Identifier().getText();
            IRValue varPtr = symbolTable.get(varName);
            if (varPtr == null) {
                throw new RuntimeException("Variable not found: " + varName);
            }
            // Load the value from the variable
            return currentBlock.load(varPtr, IRType.int32(), null);
        }
        
        if (ctx.LPAREN() != null && ctx.expression().size() == 1) {
            // Parenthesized expression: (expr)
            return evaluateExpression(ctx.expression(0));
        }

        if (ctx.EQ() != null || ctx.NEQ() != null ||
            ctx.LT() != null || ctx.LE() != null ||
            ctx.GT() != null || ctx.GE() != null) {
            return evaluateRelOp(ctx);  // ·µ»Ø i1
        }
        
        // Binary operations: a + b, a - b, a * b, a / b, a = b, etc.
        if (ctx.PLUS() != null || ctx.MINUS() != null || ctx.STAR() != null || 
            ctx.DIV() != null || ctx.MOD() != null) {
            return evaluateBinaryOp(ctx);
        }
        
        if (ctx.ASSIGN() != null) {
            // Assignment: a = expr
            return evaluateAssignment(ctx);
        }
        
        // TODO: Add support for more expressions (function calls, array access, struct access, etc.)
        throw new RuntimeException("Unsupported expression: " + ctx.getText());
    }


    private IRValue evaluateRelOp(SplcParser.ExpressionContext ctx) {
        IRValue lhs = evaluateExpression(ctx.expression(0)); // i32
        IRValue rhs = evaluateExpression(ctx.expression(1)); // i32

        // TODO Whether there should consider unsigned int
        LLVMIcmpPredicate pred;
        if (ctx.EQ() != null) pred = LLVMIcmpPredicate.Equals;
        else if (ctx.NEQ() != null) pred = LLVMIcmpPredicate.NotEquals;
        else if (ctx.LT() != null) pred = LLVMIcmpPredicate.SignedLT;
        else if (ctx.LE() != null) pred = LLVMIcmpPredicate.SignedLE;
        else if (ctx.GT() != null) pred = LLVMIcmpPredicate.SignedGT;
        else if (ctx.GE() != null) pred = LLVMIcmpPredicate.SignedLE;
        else throw new RuntimeException("Unknown relop");

        return currentBlock.icmp(lhs, pred, rhs, null); // i1
    }

    /**
     * Evaluate binary arithmetic operations.
     * For example, "1 + 1" is translated to:
     *   %tmp = add i32 1, 1
     */
    private IRValue evaluateBinaryOp(SplcParser.ExpressionContext ctx) {
        IRValue lhs = evaluateExpression(ctx.expression(0));
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
    
    /**
     * Evaluate assignment expression: a = rhs
     * This stores the RHS value into variable a and returns the stored value.
     * For example, "a = 1 + 1" is translated to:
     *   %tmp = add i32 1, 1
     *   store i32 %tmp, i32* %a
     */
    private IRValue evaluateAssignment(SplcParser.ExpressionContext ctx) {
        // LHS must be an identifier (variable name)
        SplcParser.ExpressionContext lhsExpr = ctx.expression(0);
        if (lhsExpr.Identifier() == null || lhsExpr.getChildCount() != 1) {
            throw new RuntimeException("Assignment target must be a variable");
        }
        
        String varName = lhsExpr.Identifier().getText();
        IRValue varPtr = symbolTable.get(varName);
        if (varPtr == null) {
            throw new RuntimeException("Variable not found: " + varName);
        }
        
        // Evaluate RHS
        IRValue rhsValue = evaluateExpression(ctx.expression(1));
        
        // Store RHS value to the variable
        currentBlock.store(varPtr, IRType.int32(), rhsValue);
        
        return rhsValue;
    }

    // Add more overrides: visitVarDecStmt, visitIfStmt, visitWhileStmt, visitReturnStmt, visitExprStmt...
}
