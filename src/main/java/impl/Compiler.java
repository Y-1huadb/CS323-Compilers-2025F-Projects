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
import java.util.Map;

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
    LinkedHashMap<String, Type> symbols;
    StructType(String name, LinkedHashMap<String, Type> symbols){
        this.name = name;
        this.symbols = symbols;
    }
    StructType(String name, ArrayList<SplcParser.VarDecContext> vars){
        this.name = name;
        for (SplcParser.VarDecContext var : vars){
            Type tmp;
//            switch (var.Identifier()){
//
//            }
//            symbols.put(var.Identifier().getText(), tmp);
        }
    }
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, Type> entry : symbols.entrySet()){
            stringBuilder.append(entry.getValue().toString()).append(" ").append(entry.getKey()).append(";");
        }
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

class VariableSymbol {
    String name;
    Type type;

    public VariableSymbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name + ": " + type;
    }
}

class Scope{
    // Symbols table
    LinkedHashMap<String, VariableSymbol> symbols = new LinkedHashMap<>();

    // Parent Scope
    Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
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
