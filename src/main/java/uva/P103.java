package uva;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

class P103 {

    int[][] boxes;
    int[][] matrix;
    Node[] nodes;

    public P103(int numBoxes, int dims) {
        boxes = new int[numBoxes][dims];
        matrix = new int[numBoxes][numBoxes];
        nodes = new Node[numBoxes];
    }

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null && line.length() > 0) {
            StringTokenizer token = new StringTokenizer(line);
            int numBoxes = Integer.parseInt(token.nextToken());
            int dims = Integer.parseInt(token.nextToken());
            P103 p = new P103(numBoxes, dims);
            for (int i = 0; i < numBoxes; i++) {
                line = in.readLine();
                token = new StringTokenizer(line);
                int[] vals = new int[dims];
                for (int j = 0; j < dims; j++) {
                    vals[j] = Integer.parseInt(token.nextToken());
                }
                p.addBox(i, vals);
            }
            p.graph();
            Node n = p.longest();
            p.printLongest(n);
        }
    }

    private void printLongest(Node n) {
        System.out.println(n.max);

        int[] r = new int[n.max];
        for (int i = 0; i < r.length; i++) {
            r[i] = n.idx;
            if (n.prev >= 0) {
                n = nodes[n.prev];
            }
        }
        assert (n.prev == -1);

        String prefix = "";
        for (int i = r.length - 1; i >= 0; i--) {
            System.out.print(prefix);
            System.out.print(r[i] + 1);
            prefix = " ";
        }
        System.out.println();
    }

    public Node longest() {
        int max = -1;
        Node maxNode = null;
        for (int i = 0; i < nodes.length; i++) {
            Node n = longest(i);
            if (n.max > max) {
                max = n.max;
                maxNode = n;
            }
        }
        return maxNode;
    }

    public Node longest(int j) {
        if (nodes[j] != null)
            return nodes[j];
        int max = -1;
        int prev = -1;
        for (int i = 0; i < nodes.length; i++) {
            if (matrix[i][j] == 1) {
                Node n = longest(i);
                if (n.max > max) {
                    max = n.max;
                    prev = i;
                }
            }
        }
        Node n = new Node();
        n.idx = j;
        if (max <= 0) {
            n.max = 1;
            n.prev = -1;
        } else {
            n.max = max + 1;
            n.prev = prev;
        }
        nodes[j] = n;
        return n;
    }

    public void graph() {
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length; j++) {
                if (contains(i, j)) {
                    matrix[i][j] = 1;
                }
            }
        }
    }

    public void addBox(int num, int[] vals) {
        Arrays.sort(vals);
        boxes[num] = vals;
    }

    public boolean contains(int bn1, int bn2) {
        int[] b1 = boxes[bn1];
        int[] b2 = boxes[bn2];
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] >= b2[i]) {
                return false;
            }
        }
        return true;
    }

    class Node {
        public int idx;
        public int prev;
        public int max;
    }

}
