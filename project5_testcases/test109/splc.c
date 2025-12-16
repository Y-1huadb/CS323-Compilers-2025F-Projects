int readint();
int writeint(int out);
int setseed(int seed);
int getrand();

struct Stats {
    int *minv;
    int *maxv;
    int *sum;
};


int main() {
    int n;
    int i;
    int x;
    struct Stats s;

    int v_min;
    int v_max;
    int v_sum;

    s.minv = &v_min;
    s.maxv = &v_max;
    s.sum  = &v_sum;

    n = readint();

    if (n <= 0) {
        return 0;
    }

    x = readint();

    *s.minv = x;
    *s.maxv = x;
    *s.sum  = x;

    i = 1;
    while (i < n) {
        x = readint();

        if (x < *s.minv) {
            *s.minv = x;
        }
        if (x > *s.maxv) {
            *s.maxv = x;
        }
        *s.sum = *s.sum + x;

        i = i + 1;
    }

    writeint(*s.minv);
    writeint(*s.maxv);
    writeint(*s.sum);
    int avg = *s.sum / n;
    writeint(avg);

    return 0;
}