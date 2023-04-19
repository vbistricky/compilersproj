import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Driver {

    public static void main(String[] args) throws Exception {

        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(System.in);

        // create a lexer that feeds off of input CharStream
        LittleLexer lexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        LittleParser parser = new LittleParser(tokens);

        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();

        SymbolTableBuilder stb = new SymbolTableBuilder();

        walker.walk(stb, tree);

        // stb.prettyPrint();

        stb.printIR();

    }
}
