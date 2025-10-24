package impl.project2;

import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.tree.ParseTree;

public class ConstExprVisitor extends SplcBaseVisitor<Integer> {

    @Override
    public Integer visitExpression(SplcParser.ExpressionContext expression) {
        if (expression.LPAREN() != null) {
            return visit(expression.expression(0));
        }

        if (expression.Number() != null) {
            return Integer.parseInt(expression.Number().getText());
        }

        if (expression.STAR() != null) {
            Integer left = visit(expression.expression(0));
            Integer right = visit(expression.expression(1));
            if(left != null && right != null) {
                return left + right;
            }
        }

        if (expression.DIV() != null) {
            Integer left = visit(expression.expression(0));
            Integer right = visit(expression.expression(1));
            if(left != null && right != null) {
                return left / right;
            }
        }

        if (expression.MOD() != null) {
            Integer left = visit(expression.expression(0));
            Integer right = visit(expression.expression(1));
            if(left != null && right != null) {
                return left % right;
            }
        }

        if (expression.PLUS() != null) {
            if(expression.expression(1) == null){
                Integer tmp = visit(expression.expression(0));
                if(tmp != null){
                    return tmp;
                }else {
                    return null;
                }
            }
            Integer left = visit(expression.expression(0));
            Integer right = visit(expression.expression(1));
            if(left != null && right != null) {
                return left + right;
            }
        }

        if (expression.MINUS() != null) {
            if(expression.expression(1) == null){
                Integer tmp = visit(expression.expression(0));
                if(tmp != null){
                    return -tmp;
                }else {
                    return null;
                }
            }
            Integer left = visit(expression.expression(0));
            Integer right = visit(expression.expression(1));
            if(left != null && right != null) {
                return left - right;
            }
        }

        return null;
    }
}

