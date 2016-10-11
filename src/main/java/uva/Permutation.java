package uva;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Permutation {

    public static void main(String[] args) {
        StringPermutationV2 perm = new StringPermutationV2("ABC");
        for (String s : perm) {
            System.out.println(s);
        }
    }

    public static List<String> permutation(String str) {
        List<String> l = new ArrayList<>();
        permutation("", str, l);
        return l;
    }

    private static void permutation(String prefix, String str, List<String> l) {
        int n = str.length();
        if (n == 0) {
            l.add(prefix);
        } else {
            for (int i = 0; i < n; i++)
                permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i + 1, n), l);
        }
    }

    /**
     * Original
     * http://stackoverflow.com/a/13037291
     */
    public static class StringPermutationV1 implements Iterable<String> {

        // could implement Collection<String> but it's immutable, so most methods are essentially vacuous

        protected final String string;

        /**
         * Creates an implicit Iterable collection of all permutations of a string
         *
         * @param string String to be permuted
         * @see Iterable
         * @see #iterator
         */
        public StringPermutationV1(String string) {
            this.string = string;
        }

        /**
         * Constructs and sequentially returns the permutation values
         */
        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {

                char[] array = string.toCharArray();
                int length = string.length();
                int[] numRotations = (length == 0) ? null : new int[length];

                @Override
                public boolean hasNext() {
                    return numRotations != null;
                }

                @Override
                public String next() {

                    if (numRotations == null) throw new NoSuchElementException();

                    for (int i = 1; i < length; ++i) {
                        rotate(i);
                        for (int j = 0; j < i; ++j) {
                            numRotations[j] = 0;
                        }
                        if (++numRotations[i] <= i) {
                            return new String(array);
                        }
                        numRotations[i] = 0;
                    }
                    numRotations = null;
                    return new String(array);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private void rotate(int i) {
                    char swap = array[i];
                    System.arraycopy(array, 0, array, 1, i);
                    array[0] = swap;
                }

            };
        }
    }

    /**
     * Different iteration order
     */
    public static class StringPermutationV2 implements Iterable<String> {

        // could implement Collection<String> but it's immutable, so most methods are essentially vacuous

        protected final String str;

        /**
         * Creates an implicit Iterable collection of all permutations of a string
         *
         * @param str String to be permuted
         * @see Iterable
         * @see #iterator
         */
        public StringPermutationV2(String str) {
            this.str = str;
        }

        /**
         * Constructs and sequentially returns the permutation values
         */
        @Override
        public Iterator<String> iterator() {
            return new StringPermutationIterator(str);
        }

        private class StringPermutationIterator implements Iterator<String> {

            char[] array;
            int length;
            int[] numRotations;

            public StringPermutationIterator(String str) {
                array = str.toCharArray();
                length = str.length();
                numRotations = (length == 0) ? null : new int[length];
            }

            @Override
            public boolean hasNext() {
                return numRotations != null;
            }

            @Override
            public String next() {

                if (numRotations == null) throw new NoSuchElementException();

                for (int i = length - 1; i >= 1; i--) {
                    rotateForward(i);
                    if (++numRotations[i] <= i) {
                        return new String(array);
                    }
                    numRotations[i] = 0;
                }
                numRotations = null;
                return new String(array);
            }

            /**
             * Rotate forward once from index 0 to i
             */
            private void rotateForward(int i) {
                char swap = array[i];
                System.arraycopy(array, 0, array, 1, i);
                array[0] = swap;
            }

            private void rotateBackward(int i) {
                char swap = array[0];
                System.arraycopy(array, 1, array, 0, i);
                array[i] = swap;
            }
        }
    }
}
