package uva;

/**
 * 
 * Given a number of parentheses, prints all permutations 
 * 
 * numParens = 3
 * ((()))
 * (()())
 * (())()
 * ()(())
 * ()()()
 *
 */
public class PermuteParens {

    public static void main(String[] args) throws Exception {
        int numParen = 3;
        printParen(numParen);
    }

    public static void printParen(int numParen) {
        printParen(0, numParen, numParen, "");
    }

    private static void printParen(int counter, int numOpen, int numClose,
            String result) {
        assert (counter >= 0);
        assert (numOpen >= 0);
        assert (numClose >= 0);
        assert (numOpen + counter == numClose);

        if (counter == 0 && numOpen == 0 && numClose == 0) {
            System.out.println(result);
            return;
        }
        if (counter == 0 && numOpen > 0) { // numOpen == numClose
            printParen(counter + 1, numOpen - 1, numClose, result + '(');
            return;
        }

        if (counter > 0) {
            if (numOpen > 0) {
                printParen(counter + 1, numOpen - 1, numClose, result + '(');
            }
            if (numClose > 0) {
                printParen(counter - 1, numOpen, numClose - 1, result + ')');
            }
        }
    }
}
