// Generated from /home/kwong/projects/fooledit/modules/mode.ada/Ada.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AdaParser}.
 */
public interface AdaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AdaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(AdaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link AdaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(AdaParser.ProgramContext ctx);
}