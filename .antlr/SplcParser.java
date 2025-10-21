// Generated from d:/github/CS323-Compilers-2025F-Projects/Splc.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SplcParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INT=1, CHAR=2, STRUCT=3, RETURN=4, IF=5, ELSE=6, WHILE=7, ASSIGN=8, PLUS=9, 
		MINUS=10, STAR=11, DIV=12, MOD=13, LT=14, LE=15, GT=16, GE=17, EQ=18, 
		NEQ=19, AND=20, OR=21, NOT=22, INC=23, DEC=24, DOT=25, ARROW=26, AMP=27, 
		SEMI=28, COMMA=29, LPAREN=30, RPAREN=31, LBRACE=32, RBRACE=33, LBRACK=34, 
		RBRACK=35, Identifier=36, Number=37, Char=38, WS=39, LINE_COMMENT=40, 
		BLOCK_COMMENT=41;
	public static final int
		RULE_program = 0, RULE_globalDef = 1, RULE_specifier = 2, RULE_varDec = 3, 
		RULE_funcArgs = 4, RULE_statement = 5, RULE_expression = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "globalDef", "specifier", "varDec", "funcArgs", "statement", 
			"expression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'int'", "'char'", "'struct'", "'return'", "'if'", "'else'", "'while'", 
			"'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'<'", "'<='", "'>'", "'>='", 
			"'=='", "'!='", "'&&'", "'||'", "'!'", "'++'", "'--'", "'.'", "'->'", 
			"'&'", "';'", "','", "'('", "')'", "'{'", "'}'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INT", "CHAR", "STRUCT", "RETURN", "IF", "ELSE", "WHILE", "ASSIGN", 
			"PLUS", "MINUS", "STAR", "DIV", "MOD", "LT", "LE", "GT", "GE", "EQ", 
			"NEQ", "AND", "OR", "NOT", "INC", "DEC", "DOT", "ARROW", "AMP", "SEMI", 
			"COMMA", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACK", "RBRACK", 
			"Identifier", "Number", "Char", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Splc.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SplcParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SplcParser.EOF, 0); }
		public List<GlobalDefContext> globalDef() {
			return getRuleContexts(GlobalDefContext.class);
		}
		public GlobalDefContext globalDef(int i) {
			return getRuleContext(GlobalDefContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 14L) != 0)) {
				{
				{
				setState(14);
				globalDef();
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(20);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GlobalDefContext extends ParserRuleContext {
		public SpecifierContext specifier() {
			return getRuleContext(SpecifierContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(SplcParser.Identifier, 0); }
		public TerminalNode LPAREN() { return getToken(SplcParser.LPAREN, 0); }
		public FuncArgsContext funcArgs() {
			return getRuleContext(FuncArgsContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SplcParser.RPAREN, 0); }
		public TerminalNode LBRACE() { return getToken(SplcParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(SplcParser.RBRACE, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public VarDecContext varDec() {
			return getRuleContext(VarDecContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SplcParser.SEMI, 0); }
		public GlobalDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_globalDef; }
	}

	public final GlobalDefContext globalDef() throws RecognitionException {
		GlobalDefContext _localctx = new GlobalDefContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_globalDef);
		int _la;
		try {
			setState(43);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(22);
				specifier();
				setState(23);
				match(Identifier);
				setState(24);
				match(LPAREN);
				setState(25);
				funcArgs();
				setState(26);
				match(RPAREN);
				setState(27);
				match(LBRACE);
				setState(31);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 486568627902L) != 0)) {
					{
					{
					setState(28);
					statement();
					}
					}
					setState(33);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(34);
				match(RBRACE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(36);
				specifier();
				setState(37);
				varDec(0);
				setState(38);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(40);
				specifier();
				setState(41);
				match(SEMI);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SpecifierContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SplcParser.INT, 0); }
		public TerminalNode CHAR() { return getToken(SplcParser.CHAR, 0); }
		public TerminalNode STRUCT() { return getToken(SplcParser.STRUCT, 0); }
		public TerminalNode Identifier() { return getToken(SplcParser.Identifier, 0); }
		public TerminalNode LBRACE() { return getToken(SplcParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(SplcParser.RBRACE, 0); }
		public List<SpecifierContext> specifier() {
			return getRuleContexts(SpecifierContext.class);
		}
		public SpecifierContext specifier(int i) {
			return getRuleContext(SpecifierContext.class,i);
		}
		public List<VarDecContext> varDec() {
			return getRuleContexts(VarDecContext.class);
		}
		public VarDecContext varDec(int i) {
			return getRuleContext(VarDecContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(SplcParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(SplcParser.SEMI, i);
		}
		public SpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specifier; }
	}

	public final SpecifierContext specifier() throws RecognitionException {
		SpecifierContext _localctx = new SpecifierContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_specifier);
		int _la;
		try {
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(45);
				match(INT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(46);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(47);
				match(STRUCT);
				setState(48);
				match(Identifier);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(49);
				match(STRUCT);
				setState(50);
				match(Identifier);
				setState(51);
				match(LBRACE);
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 14L) != 0)) {
					{
					{
					setState(52);
					specifier();
					setState(53);
					varDec(0);
					setState(54);
					match(SEMI);
					}
					}
					setState(60);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(61);
				match(RBRACE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarDecContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(SplcParser.Identifier, 0); }
		public TerminalNode STAR() { return getToken(SplcParser.STAR, 0); }
		public VarDecContext varDec() {
			return getRuleContext(VarDecContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(SplcParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SplcParser.RPAREN, 0); }
		public TerminalNode LBRACK() { return getToken(SplcParser.LBRACK, 0); }
		public TerminalNode Number() { return getToken(SplcParser.Number, 0); }
		public TerminalNode RBRACK() { return getToken(SplcParser.RBRACK, 0); }
		public VarDecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDec; }
	}

	public final VarDecContext varDec() throws RecognitionException {
		return varDec(0);
	}

	private VarDecContext varDec(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		VarDecContext _localctx = new VarDecContext(_ctx, _parentState);
		VarDecContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_varDec, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				{
				setState(65);
				match(Identifier);
				}
				break;
			case STAR:
				{
				setState(66);
				match(STAR);
				setState(67);
				varDec(2);
				}
				break;
			case LPAREN:
				{
				setState(68);
				match(LPAREN);
				setState(69);
				varDec(0);
				setState(70);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(80);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new VarDecContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_varDec);
					setState(74);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(75);
					match(LBRACK);
					setState(76);
					match(Number);
					setState(77);
					match(RBRACK);
					}
					} 
				}
				setState(82);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncArgsContext extends ParserRuleContext {
		public List<SpecifierContext> specifier() {
			return getRuleContexts(SpecifierContext.class);
		}
		public SpecifierContext specifier(int i) {
			return getRuleContext(SpecifierContext.class,i);
		}
		public List<VarDecContext> varDec() {
			return getRuleContexts(VarDecContext.class);
		}
		public VarDecContext varDec(int i) {
			return getRuleContext(VarDecContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SplcParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SplcParser.COMMA, i);
		}
		public FuncArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcArgs; }
	}

	public final FuncArgsContext funcArgs() throws RecognitionException {
		FuncArgsContext _localctx = new FuncArgsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_funcArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 14L) != 0)) {
				{
				setState(83);
				specifier();
				setState(84);
				varDec(0);
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(85);
					match(COMMA);
					setState(86);
					specifier();
					setState(87);
					varDec(0);
					}
					}
					setState(93);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfStmtContext extends StatementContext {
		public TerminalNode IF() { return getToken(SplcParser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(SplcParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SplcParser.RPAREN, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(SplcParser.ELSE, 0); }
		public IfStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExprStmtContext extends StatementContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SplcParser.SEMI, 0); }
		public ExprStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WhileStmtContext extends StatementContext {
		public TerminalNode WHILE() { return getToken(SplcParser.WHILE, 0); }
		public TerminalNode LPAREN() { return getToken(SplcParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SplcParser.RPAREN, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CodeBlockContext extends StatementContext {
		public TerminalNode LBRACE() { return getToken(SplcParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(SplcParser.RBRACE, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public CodeBlockContext(StatementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VarDecStmtContext extends StatementContext {
		public SpecifierContext specifier() {
			return getRuleContext(SpecifierContext.class,0);
		}
		public VarDecContext varDec() {
			return getRuleContext(VarDecContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SplcParser.SEMI, 0); }
		public TerminalNode ASSIGN() { return getToken(SplcParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VarDecStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStmtContext extends StatementContext {
		public TerminalNode RETURN() { return getToken(SplcParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SplcParser.SEMI, 0); }
		public ReturnStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		int _la;
		try {
			setState(134);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACE:
				_localctx = new CodeBlockContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				match(LBRACE);
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 486568627902L) != 0)) {
					{
					{
					setState(97);
					statement();
					}
					}
					setState(102);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(103);
				match(RBRACE);
				}
				break;
			case INT:
			case CHAR:
			case STRUCT:
				_localctx = new VarDecStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(104);
				specifier();
				setState(105);
				varDec(0);
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(106);
					match(ASSIGN);
					setState(107);
					expression(0);
					}
				}

				setState(110);
				match(SEMI);
				}
				break;
			case IF:
				_localctx = new IfStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(112);
				match(IF);
				setState(113);
				match(LPAREN);
				setState(114);
				expression(0);
				setState(115);
				match(RPAREN);
				setState(116);
				statement();
				setState(119);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(117);
					match(ELSE);
					setState(118);
					statement();
					}
					break;
				}
				}
				break;
			case WHILE:
				_localctx = new WhileStmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(121);
				match(WHILE);
				setState(122);
				match(LPAREN);
				setState(123);
				expression(0);
				setState(124);
				match(RPAREN);
				setState(125);
				statement();
				}
				break;
			case RETURN:
				_localctx = new ReturnStmtContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(127);
				match(RETURN);
				setState(128);
				expression(0);
				setState(129);
				match(SEMI);
				}
				break;
			case PLUS:
			case MINUS:
			case STAR:
			case NOT:
			case INC:
			case DEC:
			case AMP:
			case LPAREN:
			case Identifier:
			case Number:
			case Char:
				_localctx = new ExprStmtContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(131);
				expression(0);
				setState(132);
				match(SEMI);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SplcParser.LPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(SplcParser.RPAREN, 0); }
		public TerminalNode Number() { return getToken(SplcParser.Number, 0); }
		public TerminalNode Char() { return getToken(SplcParser.Char, 0); }
		public TerminalNode Identifier() { return getToken(SplcParser.Identifier, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SplcParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SplcParser.COMMA, i);
		}
		public TerminalNode INC() { return getToken(SplcParser.INC, 0); }
		public TerminalNode DEC() { return getToken(SplcParser.DEC, 0); }
		public TerminalNode PLUS() { return getToken(SplcParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SplcParser.MINUS, 0); }
		public TerminalNode NOT() { return getToken(SplcParser.NOT, 0); }
		public TerminalNode STAR() { return getToken(SplcParser.STAR, 0); }
		public TerminalNode AMP() { return getToken(SplcParser.AMP, 0); }
		public TerminalNode DIV() { return getToken(SplcParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(SplcParser.MOD, 0); }
		public TerminalNode LT() { return getToken(SplcParser.LT, 0); }
		public TerminalNode LE() { return getToken(SplcParser.LE, 0); }
		public TerminalNode GT() { return getToken(SplcParser.GT, 0); }
		public TerminalNode GE() { return getToken(SplcParser.GE, 0); }
		public TerminalNode EQ() { return getToken(SplcParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(SplcParser.NEQ, 0); }
		public TerminalNode AND() { return getToken(SplcParser.AND, 0); }
		public TerminalNode OR() { return getToken(SplcParser.OR, 0); }
		public TerminalNode ASSIGN() { return getToken(SplcParser.ASSIGN, 0); }
		public TerminalNode LBRACK() { return getToken(SplcParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(SplcParser.RBRACK, 0); }
		public TerminalNode DOT() { return getToken(SplcParser.DOT, 0); }
		public TerminalNode ARROW() { return getToken(SplcParser.ARROW, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(137);
				match(LPAREN);
				setState(138);
				expression(0);
				setState(139);
				match(RPAREN);
				}
				break;
			case 2:
				{
				setState(141);
				_la = _input.LA(1);
				if ( !(_la==Number || _la==Char) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 3:
				{
				setState(142);
				match(Identifier);
				}
				break;
			case 4:
				{
				setState(143);
				match(Identifier);
				setState(144);
				match(LPAREN);
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 482273660416L) != 0)) {
					{
					setState(145);
					expression(0);
					setState(150);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(146);
						match(COMMA);
						setState(147);
						expression(0);
						}
						}
						setState(152);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(155);
				match(RPAREN);
				}
				break;
			case 5:
				{
				setState(156);
				_la = _input.LA(1);
				if ( !(_la==INC || _la==DEC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(157);
				expression(13);
				}
				break;
			case 6:
				{
				setState(158);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(159);
				expression(12);
				}
				break;
			case 7:
				{
				setState(160);
				match(NOT);
				setState(161);
				expression(11);
				}
				break;
			case 8:
				{
				setState(162);
				match(STAR);
				setState(163);
				expression(10);
				}
				break;
			case 9:
				{
				setState(164);
				match(AMP);
				setState(165);
				expression(9);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(207);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(205);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(168);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(169);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 14336L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(170);
						expression(9);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(171);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(172);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(173);
						expression(8);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(174);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(175);
						_la = _input.LA(1);
						if ( !(_la==LT || _la==LE) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(176);
						expression(7);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(177);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(178);
						_la = _input.LA(1);
						if ( !(_la==GT || _la==GE) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(179);
						expression(6);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(180);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(181);
						_la = _input.LA(1);
						if ( !(_la==EQ || _la==NEQ) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(182);
						expression(5);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(183);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(184);
						match(AND);
						setState(185);
						expression(4);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(186);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(187);
						match(OR);
						setState(188);
						expression(3);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(189);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(190);
						match(ASSIGN);
						setState(191);
						expression(1);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(192);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(193);
						_la = _input.LA(1);
						if ( !(_la==INC || _la==DEC) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(194);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(195);
						match(LBRACK);
						setState(196);
						expression(0);
						setState(197);
						match(RBRACK);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(199);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(200);
						match(DOT);
						setState(201);
						match(Identifier);
						}
						break;
					case 12:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(202);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(203);
						match(ARROW);
						setState(204);
						match(Identifier);
						}
						break;
					}
					} 
				}
				setState(209);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return varDec_sempred((VarDecContext)_localctx, predIndex);
		case 6:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean varDec_sempred(VarDecContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		case 8:
			return precpred(_ctx, 1);
		case 9:
			return precpred(_ctx, 18);
		case 10:
			return precpred(_ctx, 16);
		case 11:
			return precpred(_ctx, 15);
		case 12:
			return precpred(_ctx, 14);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001)\u00d3\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0005\u0000\u0010"+
		"\b\u0000\n\u0000\f\u0000\u0013\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0005\u0001\u001e\b\u0001\n\u0001\f\u0001!\t\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0003\u0001,\b\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0005\u00029\b\u0002\n\u0002\f\u0002<\t"+
		"\u0002\u0001\u0002\u0003\u0002?\b\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003I\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005"+
		"\u0003O\b\u0003\n\u0003\f\u0003R\t\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004Z\b\u0004\n\u0004"+
		"\f\u0004]\t\u0004\u0003\u0004_\b\u0004\u0001\u0005\u0001\u0005\u0005\u0005"+
		"c\b\u0005\n\u0005\f\u0005f\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0003\u0005m\b\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005x\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u0087\b\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0005\u0006\u0095\b\u0006\n\u0006\f\u0006\u0098\t\u0006\u0003\u0006\u009a"+
		"\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003"+
		"\u0006\u00a7\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00ce\b\u0006\n\u0006\f\u0006"+
		"\u00d1\t\u0006\u0001\u0006\u0000\u0002\u0006\f\u0007\u0000\u0002\u0004"+
		"\u0006\b\n\f\u0000\u0007\u0001\u0000%&\u0001\u0000\u0017\u0018\u0001\u0000"+
		"\t\n\u0001\u0000\u000b\r\u0001\u0000\u000e\u000f\u0001\u0000\u0010\u0011"+
		"\u0001\u0000\u0012\u0013\u00f6\u0000\u0011\u0001\u0000\u0000\u0000\u0002"+
		"+\u0001\u0000\u0000\u0000\u0004>\u0001\u0000\u0000\u0000\u0006H\u0001"+
		"\u0000\u0000\u0000\b^\u0001\u0000\u0000\u0000\n\u0086\u0001\u0000\u0000"+
		"\u0000\f\u00a6\u0001\u0000\u0000\u0000\u000e\u0010\u0003\u0002\u0001\u0000"+
		"\u000f\u000e\u0001\u0000\u0000\u0000\u0010\u0013\u0001\u0000\u0000\u0000"+
		"\u0011\u000f\u0001\u0000\u0000\u0000\u0011\u0012\u0001\u0000\u0000\u0000"+
		"\u0012\u0014\u0001\u0000\u0000\u0000\u0013\u0011\u0001\u0000\u0000\u0000"+
		"\u0014\u0015\u0005\u0000\u0000\u0001\u0015\u0001\u0001\u0000\u0000\u0000"+
		"\u0016\u0017\u0003\u0004\u0002\u0000\u0017\u0018\u0005$\u0000\u0000\u0018"+
		"\u0019\u0005\u001e\u0000\u0000\u0019\u001a\u0003\b\u0004\u0000\u001a\u001b"+
		"\u0005\u001f\u0000\u0000\u001b\u001f\u0005 \u0000\u0000\u001c\u001e\u0003"+
		"\n\u0005\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001e!\u0001\u0000"+
		"\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000"+
		"\u0000 \"\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000\"#\u0005"+
		"!\u0000\u0000#,\u0001\u0000\u0000\u0000$%\u0003\u0004\u0002\u0000%&\u0003"+
		"\u0006\u0003\u0000&\'\u0005\u001c\u0000\u0000\',\u0001\u0000\u0000\u0000"+
		"()\u0003\u0004\u0002\u0000)*\u0005\u001c\u0000\u0000*,\u0001\u0000\u0000"+
		"\u0000+\u0016\u0001\u0000\u0000\u0000+$\u0001\u0000\u0000\u0000+(\u0001"+
		"\u0000\u0000\u0000,\u0003\u0001\u0000\u0000\u0000-?\u0005\u0001\u0000"+
		"\u0000.?\u0005\u0002\u0000\u0000/0\u0005\u0003\u0000\u00000?\u0005$\u0000"+
		"\u000012\u0005\u0003\u0000\u000023\u0005$\u0000\u00003:\u0005 \u0000\u0000"+
		"45\u0003\u0004\u0002\u000056\u0003\u0006\u0003\u000067\u0005\u001c\u0000"+
		"\u000079\u0001\u0000\u0000\u000084\u0001\u0000\u0000\u00009<\u0001\u0000"+
		"\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;=\u0001"+
		"\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000=?\u0005!\u0000\u0000>-\u0001"+
		"\u0000\u0000\u0000>.\u0001\u0000\u0000\u0000>/\u0001\u0000\u0000\u0000"+
		">1\u0001\u0000\u0000\u0000?\u0005\u0001\u0000\u0000\u0000@A\u0006\u0003"+
		"\uffff\uffff\u0000AI\u0005$\u0000\u0000BC\u0005\u000b\u0000\u0000CI\u0003"+
		"\u0006\u0003\u0002DE\u0005\u001e\u0000\u0000EF\u0003\u0006\u0003\u0000"+
		"FG\u0005\u001f\u0000\u0000GI\u0001\u0000\u0000\u0000H@\u0001\u0000\u0000"+
		"\u0000HB\u0001\u0000\u0000\u0000HD\u0001\u0000\u0000\u0000IP\u0001\u0000"+
		"\u0000\u0000JK\n\u0003\u0000\u0000KL\u0005\"\u0000\u0000LM\u0005%\u0000"+
		"\u0000MO\u0005#\u0000\u0000NJ\u0001\u0000\u0000\u0000OR\u0001\u0000\u0000"+
		"\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000Q\u0007\u0001"+
		"\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000ST\u0003\u0004\u0002\u0000"+
		"T[\u0003\u0006\u0003\u0000UV\u0005\u001d\u0000\u0000VW\u0003\u0004\u0002"+
		"\u0000WX\u0003\u0006\u0003\u0000XZ\u0001\u0000\u0000\u0000YU\u0001\u0000"+
		"\u0000\u0000Z]\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000"+
		"^S\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_\t\u0001\u0000\u0000"+
		"\u0000`d\u0005 \u0000\u0000ac\u0003\n\u0005\u0000ba\u0001\u0000\u0000"+
		"\u0000cf\u0001\u0000\u0000\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000"+
		"\u0000\u0000eg\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000g\u0087"+
		"\u0005!\u0000\u0000hi\u0003\u0004\u0002\u0000il\u0003\u0006\u0003\u0000"+
		"jk\u0005\b\u0000\u0000km\u0003\f\u0006\u0000lj\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0005\u001c\u0000"+
		"\u0000o\u0087\u0001\u0000\u0000\u0000pq\u0005\u0005\u0000\u0000qr\u0005"+
		"\u001e\u0000\u0000rs\u0003\f\u0006\u0000st\u0005\u001f\u0000\u0000tw\u0003"+
		"\n\u0005\u0000uv\u0005\u0006\u0000\u0000vx\u0003\n\u0005\u0000wu\u0001"+
		"\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x\u0087\u0001\u0000\u0000"+
		"\u0000yz\u0005\u0007\u0000\u0000z{\u0005\u001e\u0000\u0000{|\u0003\f\u0006"+
		"\u0000|}\u0005\u001f\u0000\u0000}~\u0003\n\u0005\u0000~\u0087\u0001\u0000"+
		"\u0000\u0000\u007f\u0080\u0005\u0004\u0000\u0000\u0080\u0081\u0003\f\u0006"+
		"\u0000\u0081\u0082\u0005\u001c\u0000\u0000\u0082\u0087\u0001\u0000\u0000"+
		"\u0000\u0083\u0084\u0003\f\u0006\u0000\u0084\u0085\u0005\u001c\u0000\u0000"+
		"\u0085\u0087\u0001\u0000\u0000\u0000\u0086`\u0001\u0000\u0000\u0000\u0086"+
		"h\u0001\u0000\u0000\u0000\u0086p\u0001\u0000\u0000\u0000\u0086y\u0001"+
		"\u0000\u0000\u0000\u0086\u007f\u0001\u0000\u0000\u0000\u0086\u0083\u0001"+
		"\u0000\u0000\u0000\u0087\u000b\u0001\u0000\u0000\u0000\u0088\u0089\u0006"+
		"\u0006\uffff\uffff\u0000\u0089\u008a\u0005\u001e\u0000\u0000\u008a\u008b"+
		"\u0003\f\u0006\u0000\u008b\u008c\u0005\u001f\u0000\u0000\u008c\u00a7\u0001"+
		"\u0000\u0000\u0000\u008d\u00a7\u0007\u0000\u0000\u0000\u008e\u00a7\u0005"+
		"$\u0000\u0000\u008f\u0090\u0005$\u0000\u0000\u0090\u0099\u0005\u001e\u0000"+
		"\u0000\u0091\u0096\u0003\f\u0006\u0000\u0092\u0093\u0005\u001d\u0000\u0000"+
		"\u0093\u0095\u0003\f\u0006\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0095"+
		"\u0098\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0096"+
		"\u0097\u0001\u0000\u0000\u0000\u0097\u009a\u0001\u0000\u0000\u0000\u0098"+
		"\u0096\u0001\u0000\u0000\u0000\u0099\u0091\u0001\u0000\u0000\u0000\u0099"+
		"\u009a\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b"+
		"\u00a7\u0005\u001f\u0000\u0000\u009c\u009d\u0007\u0001\u0000\u0000\u009d"+
		"\u00a7\u0003\f\u0006\r\u009e\u009f\u0007\u0002\u0000\u0000\u009f\u00a7"+
		"\u0003\f\u0006\f\u00a0\u00a1\u0005\u0016\u0000\u0000\u00a1\u00a7\u0003"+
		"\f\u0006\u000b\u00a2\u00a3\u0005\u000b\u0000\u0000\u00a3\u00a7\u0003\f"+
		"\u0006\n\u00a4\u00a5\u0005\u001b\u0000\u0000\u00a5\u00a7\u0003\f\u0006"+
		"\t\u00a6\u0088\u0001\u0000\u0000\u0000\u00a6\u008d\u0001\u0000\u0000\u0000"+
		"\u00a6\u008e\u0001\u0000\u0000\u0000\u00a6\u008f\u0001\u0000\u0000\u0000"+
		"\u00a6\u009c\u0001\u0000\u0000\u0000\u00a6\u009e\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a0\u0001\u0000\u0000\u0000\u00a6\u00a2\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a7\u00cf\u0001\u0000\u0000\u0000"+
		"\u00a8\u00a9\n\b\u0000\u0000\u00a9\u00aa\u0007\u0003\u0000\u0000\u00aa"+
		"\u00ce\u0003\f\u0006\t\u00ab\u00ac\n\u0007\u0000\u0000\u00ac\u00ad\u0007"+
		"\u0002\u0000\u0000\u00ad\u00ce\u0003\f\u0006\b\u00ae\u00af\n\u0006\u0000"+
		"\u0000\u00af\u00b0\u0007\u0004\u0000\u0000\u00b0\u00ce\u0003\f\u0006\u0007"+
		"\u00b1\u00b2\n\u0005\u0000\u0000\u00b2\u00b3\u0007\u0005\u0000\u0000\u00b3"+
		"\u00ce\u0003\f\u0006\u0006\u00b4\u00b5\n\u0004\u0000\u0000\u00b5\u00b6"+
		"\u0007\u0006\u0000\u0000\u00b6\u00ce\u0003\f\u0006\u0005\u00b7\u00b8\n"+
		"\u0003\u0000\u0000\u00b8\u00b9\u0005\u0014\u0000\u0000\u00b9\u00ce\u0003"+
		"\f\u0006\u0004\u00ba\u00bb\n\u0002\u0000\u0000\u00bb\u00bc\u0005\u0015"+
		"\u0000\u0000\u00bc\u00ce\u0003\f\u0006\u0003\u00bd\u00be\n\u0001\u0000"+
		"\u0000\u00be\u00bf\u0005\b\u0000\u0000\u00bf\u00ce\u0003\f\u0006\u0001"+
		"\u00c0\u00c1\n\u0012\u0000\u0000\u00c1\u00ce\u0007\u0001\u0000\u0000\u00c2"+
		"\u00c3\n\u0010\u0000\u0000\u00c3\u00c4\u0005\"\u0000\u0000\u00c4\u00c5"+
		"\u0003\f\u0006\u0000\u00c5\u00c6\u0005#\u0000\u0000\u00c6\u00ce\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c8\n\u000f\u0000\u0000\u00c8\u00c9\u0005\u0019"+
		"\u0000\u0000\u00c9\u00ce\u0005$\u0000\u0000\u00ca\u00cb\n\u000e\u0000"+
		"\u0000\u00cb\u00cc\u0005\u001a\u0000\u0000\u00cc\u00ce\u0005$\u0000\u0000"+
		"\u00cd\u00a8\u0001\u0000\u0000\u0000\u00cd\u00ab\u0001\u0000\u0000\u0000"+
		"\u00cd\u00ae\u0001\u0000\u0000\u0000\u00cd\u00b1\u0001\u0000\u0000\u0000"+
		"\u00cd\u00b4\u0001\u0000\u0000\u0000\u00cd\u00b7\u0001\u0000\u0000\u0000"+
		"\u00cd\u00ba\u0001\u0000\u0000\u0000\u00cd\u00bd\u0001\u0000\u0000\u0000"+
		"\u00cd\u00c0\u0001\u0000\u0000\u0000\u00cd\u00c2\u0001\u0000\u0000\u0000"+
		"\u00cd\u00c7\u0001\u0000\u0000\u0000\u00cd\u00ca\u0001\u0000\u0000\u0000"+
		"\u00ce\u00d1\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000"+
		"\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\r\u0001\u0000\u0000\u0000\u00d1"+
		"\u00cf\u0001\u0000\u0000\u0000\u0012\u0011\u001f+:>HP[^dlw\u0086\u0096"+
		"\u0099\u00a6\u00cd\u00cf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}