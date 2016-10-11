package uva;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

class P105 {

    private List<Node> nodes = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        P105 p = new P105();
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine() && (line = sc.nextLine()).length() > 0) {
            Scanner sc2 = new Scanner(line);
            p.add(sc2.nextInt(), sc2.nextInt(), sc2.nextInt());
            sc2.close();
        }
        sc.close();
    }

    public void add(int s, int e, int h) {
        if (nodes.isEmpty()) {
            nodes.add(new Node(s, h));
            nodes.add(new Node(e, 0));
            return;
        }
        boolean in = false;
        ListIterator<Node> it = nodes.listIterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (s == n.x) {
                if (h > n.h) {
                    n.h = h;
                    in = true;
                    it.previous();
                }
            } else if (s > n.x) {
                if (h > n.h) {
                    it.add(new Node(s, h));
                    in = true;
                    it.previous();
                }
            }
        }
    }

    public void print() {

    }

    class Node {
        public int x;
        public int h;

        public Node(int x, int h) {
            this.x = x;
            this.h = h;
        }
    }

}
