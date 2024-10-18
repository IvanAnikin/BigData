import java.util.Random;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Matrix2 {
    static int n = 1024;
    static double[][] a = new double[n][n];
    static double[][] b = new double[n][n];
    static double[][] c = new double[n][n];

    public static void main(String[] args) {

        int[] sizes = {16, 128, 1024}; 

        for (int n : sizes) {

            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextDouble();
                    b[i][j] = random.nextDouble();
                    c[i][j] = 0;
                }
            }

            long start = System.currentTimeMillis();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        c[i][j] += a[i][k] * b[k][j];
                    }
                }
            }

            long stop = System.currentTimeMillis();
            System.out.println("Execution Time: " + (stop - start) * 1e-3 + " seconds");

            Runtime runtime = Runtime.getRuntime();
            long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);  // Convert to MB
            System.out.println("Memory Usage: " + memoryUsed + " MB");

            long cpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() / 1_000_000;
            System.out.println("CPU Time: " + cpuTime + " milliseconds");
    
        }
    }
}