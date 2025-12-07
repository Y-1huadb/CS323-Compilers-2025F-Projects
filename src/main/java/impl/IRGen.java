package impl;

import framework.AbstractGrader;
import framework.llvm.BasicBlockBuilder;
import framework.llvm.FunctionBuilder;
import framework.llvm.IRBuilder;
import framework.llvm.IRType;
import framework.llvm.IRValue;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;

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
        // Walk all top-level definitions
        // Walk all top-level definitions
        return super.visitProgram(ctx);
    }

    @Override
    public Void visitGlobalDef(SplcParser.GlobalDefContext ctx) {
        // Minimal instance to pass simple tests: declare/define functions
        if (ctx.Identifier() != null) {
            String fname = String.valueOf(ctx.Identifier());
            IRType retTy = IRType.int32();
            java.util.List args = new java.util.ArrayList<>();
            boolean hasBody = ctx.LBRACE() != null;
            if (!hasBody) {
                ir.declareFunction(fname, retTy, args);
            } else {
                FunctionBuilder fb = ir.defineFunction(fname, retTy, args);
                BasicBlockBuilder entry = fb.rootBlock();
                IRValue zero = IRValue.consti32(0);
                entry.ret(zero);
            }
            return null;
        }
        return super.visitGlobalDef(ctx);
    }

    // Extend with statement translations as needed.

    // Add more overrides: visitVarDecStmt, visitIfStmt, visitWhileStmt, visitReturnStmt, visitExprStmt...
}
