import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.lang.management.ManagementFactory;

public class Matrix3 {

    private static final ForkJoinPool pool = new ForkJoinPool();

    // Strassen algorithm for matrix multiplication with parallel processing
    public static double[][] strassen(double[][] A, double[][] B, int n) {
        if (n <= 64) {  // Base case for switching to direct computation
            return multiplyDirect(A, B);
        }

        int newSize = n / 2;
        double[][] A11 = new double[newSize][newSize];
        double[][] A12 = new double[newSize][newSize];
        double[][] A21 = new double[newSize][newSize];
        double[][] A22 = new double[newSize][newSize];

        double[][] B11 = new double[newSize][newSize];
        double[][] B12 = new double[newSize][newSize];
        double[][] B21 = new double[newSize][newSize];
        double[][] B22 = new double[newSize][newSize];

        splitMatrix(A, A11, 0, 0);
        splitMatrix(A, A12, 0, newSize);
        splitMatrix(A, A21, newSize, 0);
        splitMatrix(A, A22, newSize, newSize);

        splitMatrix(B, B11, 0, 0);
        splitMatrix(B, B12, 0, newSize);
        splitMatrix(B, B21, newSize, 0);
        splitMatrix(B, B22, newSize, newSize);

        // Use ForkJoin tasks for each recursive call to strassen
        RecursiveTask<double[][]> taskM1 = new StrassenTask(add(A11, A22), add(B11, B22), newSize);
        RecursiveTask<double[][]> taskM2 = new StrassenTask(add(A21, A22), B11, newSize);
        RecursiveTask<double[][]> taskM3 = new StrassenTask(A11, subtract(B12, B22), newSize);
        RecursiveTask<double[][]> taskM4 = new StrassenTask(A22, subtract(B21, B11), newSize);
        RecursiveTask<double[][]> taskM5 = new StrassenTask(add(A11, A12), B22, newSize);
        RecursiveTask<double[][]> taskM6 = new StrassenTask(subtract(A21, A11), add(B11, B12), newSize);
        RecursiveTask<double[][]> taskM7 = new StrassenTask(subtract(A12, A22), add(B21, B22), newSize);

        // Start all tasks
        taskM1.fork();
        taskM2.fork();
        taskM3.fork();
        taskM4.fork();
        taskM5.fork();
        taskM6.fork();
        taskM7.fork();

        // Wait for results
        double[][] M1 = taskM1.join();
        double[][] M2 = taskM2.join();
        double[][] M3 = taskM3.join();
        double[][] M4 = taskM4.join();
        double[][] M5 = taskM5.join();
        double[][] M6 = taskM6.join();
        double[][] M7 = taskM7.join();

        double[][] C11 = add(subtract(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(subtract(add(M1, M3), M2), M6);

        double[][] C = new double[n][n];
        joinMatrix(C11, C, 0, 0);
        joinMatrix(C12, C, 0, newSize);
        joinMatrix(C21, C, newSize, 0);
        joinMatrix(C22, C, newSize, newSize);

        return C;
    }

    private static class StrassenTask extends RecursiveTask<double[][]> {
        private final double[][] A, B;
        private final int size;

        StrassenTask(double[][] A, double[][] B, int size) {
            this.A = A;
            this.B = B;
            this.size = size;
        }

        @Override
        protected double[][] compute() {
            return strassen(A, B, size);
        }
    }

    private static double[][] multiplyDirect(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }

    private static double[][] add(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    private static double[][] subtract(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }

    private static void splitMatrix(double[][] P, double[][] C, int iB, int jB) {
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C.length; j++) {
                C[i][j] = P[i + iB][j + jB];
            }
        }
    }

    private static void joinMatrix(double[][] C, double[][] P, int iB, int jB) {
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C.length; j++) {
                P[i + iB][j + jB] = C[i][j];
            }
        }
    }

    public static void main(String[] args) {

        int[] sizes = {16, 128, 1024};  // Array of matrix sizes to iterate through

        for (int n : sizes) {
            System.out.println("Matrix size: " + n + "x" + n);

            double[][] a = new double[n][n];
            double[][] b = new double[n][n];
            Random random = new Random();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextDouble();
                    b[i][j] = random.nextDouble();
                }
            }

            long start = System.currentTimeMillis();

            // Using Strassen's algorithm for matrix multiplication with parallelism
            double[][] c = strassen(a, b, n);

            long stop = System.currentTimeMillis();
            System.out.println("Execution Time: " + (stop - start) * 1e-3 + " seconds");

            long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);  // Convert to MB
            System.out.println("Memory Usage: " + memoryUsed + " MB");

            long cpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() / 1_000_000;
            System.out.println("CPU Time: " + cpuTime + " milliseconds");
        }
    }
}
