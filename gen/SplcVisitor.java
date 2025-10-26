// Generated from C:/Users/sunyy/Desktop/SUSTECH/±‡“Î‘≠¿Ì/CS323-Compilers-2025F-Projects/Splc.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SplcParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SplcVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SplcParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SplcParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SplcParser#globalDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalDef(SplcParser.GlobalDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SplcParser#specifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecifier(SplcParser.SpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SplcParser#varDec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDec(SplcParser.VarDecContext ctx);
	/**
	 * Visit a parse tree produced by {@link SplcParser#funcArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncArgs(SplcParser.FuncArgsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CodeBlock}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCodeBlock(SplcParser.CodeBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarDecStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecStmt(SplcParser.VarDecStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(SplcParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(SplcParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(SplcParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SplcParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(SplcParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SplcParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(SplcParser.ExpressionContext ctx);
}