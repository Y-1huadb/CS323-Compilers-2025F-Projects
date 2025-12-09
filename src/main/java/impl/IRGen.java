package impl;

import framework.AbstractGrader;
import framework.llvm.BasicBlockBuilder;
import framework.llvm.FunctionBuilder;
import framework.llvm.IRBuilder;
import framework.llvm.IRType;
import framework.llvm.IRValue;
import org.antlr.v4.runtime.misc.Pair;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        IRValue zero = IRValue.consti32(0);
        entry.ret(zero);
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
        List<SplcParser.VarDecContext> children = ctx.varDec();
        if (children == null || children.isEmpty()) {
            throw new IllegalStateException("Missing nested declarator for: " + ctx.getText());
        }
        return children.get(0);
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

    // Add more overrides: visitVarDecStmt, visitIfStmt, visitWhileStmt, visitReturnStmt, visitExprStmt...
}
