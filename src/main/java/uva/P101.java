package uva;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class P101 {

    public static void main(String[] args)
            throws NumberFormatException, IOException {
        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in));
        int size = Integer.parseInt(input.readLine());
        P101 p = new P101(size);
        String line;
        while (!(line = input.readLine()).equals("quit")) {
            StringTokenizer token = new StringTokenizer(line);
            String op1 = token.nextToken();
            int i = Integer.parseInt(token.nextToken());
            String op2 = token.nextToken();
            int j = Integer.parseInt(token.nextToken());

            if (op1.equals("move")) {
                if (op2.equals("onto")) {
                    p.moveOnto(i, j);
                } else {
                    p.moveOver(i, j);
                }
            } else {
                if (op2.equals("onto")) {
                    p.pileOnto(i, j);
                } else {
                    p.pileOver(i, j);
                }

            }
        }
        p.pprint();

//        Main p = new Main(24);
//        p.moveOnto(9, 1);
//        p.moveOnto(23, 22);
//        p.pileOver(21, 20);
//        p.moveOver(16, 1);
//        p.pileOver(8, 6);
//        p.pileOver(1, 5);
//        p.pileOver(19, 1);
//        p.moveOver(8, 1);
//        p.pprint();
    }

    Point[] blocks;
    List<Integer>[] poss;

    public P101(int size) {
        blocks = new Point[size];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Point(i, 0);
        }
        poss = new List[size];
        for (int i = 0; i < size; i++) {
            poss[i] = new ArrayList<Integer>();
            poss[i].add(i);
        }
    }

    public boolean isValid(Point b1, Point b2) {
        if (b1.x == b2.x) {
            return false;
        }
        return true;
    }

    public void moveOver(int b1, int b2) {
        if (!isValid(blocks[b1], blocks[b2])) {
            return;
        }
        clearAbove(b1);
        pileOver(b1, b2);
    }

    public void moveOnto(int b1, int b2) {
        if (!isValid(blocks[b1], blocks[b2])) {
            return;
        }
        clearAbove(b2);
        moveOver(b1, b2);
    }

    public void pileOnto(int b1, int b2) {
        if (!isValid(blocks[b1], blocks[b2])) {
            return;
        }
        clearAbove(b2);
        pileOver(b1, b2);
    }

    public void pileOver(int b1, int b2) {
        if (!isValid(blocks[b1], blocks[b2])) {
            return;
        }
        Point bl1 = blocks[b1];
        Point bl2 = blocks[b2];
        List<Integer> pos1 = poss[bl1.x];
        List<Integer> pos2 = poss[bl2.x];
        int origy = bl1.y;
        for (int i = origy; i < pos1.size(); i++) {
            int num = pos1.get(i);
            pos2.add(num);
            Point bl = blocks[num];
            bl.x = bl2.x;
            bl.y = pos2.size() - 1;

        }
        pos1.subList(origy, pos1.size()).clear();
    }

    public void clearAbove(int b) {
        Point block = blocks[b];
        List<Integer> pos = poss[block.x];
        int origy = block.y + 1;
        for (int i = origy; i < pos.size(); i++) {
            int num = pos.get(i);
            List<Integer> pos2 = poss[num];
            pos2.add(num);
            Point bl = blocks[num];
            bl.x = num;
            bl.y = pos2.size() - 1;
        }
        pos.subList(origy, pos.size()).clear();
    }

    public void pprint() {
        for (int i = 0; i < poss.length; i++) {
            List<Integer> pos = poss[i];
            System.out.print(i + ":");
            for (int j : pos) {
                System.out.print(" " + j);
            }
            System.out.println();
        }
    }

}
