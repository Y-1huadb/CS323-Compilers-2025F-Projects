package impl;

import framework.project4.Project4SemanticError;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;

public class ExprVisitor extends SplcBaseVisitor<Void> {
    @Override
    public Void visitExpression(SplcParser.ExpressionContext ctx) {
        if(ctx.Identifier() != null) {
            String id = ctx.Identifier().getText();
            Project4SemanticError.identifierNotVariable(ctx, id).throwException();
        }
        //TODO: Visit Expression
        return null;
    }
}
