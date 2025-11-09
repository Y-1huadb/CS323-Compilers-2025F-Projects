package impl;

import framework.AbstractCompiler;
import framework.AbstractGrader;
import framework.project3.Project3SemanticError;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcLexer;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

interface Type {
    String toString();
}
abstract class PrimitiveType implements Type{
    @Override
    public String toString(){
        return "Primitive Type";
    }

}
class IntType extends PrimitiveType{
    @Override
    public String toString(){
        return "int";
    }
}
class CharType extends PrimitiveType{
    @Override
    public String toString(){
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
    public String toString(){
        return this.type.toString() + "["+this.length+"]";
    }
}
class StructType implements Type{
    String name;
    Scope scope;
    StructType(String name, Scope curScope){
        this.name = name;
        this.scope = curScope;
    }
    StructType(String name, List<SplcParser.SpecifierContext> specs, List<SplcParser.VarDecContext> vars, Scope curScope){
        this.name = name;
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
                VariableSymbol variableSymbol = scope.lookup(curSpec.Identifier().getText());
                if(variableSymbol != null){
                    VariableSymbol variableSymbol1 = new VariableSymbol(curVar.Identifier().getText(), variableSymbol.typeContainer);
                    scope.define(variableSymbol1);
                }else {
                    VariableSymbol incompleteTypeSymbol = new VariableSymbol(curSpec.Identifier().getText(), new TypeContainer(curSpec.Identifier().getText(), new TypeContainer()));
                    scope.define(incompleteTypeSymbol);
                    VariableSymbol variableSymbol1 = new VariableSymbol(curVar.Identifier().getText(), incompleteTypeSymbol.typeContainer);
                    scope.define(variableSymbol1);
                }
                continue;
            }
            if(curSpec.STRUCT() != null && curSpec.LBRACE() != null){
                Scope childScope = new Scope(curScope);
                StructType structType = new StructType(curSpec.Identifier().getText(), curSpec.specifier(), curSpec.varDec(), childScope);
                VariableSymbol variableSymbol = new VariableSymbol(curVar.Identifier().getText(), structType);
                scope.define(variableSymbol);
            }
        }
    }
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("struct ").append(name).append("{");
        for (VariableSymbol sym : scope.declaredSymbols()) {
            stringBuilder.append(sym.typeContainer.toString()).append(" ").append(sym.name).append(";");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
class PointerType implements Type{
    Type type;
    PointerType(Type type){
        this.type = type;
    }
    @Override
    public String toString() {
        return this.type.toString()+"*";
    }
}

class TypeContainer implements Type{
    String name;
    Type type;
    boolean incomplete = true;
    public TypeContainer(){}
    public TypeContainer(String name, Type type){
        this.name = name;
        this.type = type;
        this.incomplete = false;
    }
    public TypeContainer(SplcParser.VarDecContext declarator, Type type){
        TypeContainer typeContainer;
        if(declarator.Identifier() != null){
            typeContainer = new TypeContainer(declarator.Identifier().getText(), type);
            this.name = typeContainer.name;
            this.type = typeContainer;
            return;
        }
        if(declarator.LBRACK() != null){
            TypeContainer tmp = new TypeContainer(declarator.varDec(), type);
            ArrayType arrayType = new ArrayType(tmp.type, Integer.parseInt(declarator.Number().getText()));
            typeContainer = new TypeContainer(tmp.name, arrayType);
            this.name = typeContainer.name;
            this.type = typeContainer;
            return;
        }
        if(declarator.STAR() != null){
            TypeContainer tmp = new TypeContainer(declarator.varDec(), type);
            PointerType pointerType = new PointerType(tmp.type);
            typeContainer = new TypeContainer(tmp.name, pointerType);
            this.name = typeContainer.name;
            this.type = typeContainer;
            return;
        }
        if(declarator.LPAREN() != null){
            typeContainer = new TypeContainer(declarator.varDec(), type);
            this.name = typeContainer.name;
            this.type = typeContainer;
            return;
        }
    }
    @Override
    public String toString(){
        return type.toString();
    }
}

class VariableSymbol {
    String name;
    TypeContainer typeContainer;
    public VariableSymbol(String name, Type type){
        TypeContainer typeContainer = new TypeContainer(name, type);
        this.name = name;
        this.typeContainer = typeContainer;
    }
    public VariableSymbol(String name, TypeContainer typeContainer){
        this.name = name;
        this.typeContainer = typeContainer;
    }
    public VariableSymbol(SplcParser.VarDecContext declarator, Type type) {
        this.typeContainer = new TypeContainer(declarator, type);
        this.name = this.typeContainer.name;
    }

    @Override
    public String toString() {
        return name + ": " + typeContainer.toString();
    }
}


class Scope{
    // Symbols table
    LinkedHashMap<String, VariableSymbol> symbols = new LinkedHashMap<>();

    // Parent Scope
    Scope parent;
    //Child Scope;
    ArrayList<Scope> children = new ArrayList<>();

    public Scope(Scope parent) {
        this.parent = parent;
        if(parent != null){
            parent.children.add(this);
        }
    }

    public boolean define(VariableSymbol var){
        if(symbols.containsKey(var.name)){
            return false;
        }
        symbols.put(var.name, var);
        return true;
    }

    public VariableSymbol lookup(String name){
        VariableSymbol sym = symbols.get(name);
        if (sym != null) return sym;
        if (parent != null) return parent.lookup(name);
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

    @Override
    public void start() throws IOException {
        CharStream input = CharStreams.fromStream(this.grader.getSourceStream());
        SplcLexer lexer = new SplcLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SplcParser parser = new SplcParser(tokens);

        // TODO: XXX
        SplcParser.ProgramContext program = parser.program();

        new SplcBaseVisitor<Void>() {
            // These are merely examples to show how to create and report a Semantic Error.
            // The Alternative name (ExprID, VarDecBase, etc.) used here may not be the same as yours.
            // So it's fine that this code won't compile. You are free to delete all of these code.
            Type curType;
            VariableSymbol curSymbol;
            Scope scope = new Scope(null);
            @Override
            public Void visitGlobalDef(SplcParser.GlobalDefContext ctx){
                var spec = ctx.specifier();
//                System.out.println(spec.getText());
                visitSpecifier(spec);
                String res = spec.getText();
                if(ctx.Identifier() != null){
                    /*
                    specifier Identifier LPAREN funcArgs RPAREN LBRACE statement* RBRACE
                    | specifier Identifier LPAREN funcArgs RPAREN SEMI
                     */
                    //TODO
                }else if(ctx.varDec() != null){
                    //TODO
                    /* specifier varDec SEMI */
                    System.out.println(1);


                }else{
                    //TODO
                    /* specifier SEMI  */
                }
//                visitChildren(ctx);
                return null;
            }
            @Override
            public Void visitSpecifier(SplcParser.SpecifierContext ctx){
                if(ctx.INT() != null){
                    curType = new IntType();
                    return null;
                }
                if(ctx.CHAR() != null){
                    curType = new CharType();
                    return null;
                }
                var vars = ctx.varDec();
                if(vars.isEmpty()){
                    var ident = ctx.Identifier();
                    curSymbol = scope.lookup(ident.getText());
                    if (curSymbol == null){
//                        grader.reportSemanticError(Project3SemanticError.undeclaredUse(ident));
                    }
                    return null;
                }else {
                    System.out.println(vars.getFirst().getClass().getName());
                    return null;
                }
            }
            @Override
            public Void visitVarDec(SplcParser.VarDecContext ctx){
                System.out.println(2);
                return null;
            }
        }.visit(program);

        grader.print("Variables:\n");


        grader.print("\n");

        grader.print("Functions:\n");
    }
}
