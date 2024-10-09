import random
import time
import psutil
import os

matrix_sizes = [16, 128, 1024]

for n in matrix_sizes:
    print(f"\nMatrix size: {n}x{n}")

    A = [[random.random() for _ in range(n)] for _ in range(n)]
    B = [[random.random() for _ in range(n)] for _ in range(n)]
    C = [[0 for _ in range(n)] for _ in range(n)]

    start = time.time()

    for i in range(n):
        for j in range(n):
            for k in range(n):
                C[i][j] += A[i][k] * B[k][j]

    end = time.time()
    print("Execution Time: %.6f seconds" % (end - start))

    process = psutil.Process(os.getpid())
    memory_usage = process.memory_info().rss / (1024 * 1024)  # Convert to MB
    print(f"Memory Usage: {memory_usage:.2f} MB")

    cpu_times = process.cpu_times()
    print(f"User CPU Time: {cpu_times.user:.6f} seconds")
    print(f"System CPU Time: {cpu_times.system:.6f} seconds")