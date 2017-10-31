package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<ColumnMultipleResult> completionService = new ExecutorCompletionService<>(executor);

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            completionService.submit(() -> columnMultiply(col, matrixA, matrixB, matrixSize));
        }

        for (int i = 0; i < matrixSize; i++) {
            ColumnMultipleResult res = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][res.col] = res.columnC[k];
            }
        }

        return matrixC;
    }

    public static class ColumnMultipleResult {
        private final int col;
        private final int[] columnC;

        private ColumnMultipleResult(int col, int[] columnC) {
            this.col = col;
            this.columnC = columnC;
        }
    }

    private static ColumnMultipleResult columnMultiply(int col, int[][] matrixA, int[][]matrixB, int matrixSize)
    {
        final int[] columnB = new int[matrixSize];
        for (int k = 0; k < matrixSize; k++) {
            columnB[k] = matrixB[k][col];
        }

        final int[] columnC = new int[matrixSize];

        for (int row = 0; row < matrixSize; row++) {
            final int[] rowA = matrixA[row];
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += rowA[k] * columnB[k];
            }
            columnC[row] = sum;
        }
        return new ColumnMultipleResult(col, columnC);
    }

    // optimized by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            ColumnMultipleResult res = columnMultiply(col, matrixA, matrixB, matrixSize);
            for (int row = 0; row < matrixSize; row++)
            {
                matrixC[row][res.col] = res.columnC[row];
            }
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
