import numpy as np
import time
import psutil
import os

def manual_multiplication(A, B, n):
    C = [[0 for _ in range(n)] for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                C[i][j] += A[i][k] * B[k][j]
    return C

matrix_sizes = [16, 128, 1024]

for n in matrix_sizes:
    print(f"\nMatrix size: {n}x{n}")

    # Initialize matrices A and B with random values
    A = np.random.rand(n, n)
    B = np.random.rand(n, n)

    # Convert NumPy arrays to lists for manual multiplication
    A_list = A.tolist()
    B_list = B.tolist()

    # Manual matrix multiplication
    start_manual = time.time()
    C_manual = manual_multiplication(A_list, B_list, n)
    end_manual = time.time()

    # NumPy matrix multiplication
    start_numpy = time.time()
    C_numpy = np.matmul(A, B)  # or C_numpy = A @ B
    end_numpy = time.time()

    # Execution times
    print("Manual Multiplication Time: %.6f seconds" % (end_manual - start_manual))
    print("NumPy Multiplication Time: %.6f seconds" % (end_numpy - start_numpy))

    # Convert C_manual to NumPy array for comparison
    C_manual_np = np.array(C_manual)

    # Compare results of manual and NumPy matrix multiplication
    if np.allclose(C_manual_np, C_numpy):
        print("Results Match: Manual multiplication and NumPy give the same results.")
    else:
        print("Results Mismatch: There is a difference between manual multiplication and NumPy results.")

    # Get memory usage
    process = psutil.Process(os.getpid())
    memory_usage = process.memory_info().rss / (1024 * 1024)  # Convert to MB
    print(f"Memory Usage: {memory_usage:.2f} MB")

    # Get CPU time
    cpu_times = process.cpu_times()
    print(f"User CPU Time: {cpu_times.user:.6f} seconds")
    print(f"System CPU Time: {cpu_times.system:.6f} seconds")