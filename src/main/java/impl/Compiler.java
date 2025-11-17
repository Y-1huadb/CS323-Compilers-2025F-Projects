package impl;

import framework.AbstractCompiler;
import framework.AbstractGrader;
import framework.project3.Project3SemanticError;
import framework.lang.Type;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcLexer;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;

abstract class PrimitiveType implements Type{
    @Override
    public String prettyPrint(){
        return "Primitive Type";
    }

}
class IntType extends PrimitiveType{
    @Override
    public String prettyPrint(){
        return "int";
    }
}
class CharType extends PrimitiveType{
    @Override
    public String prettyPrint(){
        return "char";
    }
}
class ArrayType implements Type {
    int length;
    Type type;
    ArrayType(Type type, int length){
        this.type = type;
        this.length = length;
    }
    
    @Override
    public String prettyPrint(){
        return this.type.prettyPrint() + "["+this.length+"]";
    }
}
class StructType implements Type{
    TerminalNode identifier;
    Scope scope; // field scope, may be filled later
    boolean preetyPrint = true;
    StructType(TerminalNode identifier, Scope curScope){
        this.identifier = identifier;
        this.scope = curScope;
    }
    StructType(TerminalNode identifier, List<SplcParser.SpecifierContext> specs, List<SplcParser.VarDecContext> vars, Scope curScope){
        this.identifier = identifier;
        this.scope = curScope;
        if(specs.size() != vars.size()){
            return;
        }
        for (int i = 0; i < specs.size(); i++) {
            SplcParser.SpecifierContext curSpec = specs.get(i);
            SplcParser.VarDecContext curVar = vars.get(i);
            if(curSpec.INT() != null){
                IntType intType = new IntType();
                VariableSymbol variableSymbol = new VariableSymbol(curVar.varDec(), intType);
                scope.define(variableSymbol);
                continue;
            }
            if(curSpec.CHAR() != null){
                CharType charType = new CharType();
                VariableSymbol variableSymbol = new VariableSymbol(curVar.varDec(), charType);
                scope.define(variableSymbol);
                continue;
            }
            if(curSpec.STRUCT() != null && curSpec.LBRACE() == null){
                VariableSymbol variableSymbol = scope.lookup(curSpec.Identifier());
                if(variableSymbol != null){
                    VariableSymbol variableSymbol1 = new VariableSymbol(curVar.Identifier(), variableSymbol.typeContainer);
                    scope.define(variableSymbol1);
                }else {
                    VariableSymbol incompleteTypeSymbol = new VariableSymbol(curSpec.Identifier(), new TypeContainer(curSpec.Identifier(), new TypeContainer()));
                    scope.define(incompleteTypeSymbol);
                    VariableSymbol variableSymbol1 = new VariableSymbol(curVar.Identifier(), incompleteTypeSymbol.typeContainer);
                    scope.define(variableSymbol1);
                }
                continue;
            }
            if(curSpec.STRUCT() != null && curSpec.LBRACE() != null){
                Scope childScope = new Scope(curScope, curScope.grader);
                StructType structType = new StructType(curSpec.Identifier(), curSpec.specifier(), curSpec.varDec(), childScope);
                VariableSymbol variableSymbol = new VariableSymbol(curVar.Identifier(), structType);
                scope.define(variableSymbol);
            }
        }
    }
    @Override
    public String prettyPrint(){
        // For structure types, prettyPrint only prints the tag name per spec: "struct T"
        return "struct " + identifier.getText();
    }
    @Override
    public String fullPrint(){
        // Full print includes member list: struct T{T1 M1;...;Tn Mn;}
        StringBuilder sb = new StringBuilder("struct ").append(identifier.getText()).append("{");
        if(scope != null){
            for(VariableSymbol sym : scope.declaredSymbols()){
                sb.append(sym.typeContainer.prettyPrint())
                  .append(" ")
                  .append(sym.identifier.getText())
                  .append(";");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
// A reference to a previously declared struct, prints as "struct <name>"
class StructRefType implements Type{
    TerminalNode identifier;
    boolean defined; // whether this tag is fully defined somewhere visible
    StructRefType(TerminalNode identifier){
        this.identifier = identifier;
    }
    StructRefType(TerminalNode identifier, boolean defined){
        this.identifier = identifier;
        this.defined = defined;
    }
    @Override
    public String prettyPrint(){
        return "struct " + identifier.getText();
    }
}
class PointerType implements Type{
    Type type;
    PointerType(Type type){
        this.type = type;
    }
    @Override
    public String prettyPrint() {
        return this.type.prettyPrint()+"*";
    }
}

class TypeContainer implements Type{
    TerminalNode identifier;
    Type type;
    boolean incomplete = true;
    public TypeContainer(){}
    public TypeContainer(TerminalNode identifier, Type type){
        this.identifier = identifier;
        this.type = type;
        this.incomplete = false;
    }
    public TypeContainer(SplcParser.VarDecContext declarator, Type type){
        TypeContainer typeContainer;
        //System.out.println(declarator.getText());
        //System.out.println(declarator.LPAREN() != null);
        //System.out.println(type.toString());
        if(declarator.Identifier() != null){
            typeContainer = new TypeContainer(declarator.Identifier(), type);
            this.identifier = typeContainer.identifier;
            this.type = typeContainer;
            return;
        }
        if(declarator.LPAREN() != null){
            typeContainer = new TypeContainer(declarator.varDec(), type);
            this.identifier = typeContainer.identifier;
            this.type = typeContainer;
            return;
        }
        if(declarator.STAR() != null){
            PointerType pointerType = new PointerType(type);
            typeContainer = new TypeContainer(declarator.varDec(), pointerType);
            this.identifier = typeContainer.identifier;
            this.type = typeContainer;
            return;
        }
        if(declarator.LBRACK() != null){
            if(declarator.varDec().LPAREN() != null){
                ArrayType arrayType = new ArrayType(type, Integer.parseInt(declarator.Number().getText()));
                typeContainer = new TypeContainer(declarator.varDec(), arrayType);
                this.identifier = typeContainer.identifier;
                this.type = typeContainer;
                return;
            }
            TypeContainer tmp = new TypeContainer(declarator.varDec(), type);
            ArrayType arrayType = new ArrayType(tmp.type, Integer.parseInt(declarator.Number().getText()));
            typeContainer = new TypeContainer(tmp.identifier, arrayType);
            this.identifier = typeContainer.identifier;
            this.type = typeContainer;
            return;
        }
    }
    @Override
    public String prettyPrint(){
        return type.prettyPrint();
    }
}

class VariableSymbol {
    TerminalNode identifier;
    TypeContainer typeContainer;
    public VariableSymbol(TerminalNode identifier, Type type){
        TypeContainer typeContainer = new TypeContainer(identifier, type);
        this.identifier = identifier;
        this.typeContainer = typeContainer;
    }
    public VariableSymbol(TerminalNode identifier, TypeContainer typeContainer){
        this.identifier = identifier;
        this.typeContainer = typeContainer;
    }
    public VariableSymbol(SplcParser.VarDecContext declarator, Type type) {
        this.typeContainer = new TypeContainer(declarator, type);
        this.identifier = this.typeContainer.identifier;
    }

    public String prettyPrint() {
        // 解开 TypeContainer 包装，若顶层有效类型为 StructType（非派生），使用 fullPrint
        Type t = typeContainer.type;
        while(t instanceof TypeContainer tc){
            t = tc.type;
        }
        String rendered;
        if(t instanceof StructType st){
            rendered = st.fullPrint();
        } else {
            rendered = typeContainer.prettyPrint();
        }
        return identifier.getText()+ ": " + rendered;
    }
    @Override
    public String toString(){
        return prettyPrint();
    }
}


class FunctionSymbol {
    TerminalNode identifier;
    Type returnType;
    List<TypeContainer> params = new ArrayList<>();
    boolean hasBody = false;

    public FunctionSymbol(TerminalNode identifier, Type returnType){
        this.identifier = identifier;
        this.returnType = returnType;
    }

    public String prettyPrint(){
        StringBuilder sb = new StringBuilder();
        sb.append(identifier.getText()).append(": ").append(returnType.prettyPrint()).append("(");
        for (int i = 0; i < params.size(); i++){
            if(i > 0) sb.append(',');
            sb.append(params.get(i).prettyPrint());
        }
        sb.append(")");
        return sb.toString();
    }
    @Override
    public String toString(){
        return prettyPrint();
    }
}

class Scope{
    // Symbols table
    LinkedHashMap<TerminalNode, VariableSymbol> symbols = new LinkedHashMap<>();
    AbstractGrader grader;

    // Parent Scope
    Scope parent;
    //Child Scope;
    ArrayList<Scope> children = new ArrayList<>();

    public Scope(Scope parent, AbstractGrader grader) {
        this.parent = parent;
        this.grader = grader;
        if(parent != null){
            parent.children.add(this);
        }
    }

    public void define(VariableSymbol var){
        if(symbols.containsKey(var.identifier)){
            grader.reportSemanticError(Project3SemanticError.redefinition(var.identifier));
        }
        symbols.put(var.identifier, var);
    }

    public VariableSymbol lookup(TerminalNode identifier){
        VariableSymbol sym = symbols.get(identifier);
        if (sym != null) return sym;
        if (parent != null) return parent.lookup(identifier);
        return null;
    }

    Iterable<VariableSymbol> declaredSymbols() {
        return symbols.values();
    }
}

public class Compiler extends AbstractCompiler {
    public Compiler(AbstractGrader grader) {
        super(grader);
    }

    // Helper to build a Type from a specifier node
    static Type getTypeFromSpecifier(SplcParser.SpecifierContext ctx, Scope curScope){
        if(ctx.INT() != null){
            return new IntType();
        }
        if(ctx.CHAR() != null){
            return new CharType();
        }
        if(ctx.STRUCT() != null){
            if(ctx.LBRACE() != null){
                // struct definition with fields
                Scope childScope = new Scope(curScope, curScope.grader);
                return new StructType(ctx.Identifier(), childScope);
            }else{
                // reference to existing struct
                return new StructRefType(ctx.Identifier());
            }
        }
        // Fallback, should not happen
        return new PrimitiveType(){};
    }

    @Override
    public void start() throws IOException {
        CharStream input = CharStreams.fromStream(this.grader.getSourceStream());
        SplcLexer lexer = new SplcLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SplcParser parser = new SplcParser(tokens);

        // TODO: XXX
        SplcParser.ProgramContext program = parser.program();

        // Collections for final printing
        List<VariableSymbol> globalVariables = new ArrayList<>();
        List<FunctionSymbol> functions = new ArrayList<>();
        // Name maps for semantic checks at global scope
        LinkedHashMap<String, VariableSymbol> globalVarMap = new LinkedHashMap<>();
        LinkedHashMap<String, FunctionSymbol> globalFuncMap = new LinkedHashMap<>();

        new SplcBaseVisitor<Void>() {
            Scope scope = new Scope(null, grader);
            // function-local scope stack (top is current block)
            ArrayList<LinkedHashMap<String, VariableSymbol>> varScopeStack = new ArrayList<>();
            // struct tag namespace scope stack
            static class TagInfo {
                boolean defined;
                TerminalNode token;
                StructType structType; // 若已定义，指向对应的 StructType 实例
                TagInfo(boolean defined, TerminalNode token){ this.defined = defined; this.token = token; }
                TagInfo(boolean defined, TerminalNode token, StructType structType){ this.defined = defined; this.token = token; this.structType = structType; }
            }
            ArrayList<LinkedHashMap<String, TagInfo>> tagScopeStack = new ArrayList<>(){{ add(new LinkedHashMap<>()); }};
            // Track tags currently being defined (complete definitions) to catch redeclaration inside member lists
            HashSet<String> definingTags = new HashSet<>();
            // Defer global non-array incomplete struct value checks until end of translation unit
            ArrayList<StructRefType> pendingGlobalIncompletes = new ArrayList<>();

            private void pushVarScope(){
                 varScopeStack.add(new LinkedHashMap<>()); 
            }

            private void popVarScope(){ 
                if(!varScopeStack.isEmpty()) varScopeStack.remove(varScopeStack.size()-1); 
            }
            
            private LinkedHashMap<String, VariableSymbol> currentVarScope(){
                return varScopeStack.isEmpty()? null : varScopeStack.get(varScopeStack.size()-1); 
            }

            private void pushTagScope(){ tagScopeStack.add(new LinkedHashMap<>()); }
            private void popTagScope(){ if(!tagScopeStack.isEmpty()) tagScopeStack.remove(tagScopeStack.size()-1); }
            private LinkedHashMap<String, TagInfo> currentTagScope(){ return tagScopeStack.get(tagScopeStack.size()-1); }
            private TagInfo lookupTag(String name){
                for(int i=tagScopeStack.size()-1;i>=0;i--){
                    TagInfo info = tagScopeStack.get(i).get(name);
                    if(info!=null) return info;
                }
                return null;
            }

            private VariableSymbol lookupVarByName(String name){
                for(int i = varScopeStack.size()-1; i >= 0; i--){
                    VariableSymbol vs = varScopeStack.get(i).get(name);
                    if(vs!=null) return vs;
                }
                return globalVarMap.get(name);
            }

            private void declareLocalVarOrError(VariableSymbol var){
                LinkedHashMap<String, VariableSymbol> cur = currentVarScope();
                if(cur == null) return; // not inside body
                String name = var.identifier.getText();
                if(cur.containsKey(name)){
                    grader.reportSemanticError(Project3SemanticError.redefinition(var.identifier));
                }
                cur.put(name, var);
            }

            private void checkUndeclaredIdentifierUse(TerminalNode identifier){
                String name = identifier.getText();
                // Treat function identifiers as declared as well
                if(lookupVarByName(name) == null && !globalFuncMap.containsKey(name)){
                    grader.reportSemanticError(Project3SemanticError.undeclaredUse(identifier));
                }
            }

            private void walkExpr(SplcParser.ExpressionContext ctx){
                if(ctx == null) return;
                if(ctx.Identifier() != null && ctx.getChildCount()==1){
                    checkUndeclaredIdentifierUse(ctx.Identifier());
                }
                try{
                    if(ctx.Identifier() != null && ctx.LPAREN() != null){
                        checkUndeclaredIdentifierUse(ctx.Identifier());
                    }
                }catch(Throwable ignored){}
                for(SplcParser.ExpressionContext sub : ctx.expression()){
                    walkExpr(sub);
                }
            }

            @Override
            public Void visitGlobalDef(SplcParser.GlobalDefContext ctx){
                SplcParser.SpecifierContext spec = ctx.specifier();
                Type baseType = makeType(spec);
                if(ctx.Identifier() != null){
                    /* specifier Identifier LPAREN funcArgs RPAREN LBRACE statement* RBRACE */
                    FunctionSymbol fun = new FunctionSymbol(ctx.Identifier(), baseType);
                    SplcParser.FuncArgsContext fa = ctx.funcArgs();
                    if(fa != null){
                        List<SplcParser.SpecifierContext> specs = fa.specifier();
                        List<SplcParser.VarDecContext> vds = fa.varDec();
                        LinkedHashMap<String, Boolean> seenParams = new LinkedHashMap<>();
                        for(int i = 0; i < specs.size() && i < vds.size(); i++){
                            Type pBase = makeType(specs.get(i));
                            TypeContainer pType = new TypeContainer(vds.get(i), pBase);
                            if(pType.identifier != null){
                                String pname = pType.identifier.getText();
                                if(seenParams.containsKey(pname)){
                                    grader.reportSemanticError(Project3SemanticError.redefinition(pType.identifier));
                                }
                                seenParams.put(pname, true);
                            }
                            fun.params.add(pType);
                        }
                    }
                    String name = ctx.Identifier().getText();
                    boolean hasBody = ctx.LBRACE()!=null;
                    fun.hasBody = hasBody;
                    FunctionSymbol existed = globalFuncMap.get(name);
                    if(existed != null){
                        if(existed.hasBody && !hasBody){
                            grader.reportSemanticError(Project3SemanticError.redeclaration(ctx.Identifier()));
                        }
                        if(existed.hasBody && hasBody){
                            grader.reportSemanticError(Project3SemanticError.redefinition(ctx.Identifier()));
                        }
                        if(!existed.hasBody && hasBody){
                            existed.hasBody = true;
                            existed.returnType = fun.returnType;
                            existed.params = fun.params;
                        }
                        if(!existed.hasBody && !hasBody){
                            grader.reportSemanticError(Project3SemanticError.redeclaration(ctx.Identifier()));
                        }
                    } else {
                        if(globalVarMap.containsKey(name)){
                            if(hasBody){
                                grader.reportSemanticError(Project3SemanticError.redefinition(ctx.Identifier()));
                            } else {
                                grader.reportSemanticError(Project3SemanticError.redeclaration(ctx.Identifier()));
                            }
                        }
                        globalFuncMap.put(name, fun);
                        functions.add(fun);
                    }

                    if(ctx.LBRACE()!=null){
                        pushVarScope();
                        pushTagScope();
                        // parameters
                        if(fa!=null){
                            List<SplcParser.VarDecContext> vds = fa.varDec();
                            List<SplcParser.SpecifierContext> specs = fa.specifier();
                            for(int i=0;i<vds.size() && i<specs.size();i++){
                                Type pBase = makeType(specs.get(i));
                                VariableSymbol paramSym = new VariableSymbol(vds.get(i), pBase);
                                declareLocalVarOrError(paramSym);
                            }
                        }
                        for(SplcParser.StatementContext st : ctx.statement()){ visit(st); }
                        popTagScope();
                        popVarScope();
                    }
                }else if(ctx.varDec()!=null){
                    VariableSymbol var = new VariableSymbol(ctx.varDec(), baseType);
                    String name = var.identifier.getText();
                    if(globalVarMap.containsKey(name)){
                        grader.reportSemanticError(Project3SemanticError.redefinition(var.identifier));
                    }
                    if(globalFuncMap.containsKey(name)){
                        grader.reportSemanticError(Project3SemanticError.redeclaration(var.identifier));
                    }
                    // Handle incomplete struct rules:
                    //  - Arrays: element type must be complete immediately (cannot defer)
                    //  - Non-array struct value: may defer until full file (might be completed later)
                    StructRefType arrayInc = findFirstIncompleteStructRefInArray(var.typeContainer.type);
                    if(arrayInc != null){
                        grader.reportSemanticError(Project3SemanticError.definitionIncomplete(arrayInc.identifier));
                    } else {
                        StructRefType inc = findFirstIncompleteStructRef(var.typeContainer.type);
                        if(inc != null){
                            pendingGlobalIncompletes.add(inc); // defer
                        }
                    }
                    globalVarMap.put(name, var);
                    globalVariables.add(var);
                }else{
                    // specifier SEMI (e.g., struct forward declaration or definition-only)
                    if(spec != null && spec.STRUCT()!=null && spec.LBRACE()==null){
                        String tag = spec.Identifier().getText();
                        currentTagScope().putIfAbsent(tag, new TagInfo(false, spec.Identifier()));
                    }
                }
                return null;
            }

            @Override
            public Void visitCodeBlock(SplcParser.CodeBlockContext ctx){
                pushVarScope();
                pushTagScope();
                for(SplcParser.StatementContext st : ctx.statement()){ visit(st); }
                popTagScope();
                popVarScope();
                return null;
            }
            @Override
            public Void visitVarDecStmt(SplcParser.VarDecStmtContext ctx){
                Type base = makeType(ctx.specifier());
                VariableSymbol var = new VariableSymbol(ctx.varDec(), base);
                declareLocalVarOrError(var);
                StructRefType inc = findFirstIncompleteStructRef(var.typeContainer.type);
                if(inc != null){
                    grader.reportSemanticError(Project3SemanticError.definitionIncomplete(inc.identifier));
                }
                if(ctx.expression()!=null) walkExpr(ctx.expression());
                return null;
            }
            @Override
            public Void visitIfStmt(SplcParser.IfStmtContext ctx){
                walkExpr(ctx.expression());
                visit(ctx.statement(0));
                if(ctx.statement().size()>1) visit(ctx.statement(1));
                return null;
            }
            @Override
            public Void visitWhileStmt(SplcParser.WhileStmtContext ctx){
                walkExpr(ctx.expression());
                visit(ctx.statement());
                return null;
            }
            @Override
            public Void visitReturnStmt(SplcParser.ReturnStmtContext ctx){
                walkExpr(ctx.expression());
                return null;
            }
            @Override
            public Void visitExprStmt(SplcParser.ExprStmtContext ctx){
                walkExpr(ctx.expression());
                return null;
            }

            // Build Type with struct tag namespace & member checks
            private Type makeType(SplcParser.SpecifierContext ctx){
                //System.out.println(ctx.getText());
                if(ctx == null) return new PrimitiveType(){};
                if(ctx.INT()!=null) return new IntType();
                if(ctx.CHAR()!=null) return new CharType();
                if(ctx.STRUCT()!=null){
                    TerminalNode tag = ctx.Identifier();
                    if(ctx.LBRACE()!=null){
                        // struct definition in current scope
                        LinkedHashMap<String, TagInfo> cur = currentTagScope();
                        TagInfo exist = cur.get(tag.getText());
                        // Redeclaration cases:
                        if((exist!=null && exist.defined) || definingTags.contains(tag.getText())){
                            grader.reportSemanticError(Project3SemanticError.redeclaration(tag));
                        }
                        cur.put(tag.getText(), new TagInfo(false, tag));
                        definingTags.add(tag.getText());

                        Scope fieldScope = new Scope(scope, grader);
                        StructType st = new StructType(tag, fieldScope);
                        // build members
                        List<SplcParser.SpecifierContext> specs = ctx.specifier();
                        List<SplcParser.VarDecContext> vds = ctx.varDec();
                        LinkedHashMap<String, Boolean> memberNames = new LinkedHashMap<>();
                        for(int i=0;i<specs.size() && i<vds.size();i++){
                            SplcParser.VarDecContext vd = vds.get(i);
                            Type mBase = makeType(specs.get(i));
                            VariableSymbol vs = new VariableSymbol(vd, mBase);
                            TerminalNode memId = vs.typeContainer.identifier;
                            if(memId==null) continue;
                            String memName = memId.getText();
                            if(memberNames.containsKey(memName)){
                                grader.reportSemanticError(Project3SemanticError.memberDuplicate(memId));
                            }
                            // member incomplete (non-pointer value of incomplete struct)
                            StructRefType inc = findFirstIncompleteStructRef(vs.typeContainer.type);
                            if(inc != null){
                                grader.reportSemanticError(Project3SemanticError.memberIncomplete(memId));
                            }
                            fieldScope.symbols.put(vs.identifier, vs);
                            memberNames.put(memName, true);
                        }
                        // finish definition
                        cur.put(tag.getText(), new TagInfo(true, tag, st));
                        definingTags.remove(tag.getText());
                        return st;
                    } else {
                        TagInfo info = lookupTag(tag.getText());
                        if(info != null && info.defined && info.structType != null){
                            // 已定义的结构体，返回完整 StructType 以便后续 fullPrint
                            return info.structType;
                        }
                        boolean defined = info!=null && info.defined;
                        return new StructRefType(tag, defined);
                    }
                }
                return new PrimitiveType(){};
            }

            // Find first incomplete struct reference in a value type (pointer exempt, arrays recurse)
            private StructRefType findFirstIncompleteStructRef(Type type){
                if(type instanceof TypeContainer tc){
                    return findFirstIncompleteStructRef(tc.type);
                }
                if(type instanceof PointerType){
                    return null; // pointer allowed
                }
                if(type instanceof ArrayType at){
                    return findFirstIncompleteStructRef(at.type);
                }
                if(type instanceof StructRefType srt){
                    if(!srt.defined) return srt;
                    return null;
                }
                if(type instanceof StructType){
                    return null; // complete type
                }
                return null;
            }
            // Specifically detect incomplete struct element type occurring anywhere under an ArrayType layer.
            private StructRefType findFirstIncompleteStructRefInArray(Type type){
                return findIncompleteUnderArray(type, false);
            }
            private StructRefType findIncompleteUnderArray(Type type, boolean inArray){
                if(type instanceof TypeContainer tc){
                    return findIncompleteUnderArray(tc.type, inArray);
                }
                if(type instanceof PointerType){
                    return null; // pointer element allowed
                }
                if(type instanceof ArrayType at){
                    // Entering array context
                    return findIncompleteUnderArray(at.type, true);
                }
                if(inArray && type instanceof StructRefType srt){
                    return (!srt.defined) ? srt : null;
                }
                return null;
            }
            @Override
            public Void visitProgram(SplcParser.ProgramContext ctx){
                super.visitProgram(ctx);
                // After full traversal, check deferred global incomplete struct values
                for(StructRefType srt : pendingGlobalIncompletes){
                    TagInfo info = lookupTag(srt.identifier.getText());
                    if(info == null || !info.defined){
                        grader.reportSemanticError(Project3SemanticError.definitionIncomplete(srt.identifier));
                    }
                }
                // Upgrade global variable types from StructRefType to final StructType for pretty print
                for(VariableSymbol gv : globalVariables){
                    upgradeToCompleteIfAvailable(gv.typeContainer);
                }
                return null;
            }
            private void upgradeToCompleteIfAvailable(TypeContainer tc){
                if(tc == null) return;
                tc.type = upgradeType(tc.type);
            }
            private Type upgradeType(Type type){
                if(type instanceof TypeContainer inner){
                    inner.type = upgradeType(inner.type);
                    return inner;
                }
                if(type instanceof PointerType){
                    return type; // do not upgrade inside pointer
                }
                if(type instanceof ArrayType){
                    return type; // arrays should already be complete at this point
                }
                if(type instanceof StructRefType srt){
                    TagInfo info = lookupTag(srt.identifier.getText());
                    if(info != null && info.defined && info.structType != null){
                        return info.structType;
                    }
                }
                return type;
            }
        }.visit(program);

        grader.print("Variables:\n");
        for(VariableSymbol v : globalVariables){
            grader.print(v.prettyPrint()+"\n");
        }

        grader.print("\n");

        grader.print("Functions:\n");
        for(FunctionSymbol f : functions){
            grader.print(f.prettyPrint()+"\n");
        }
    }
}
