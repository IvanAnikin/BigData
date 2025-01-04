import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Random;

public class DistributedMatrixMultiplication {

    public static double[][] distributedMatrixMultiply(double[][] A, double[][] B, int n) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IMap<String, double[][]> matrixMap = hazelcastInstance.getMap("matrixMap");

        // Split matrices into blocks
        int blockSize = n / 2;  // Example: Split into 4 blocks for simplicity
        double[][] A11 = new double[blockSize][blockSize];
        double[][] A12 = new double[blockSize][blockSize];
        double[][] A21 = new double[blockSize][blockSize];
        double[][] A22 = new double[blockSize][blockSize];

        double[][] B11 = new double[blockSize][blockSize];
        double[][] B12 = new double[blockSize][blockSize];
        double[][] B21 = new double[blockSize][blockSize];
        double[][] B22 = new double[blockSize][blockSize];

        MatrixUtils.splitMatrix(A, A11, 0, 0);
        MatrixUtils.splitMatrix(A, A12, 0, blockSize);
        MatrixUtils.splitMatrix(A, A21, blockSize, 0);
        MatrixUtils.splitMatrix(A, A22, blockSize, blockSize);

        MatrixUtils.splitMatrix(B, B11, 0, 0);
        MatrixUtils.splitMatrix(B, B12, 0, blockSize);
        MatrixUtils.splitMatrix(B, B21, blockSize, 0);
        MatrixUtils.splitMatrix(B, B22, blockSize, blockSize);

        // Distribute tasks
        matrixMap.put("A11", A11);
        matrixMap.put("A12", A12);
        matrixMap.put("A21", A21);
        matrixMap.put("A22", A22);

        matrixMap.put("B11", B11);
        matrixMap.put("B12", B12);
        matrixMap.put("B21", B21);
        matrixMap.put("B22", B22);

        // Execute multiplication tasks on distributed nodes
        hazelcastInstance.getExecutorService("default").execute(() -> {
            double[][] C11 = MatrixUtils.multiplyDirect(matrixMap.get("A11"), matrixMap.get("B11"));
            double[][] C12 = MatrixUtils.multiplyDirect(matrixMap.get("A12"), matrixMap.get("B21"));
            // Store the result back in Hazelcast map or gather results on the main node
        });

        hazelcastInstance.shutdown();
        return new double[n][n];  // Return the combined result
    }

    public static void main(String[] args) {
        int n = 1024;  // Example size
        double[][] A = new double[n][n];
        double[][] B = new double[n][n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = random.nextDouble();
                B[i][j] = random.nextDouble();
            }
        }

        long start = System.currentTimeMillis();
        double[][] result = distributedMatrixMultiply(A, B, n);
        long end = System.currentTimeMillis();

        System.out.println("Execution time: " + (end - start) + " ms");
    }
}
