// Generated from C:/Users/sunyy/Desktop/SUSTECH/±‡“Î‘≠¿Ì/CS323-Compilers-2025F-Projects/Splc.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SplcParser}.
 */
public interface SplcListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SplcParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SplcParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SplcParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SplcParser#globalDef}.
	 * @param ctx the parse tree
	 */
	void enterGlobalDef(SplcParser.GlobalDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#globalDef}.
	 * @param ctx the parse tree
	 */
	void exitGlobalDef(SplcParser.GlobalDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SplcParser#specifier}.
	 * @param ctx the parse tree
	 */
	void enterSpecifier(SplcParser.SpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#specifier}.
	 * @param ctx the parse tree
	 */
	void exitSpecifier(SplcParser.SpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SplcParser#varDec}.
	 * @param ctx the parse tree
	 */
	void enterVarDec(SplcParser.VarDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#varDec}.
	 * @param ctx the parse tree
	 */
	void exitVarDec(SplcParser.VarDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link SplcParser#funcArgs}.
	 * @param ctx the parse tree
	 */
	void enterFuncArgs(SplcParser.FuncArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#funcArgs}.
	 * @param ctx the parse tree
	 */
	void exitFuncArgs(SplcParser.FuncArgsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CodeBlock}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCodeBlock(SplcParser.CodeBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CodeBlock}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCodeBlock(SplcParser.CodeBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarDecStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDecStmt(SplcParser.VarDecStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDecStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDecStmt(SplcParser.VarDecStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(SplcParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(SplcParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(SplcParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(SplcParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(SplcParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(SplcParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(SplcParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(SplcParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SplcParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(SplcParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SplcParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(SplcParser.ExpressionContext ctx);
}