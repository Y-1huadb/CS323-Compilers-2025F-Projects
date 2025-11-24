package impl;

import framework.project4.Project4SemanticError;
import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;

public class ExprVisitor extends SplcBaseVisitor<Void> {
    @Override
    public Void visitExpression(SplcParser.ExpressionContext ctx) {
        String id = ctx.Identifier().getText();
        //TODO: Visit Expression
        Project4SemanticError.identifierNotVariable(ctx, id).throwException();
        return null;
    }
}
