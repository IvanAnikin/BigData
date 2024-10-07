import random
import time
import psutil
import os

n = 1024

A = [[random.random() for _ in range(n)] for _ in range(n)]
B = [[random.random() for _ in range(n)] for _ in range(n)]
C = [[0 for _ in range(n)] for _ in range(n)]

# Start measuring execution time
start = time.time()

# Matrix multiplication
for i in range(n):
    for j in range(n):
        for k in range(n):
            C[i][j] += A[i][k] * B[k][j]

# End measuring execution time
end = time.time()
print("Execution Time: %.6f seconds" % (end - start))

# Get memory usage
process = psutil.Process(os.getpid())
memory_usage = process.memory_info().rss / (1024 * 1024)  # Convert to MB
print(f"Memory Usage: {memory_usage:.2f} MB")

# Optional: Get CPU time
cpu_times = process.cpu_times()
print(f"User CPU Time: {cpu_times.user:.6f} seconds")
print(f"System CPU Time: {cpu_times.system:.6f} seconds")