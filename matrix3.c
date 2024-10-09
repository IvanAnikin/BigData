#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <windows.h>
#include <psapi.h>

// Function to measure time difference
double time_diff(struct timeval start, struct timeval stop) {
    return (stop.tv_sec - start.tv_sec) + 1e-6 * (stop.tv_usec - start.tv_usec);
}

// Function to measure memory and CPU usage on Windows
void print_usage() {
    // Get memory usage
    PROCESS_MEMORY_COUNTERS pmc;
    if (GetProcessMemoryInfo(GetCurrentProcess(), &pmc, sizeof(pmc))) {
        printf("Memory Usage: %lu KB\n", pmc.WorkingSetSize / 1024);
    }

    // Get CPU times
    FILETIME creationTime, exitTime, kernelTime, userTime;
    if (GetProcessTimes(GetCurrentProcess(), &creationTime, &exitTime, &kernelTime, &userTime)) {
        ULARGE_INTEGER user, kernel;
        user.LowPart = userTime.dwLowDateTime;
        user.HighPart = userTime.dwHighDateTime;
        kernel.LowPart = kernelTime.dwLowDateTime;
        kernel.HighPart = kernelTime.dwHighDateTime;

        printf("User CPU Time: %.6f seconds\n", user.QuadPart / 1e7);
        printf("System CPU Time: %.6f seconds\n", kernel.QuadPart / 1e7);
    }
}

int main() {
    int sizes[] = {16, 128, 1024};  // Matrix sizes to iterate through
    int num_sizes = 3;

    for (int s = 0; s < num_sizes; ++s) {
        int n = sizes[s];
        printf("\nMatrix size: %dx%d\n", n, n);

        // Dynamically allocate matrices
        double **a = (double **)malloc(n * sizeof(double *));
        double **b = (double **)malloc(n * sizeof(double *));
        double **c = (double **)malloc(n * sizeof(double *));
        for (int i = 0; i < n; ++i) {
            a[i] = (double *)malloc(n * sizeof(double));
            b[i] = (double *)malloc(n * sizeof(double));
            c[i] = (double *)malloc(n * sizeof(double));
        }

        // Initialize matrices a, b and c
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                a[i][j] = (double)rand() / RAND_MAX;
                b[i][j] = (double)rand() / RAND_MAX;
                c[i][j] = 0;
            }
        }

        // Start timing
        struct timeval start, stop;
        gettimeofday(&start, NULL);

        // Matrix multiplication
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        // Stop timing
        gettimeofday(&stop, NULL);
        double exec_time = time_diff(start, stop);
        printf("Execution Time: %.6f seconds\n", exec_time);

        // Print memory and CPU usage
        print_usage();

        // Free dynamically allocated memory
        for (int i = 0; i < n; ++i) {
            free(a[i]);
            free(b[i]);
            free(c[i]);
        }
        free(a);
        free(b);
        free(c);
    }

    return 0;
}