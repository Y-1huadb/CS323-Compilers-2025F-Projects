int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

struct Stats {
    int minv;
    int maxv;
    int sum;
    int avg;
};

int main() {
    int n;
    int i;
    int x;
    struct Stats s;

    n = readint();

    x = readint();
    s.minv = x;
    s.maxv = x;
    s.sum  = x;

    i = 1;
    while (i < n) {
        x = readint();

        if (x < s.minv) {
            s.minv = x;
        }
        if (x > s.maxv) {
            s.maxv = x;
        }

        s.sum = s.sum + x;
        i = i + 1;
    }

    s.avg = s.sum / n;

    writeint(s.minv);
    writeint(s.maxv);
    writeint(s.sum);
    writeint(s.avg);

    return 0;
}
