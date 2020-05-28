package demo.antlr;

import demo.antlr.generated.expression.ExpressionBaseVisitor;
import demo.antlr.generated.expression.ExpressionLexer;
import demo.antlr.generated.expression.ExpressionParser;
import demo.antlr.generated.hello.HelloBaseListener;
import demo.antlr.generated.hello.HelloLexer;
import demo.antlr.generated.hello.HelloParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AntlrDemo {

    public static void main(String[] args) throws Exception {
//        generateHelloParser();
//        testHelloParser();
//        testHelloListener();

//        generateExpressionParser();
//        testExpressionVisitor();
    }

    private static void generateHelloParser() {
        String cwd = System.getProperty("user.dir");
        String inFile = Paths.get(cwd, "etc/antlr/Hello.g4").toAbsolutePath().toString();
        String outdir = Paths.get(cwd, "src/main/java/demo/antlr/generated/hello").toAbsolutePath().toString();
        String[] args = {"-Werror", "-visitor", "-package", "demo.antlr.generated.hello", "-o", outdir, inFile};
        org.antlr.v4.Tool.main(args);
    }

    /**
     * Test rule r
     * Type input, ^D to end
     */
    private static void testHelloParser() throws Exception {
        String[] args = {"demo.antlr.generated.hello.Hello", "r", "-tokens", "-tree", "-gui"};
        org.antlr.v4.gui.TestRig.main(args);
    }

    private static void testHelloListener() {
        String in = "hello johndoe";
        ANTLRInputStream is = new ANTLRInputStream(in);
        HelloLexer lexer = new HelloLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(tokens);
        ParseTree tree = parser.r();

        ParseTreeWalker walker = new ParseTreeWalker();
        HelloListener helloListener = new HelloListener();
        walker.walk(helloListener, tree);
        System.out.println(helloListener.getName());
    }

    private static void generateExpressionParser() {
        String cwd = System.getProperty("user.dir");
        String inFile = Paths.get(cwd, "etc/antlr/Expression.g4").toAbsolutePath().toString();
        String outdir = Paths.get(cwd, "src/main/java/demo/antlr/generated/expression").toAbsolutePath().toString();
        String[] args = {"-Werror", "-visitor", "-package", "demo.antlr.generated.expression", "-o", outdir, inFile};
        org.antlr.v4.Tool.main(args);
    }

    private static void testExpressionVisitor() {
        int exprResult = compute("1+2+3", ExpressionParser::expr);
        System.out.println(exprResult);

        compute("2+3+4\n", ExpressionParser::stat);
        compute("a=3+4+5\nb=a*2\nb\n", ExpressionParser::prog);
    }

    private static int compute(String expr, Function<ExpressionParser, ParseTree> fn) {
        ANTLRInputStream is = new ANTLRInputStream(expr);
        ExpressionLexer lexer = new ExpressionLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpressionParser parser = new ExpressionParser(tokens);
        ParseTree tree = fn.apply(parser);

        ExpressionCalculator calculator = new ExpressionCalculator();
        return calculator.visit(tree);
    }

    private static class ExpressionCalculator extends ExpressionBaseVisitor<Integer> {
        Map<String, Integer> memory = new HashMap<>();

        @Override
        public Integer visitAssign(ExpressionParser.AssignContext ctx) {
            String id = ctx.ID().getText();
            int value = visit(ctx.expr());
            memory.put(id, value);
            return value;
        }

        @Override
        public Integer visitPrintExpr(ExpressionParser.PrintExprContext ctx) {
            Integer value = visit(ctx.expr());
            System.out.println(value);
            return 0;
        }

        @Override
        public Integer visitInt(ExpressionParser.IntContext ctx) {
            return Integer.valueOf(ctx.INT().getText());
        }

        @Override
        public Integer visitId(ExpressionParser.IdContext ctx) {
            String id = ctx.ID().getText();
            if (memory.containsKey(id)) return memory.get(id);
            return 0;
        }

        @Override
        public Integer visitMulDiv(ExpressionParser.MulDivContext ctx) {
            int left = visit(ctx.expr(0));
            int right = visit(ctx.expr(1));
            if (ctx.op.getType() == ExpressionParser.MUL) return left * right;
            return left / right;
        }

        @Override
        public Integer visitAddSub(ExpressionParser.AddSubContext ctx) {
            int left = visit(ctx.expr(0));
            int right = visit(ctx.expr(1));
            if (ctx.op.getType() == ExpressionParser.ADD) return left + right;
            return left - right;
        }

        @Override
        public Integer visitParens(ExpressionParser.ParensContext ctx) {
            return visit(ctx.expr());
        }
    }

    private static class HelloListener extends HelloBaseListener {
        private String name;

        public String getName() {
            return name;
        }

        public void enterR(HelloParser.RContext ctx) {
            name = ctx.ID().getSymbol().getText();
        }
    }
}
