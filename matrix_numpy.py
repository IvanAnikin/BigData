import numpy as np
import time
import psutil
import os

matrix_sizes = [16, 128, 1024]

for n in matrix_sizes:
    print(f"\nMatrix size: {n}x{n}")

    # Initialize matrices A and B with random values
    A = np.random.rand(n, n)
    B = np.random.rand(n, n)

    # Start measuring execution time
    start = time.time()

    # Matrix multiplication using NumPy
    C = np.matmul(A, B)  # or C = A @ B

    # End measuring execution time
    end = time.time()
    print("Execution Time: %.6f seconds" % (end - start))

    # Get memory usage
    process = psutil.Process(os.getpid())
    memory_usage = process.memory_info().rss / (1024 * 1024)  # Convert to MB
    print(f"Memory Usage: {memory_usage:.2f} MB")

    # Get CPU time
    cpu_times = process.cpu_times()
    print(f"User CPU Time: {cpu_times.user:.6f} seconds")
    print(f"System CPU Time: {cpu_times.system:.6f} seconds")