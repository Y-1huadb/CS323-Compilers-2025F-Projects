int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int a[256];
int tmp[256];

void merge(int l, int m, int r) {
    int i = l;
    int j = m + 1;
    int k = l;
    while (i <= m && j <= r) {
        if (a[i] <= a[j]) {
            tmp[k] = a[i];
            i = i + 1;
        } else {
            tmp[k] = a[j];
            j = j + 1;
        }
        k = k + 1;
    }
    while (i <= m) { tmp[k] = a[i]; i = i + 1; k = k + 1; }
    while (j <= r) { tmp[k] = a[j]; j = j + 1; k = k + 1; }
    k = l;
    while (k <= r) { a[k] = tmp[k]; k = k + 1; }
}

void mergesort(int l, int r) {
    if (l >= r) return;
    int m = (l + r) / 2;
    mergesort(l, m);
    mergesort(m + 1, r);
    merge(l, m, r);
}

int main0() {
    int n = readint();
    int i = 0;
    while (i < n) {
        a[i] = readint();
        i = i + 1;
    }
    mergesort(0, n - 1);
    i = 0;
    while (i < n) {
        writeint(a[i]);
        i = i + 1;
    }
    return 0;
}
