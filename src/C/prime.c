#include <stdio.h>
#include <stdlib.h>

void creat_prime(int p[], int n, int* total)
{
    int i, j, g = 2;
    for (i = 7; i <= n; i += (g ^= 6)) {
        for (j = 0; (p[j] * p[j] <= i) && (i % p[j]); j++)
            ;
        if (i % p[j])
            p[(*total)++] = i;
    }
}

int prime[6000000] = { 2, 3, 5 };
int n = 100000000; // 要查找的范围(>=6)

int main()
{
    int total = 3; // 找到素数的个数
    int i;
    FILE* fp = fopen("prime.txt", "w");
    creat_prime(prime, n, &total);
    for (i = 0; i < total; i++)
        fprintf(fp, "%d%c", prime[i], ((i + 1) % 20) ? ' ' : '\n');
    fclose(fp);
    return 0;
}