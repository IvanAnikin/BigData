#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <sys/resource.h>


struct timeval start, stop;
struct rusage usage;

int main() {
    int sizes[] = {16, 128, 1024}; 
    int num_sizes = 3;

    for (int s = 0; s < num_sizes; ++s) {
        int n = sizes[s];
        printf("\nMatrix size: %dx%d\n", n, n);

        double **a = (double **)malloc(n * sizeof(double *));
        double **b = (double **)malloc(n * sizeof(double *));
        double **c = (double **)malloc(n * sizeof(double *));
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                a[i][j] = (double) rand() / RAND_MAX;
                b[i][j] = (double) rand() / RAND_MAX;
                c[i][j] = 0;
            }
        }

        gettimeofday(&start, NULL);

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        gettimeofday(&stop, NULL);
        double diff = stop.tv_sec - start.tv_sec + 1e-6 * (stop.tv_usec - start.tv_usec);
        printf("Execution Time: %0.6f seconds\n", diff);

        getrusage(RUSAGE_SELF, &usage);
        printf("Memory Usage: %ld KB\n", usage.ru_maxrss);  // in kilobytes

        printf("User CPU Time: %ld.%06ld seconds\n", usage.ru_utime.tv_sec, usage.ru_utime.tv_usec);
        printf("System CPU Time: %ld.%06ld seconds\n", usage.ru_stime.tv_sec, usage.ru_stime.tv_usec);

    }

    return 0;

}