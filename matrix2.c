#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <sys/resource.h>

#define n 1024
double a[n][n];
double b[n][n];
double c[n][n];

struct timeval start, stop;
struct rusage usage;

int main() {
    // Initialize matrices
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            a[i][j] = (double) rand() / RAND_MAX;
            b[i][j] = (double) rand() / RAND_MAX;
            c[i][j] = 0;
        }
    }

    // Start measuring execution time
    gettimeofday(&start, NULL);

    // Matrix multiplication
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            for (int k = 0; k < n; ++k) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }

    // Stop measuring execution time
    gettimeofday(&stop, NULL);
    double diff = stop.tv_sec - start.tv_sec + 1e-6 * (stop.tv_usec - start.tv_usec);
    printf("Execution Time: %0.6f seconds\n", diff);

    // Measure memory usage
    getrusage(RUSAGE_SELF, &usage);
    printf("Memory Usage: %ld KB\n", usage.ru_maxrss);  // in kilobytes

    // Optional: CPU time
    printf("User CPU Time: %ld.%06ld seconds\n", usage.ru_utime.tv_sec, usage.ru_utime.tv_usec);
    printf("System CPU Time: %ld.%06ld seconds\n", usage.ru_stime.tv_sec, usage.ru_stime.tv_usec);

    return 0;
}