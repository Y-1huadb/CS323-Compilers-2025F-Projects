package impl.project2;

import generated.Splc.SplcBaseVisitor;
import generated.Splc.SplcParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ConstExprVisitor extends SplcBaseVisitor<Integer> {

    @Override
    public Integer visitExpression(SplcParser.ExpressionContext expression) {

        Integer acc = null;        // 累积值
        String  pendingOp = null;  // 等待应用的二元运算符: "+","-","*","/","%"
        boolean lastWasValue = false; // 上一个消费的是“值”
        int sign = 1;              // 一元 +/? 的累计符号

        int n = expression.getChildCount();
        for (int i = 0; i < n; ++i) {
            ParseTree c = expression.getChild(i);
            String tmp = c.getText();

            // 子表达式：递归取值（括号优先级天然通过子树体现）
            if (c instanceof SplcParser.ExpressionContext) {
                Integer v = visit(c);
                if (v == null) return null;   // 子树不是常量 → 整体非常量
                v = sign * v;                  // 应用一元 +/-
                sign = 1;

                if (acc == null) {
                    acc = v;
                } else {
                    if (pendingOp == null) return null; // 语法意外
                    acc = apply(acc, pendingOp, v);
                    pendingOp = null;
                }
                lastWasValue = true;
                continue;
            }

            // 终结符
            if (c instanceof TerminalNode tn) {
                Token tok = tn.getSymbol();
                String text = tn.getText();
                int type = tok.getType();

                // 纯数字字面量
                if (type == SplcParser.Number) {
                    Integer v = Integer.parseInt(text);
                    v = sign * v; sign = 1;
                    if (acc == null) acc = v;
                    else {
                        if (pendingOp == null) return null;
                        acc = apply(acc, pendingOp, v);
                        pendingOp = null;
                    }
                    lastWasValue = true;
                    continue;
                }
                // 允许的运算符：+ / -（一元或二元）
                if ("+".equals(text) || "-".equals(text)) {
                    if (!lastWasValue) {
                        // 一元 +/-
                        if ("-".equals(text)) sign = -sign; // 可连用：--x 变 +x
                    } else {
                        // 二元 +/-
                        if (pendingOp != null) return null;
                        pendingOp = text;
                        lastWasValue = false;
                    }
                    continue;
                }

                if ("(".equals(text) || ")".equals(text)) {
                    continue;
                }

                // 允许的运算符：* / %（只允许二元；一元 * 视作解引用 → 非常量）
                if ("*".equals(text) || "/".equals(text) || "%".equals(text)) {
                    if (!lastWasValue) return null; // 一元 * / % 不在“常量表达式”定义里
                    if (pendingOp != null) return null;
                    pendingOp = text;
                    lastWasValue = false;
                    continue;
                }

                if (type == SplcParser.Identifier
                        || ".".equals(text) || "->".equals(text)
                        || "[".equals(text) || "]".equals(text)
                        || "++".equals(text) || "--".equals(text)
                        || "!".equals(text) || "&".equals(text)
                        || "&&".equals(text) || "||".equals(text)
                        || "<".equals(text) || "<=".equals(text)
                        || ">".equals(text) || ">=".equals(text)
                        || "==".equals(text) || "!=".equals(text)
                        || "=".equals(text)) {
                    return null;
                }

                return null;
            }

            return null;
        }

        if (pendingOp != null) return null;
        return acc;
    }

    private static Integer apply(Integer a, String op, Integer b) {
        switch (op) {
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("/ by zero");
                return a / b; // 向0截断
            case "%":
                if (b == 0) throw new ArithmeticException("% by zero");
                return a % b;
            case "+": return a + b;
            case "-": return a - b;
            default:  return null;
        }
    }
}

