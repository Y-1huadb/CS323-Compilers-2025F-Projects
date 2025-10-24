package impl.project2;

import framework.project2.Grader;
import framework.project2.MissingSymbolError;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class Project2ErrorListener extends BaseErrorListener {
    private final Grader grader;
    public Project2ErrorListener(Grader grader) {
        this.grader = grader;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        // 仅在“缺失符号”场景下处理（ANTLR 默认文案以 "missing " 开头）
        boolean isMissing = msg != null && msg.startsWith("missing ");

        // 解析期望的 token（不使用 IntervalSet）：直接从默认错误消息中提取
        String symbolName = "";
        int reportLineZeroIndexed = Math.max(0, line - 1); // fallback，如果找不到 prev token

        if (recognizer instanceof Parser) {
            Parser parser = (Parser) recognizer;
            Vocabulary vocab = parser.getVocabulary();
            // 从 msg 中提取 missing 后的 token 文本，例如：missing ';' at ... 或 missing RPAREN at ...
            String expectedText = null;
            if (isMissing) {
                final String prefix = "missing ";
                int start = msg.indexOf(prefix);
                if (start >= 0) {
                    start += prefix.length();
                    int atIdx = msg.indexOf(" at", start);
                    if (atIdx > start) {
                        expectedText = msg.substring(start, atIdx).trim();
                    }
                }
            }

            if (expectedText != null && !expectedText.isEmpty()) {
                // 如果是字面量（以单引号包裹），通过 literalName -> symbolicName 的映射找到符号名
                if (expectedText.startsWith("'") && expectedText.endsWith("'") && expectedText.length() >= 2) {
                    int maxType = parser.getATN().maxTokenType;
                    for (int t = 0; t <= maxType; t++) {
                        String lit = vocab.getLiteralName(t);
                        if (expectedText.equals(lit)) {
                            String sym = vocab.getSymbolicName(t);
                            if (sym == null) sym = vocab.getDisplayName(t);
                            symbolName = sym != null ? sym : "";
                            break;
                        }
                    }
                } else {
                    // 否则，直接认为它已经是一个符号名（如 RPAREN, SEMI 等）
                    symbolName = expectedText;
                }
            }

            // 2) 依据“前一个有效 token”的行号（不包含 skip 的词法符号），0-based
            CommonTokenStream tokenStream = (parser.getInputStream() instanceof CommonTokenStream)
                ? (CommonTokenStream) parser.getInputStream()
                : null;

            Token offending = (offendingSymbol instanceof Token)
                ? (Token) offendingSymbol
                : parser.getCurrentToken();

            if (tokenStream != null && offending != null) {
                int idx = offending.getTokenIndex() - 1; // 前一个 token
                while (idx >= 0) {
                    Token prev = tokenStream.get(idx);
                    if (prev == null) break;
                    if (prev.getChannel() == Token.DEFAULT_CHANNEL) {
                        int prevLine = prev.getLine();
                        reportLineZeroIndexed = Math.max(0, prevLine - 1);
                        break;
                    }
                    idx--;
                }
            }
        }

        // 在项目约束下，仅输出 Missing Symbol 的错误
        if (isMissing && symbolName != null && !symbolName.isEmpty()) {
            MissingSymbolError missingSymbol = new MissingSymbolError(symbolName, reportLineZeroIndexed);
            this.grader.getWriter().println(missingSymbol);
        }

        // 按要求：仅报告一次并终止后续解析
        throw new ParseCancellationException();
    }
}
