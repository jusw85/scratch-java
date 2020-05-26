package uva;

import org.apache.commons.lang.ArrayUtils;

/**
 * <pre>
 * All pairs shortest path
 * ok - negative edge weights
 * nok - negative cycles
 *
 * shortestPath(i,j,k) = shortest path from i to j using {0..k} as intermediate nodes
 * shortestPath(i,j,-1) = shortest path from i to j using {} as intermediate nodes = baseWeight(i, j)
 * shortestPath(i,j,k) = min(shortestPath(i,j,k-1), shortestPath(i,k,k-1) + shortestPath(k,j,k-1))
 * </pre>
 */
public class FloydWarshall {

    /**
     * Sample input:
     * https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm#Example
     */
    public static void main(String[] args) {
//        Recursive n = new Recursive(4);
//        DP n = new DP(4);
        DP2 n = new DP2(4);
        n.connect(0, 2, -2);
        n.connect(2, 3, 2);
        n.connect(3, 1, -1);
        n.connect(1, 0, 4);
        n.connect(1, 2, 3);
        n.solve();
        n.pprint();
    }

    private static class Recursive {
        private int size;
        private Double[][] w;
        private Double[][] m;

        public Recursive(int size) {
            this.size = size;
            m = new Double[size][size];
            w = new Double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    w[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        public void connect(int from, int to, double weight) {
            w[from][to] = weight;
        }

        public void solve() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    m[i][j] = fw(i, j, size - 1);
                }
            }
        }

        /**
         * shortest path from i to j using {0,1,..,k} as intermediate nodes
         */
        public double fw(int i, int j, int k) {
            if (k < 0) {
                return w[i][j];
            }
            double d1 = fw(i, j, k - 1);
            double d2 = fw(i, k, k - 1) + fw(k, j, k - 1);
            return Math.min(d1, d2);
        }

        public void pprint() {
            for (int i = 0; i < size; i++) {
                System.out.println(ArrayUtils.toString(m[i]));
            }
        }
    }

    private static class DP {
        private int size;
        private Double[][] w;
        private Double[][][] m;

        public DP(int size) {
            this.size = size;
            m = new Double[size][size][size];
            w = new Double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    w[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        public void connect(int from, int to, double weight) {
            w[from][to] = weight;
        }

        /**
         * m[k][i][j] = shortest path from i to j using {0,1,..,k} as intermediate nodes
         */
        public void solve() {
            for (int k = 0; k < size; k++) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (k == 0) {
                            m[k][i][j] = Math.min(
                                    w[i][j],
                                    w[i][k] + w[k][j]);
                        } else {
                            m[k][i][j] = Math.min(
                                    m[k - 1][i][j],
                                    m[k - 1][i][k] + m[k - 1][k][j]);
                        }
                    }
                }
            }
        }

        public void pprint() {
            Double[][] mk = m[size - 1];
            for (int i = 0; i < size; i++) {
                System.out.println(ArrayUtils.toString(mk[i]));
            }
        }
    }

    private static class DP2 {
        private int size;
        private Double[][] m;

        public DP2(int size) {
            this.size = size;
            m = new Double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    m[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        public void connect(int from, int to, double weight) {
            m[from][to] = weight;
        }

        /**
         * <pre>
         * Flattened version of DP
         * Can be flattened because:
         *
         * m[k][i][j] = min(
         *     m[k - 1][i][j],
         *     m[k - 1][i][k] + m[k - 1][k][j]);
         *
         * if (j == k):
         * m[k][i][k] = min(
         *     m[k - 1][i][k],
         *     m[k - 1][i][k] + m[k - 1][k][k]);
         *
         * No negative cycles => m[*][k][k] >= 0
         * i.e. m[k][i][k] = m[k-1][i][k]
         * Similarly, m[k][k][j] = m[k-1][k][j]
         *
         * For (i == k || j == k), m[k][i][j] = m[k-1][i][j]
         *
         * Visually: When processing k, highlight row k horizontally, column k vertically
         * These values don't change on k
         * Only process non-highlighted cells
         * For cell c, c = min(c, projection of c onto highlighted row + highlighted col)
         * Since highlighted doesnt change, can reuse same array for k [0..n)
         * </pre>
         */
        public void solve() {
            for (int k = 0; k < size; k++) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        m[i][j] = Math.min(
                                m[i][j],
                                m[i][k] + m[k][j]);
                    }
                }
            }
        }

        public void pprint() {
            for (int i = 0; i < size; i++) {
                System.out.println(ArrayUtils.toString(m[i]));
            }
        }
    }

    private static class UndirectedPositive {
        private int size;
        private int[][] m;

        public UndirectedPositive(int size) {
            this.size = size;
            m = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i == j) {
                        m[i][j] = 0;
                    } else {
                        m[i][j] = -1;
                    }
                }
            }
        }

        public void connect(int from, int to, int weight) {
            m[from][to] = weight;
            m[to][from] = weight;
        }

        public void solve() {
            for (int k = 0; k < size; k++) {
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {
                        if (k != i && k != j && m[i][k] > 0 && m[k][j] > 0) {
                            int newcost = m[i][k] + m[k][j];
                            if (m[i][j] < 0 || newcost < m[i][j]) {
                                m[i][j] = newcost;
                                m[j][i] = newcost;
                            }
                        }
                    }
                }
            }
        }

        public void pprint() {
            for (int i = 0; i < size; i++) {
                System.out.println(ArrayUtils.toString(m[i]));
            }
        }
    }
}
