package uva;

public class EditDistance {

    public static int editDistance(String s1, String s2) {
        if (s1 == null || s2 == null)
            return Integer.MAX_VALUE;
        int[][] matrix = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                matrix[i][j] = 0;

        for (int i = 1; i < matrix.length; i++)
            matrix[i][0] = i;
        for (int j = 1; j < matrix[0].length; j++)
            matrix[0][j] = j;

        for (int i = 1; i < matrix.length; i++)
            for (int j = 1; j < matrix[i].length; j++)
                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                    matrix[i][j] = matrix[i - 1][j - 1];
                else {
                    int v1 = matrix[i - 1][j - 1] + 1;
                    int v2 = matrix[i][j - 1] + 1;
                    int v3 = matrix[i - 1][j] + 1;
                    matrix[i][j] = Math.min(v1, Math.min(v2, v3));
                }
        return matrix[s1.length()][s2.length()];
    }

}
