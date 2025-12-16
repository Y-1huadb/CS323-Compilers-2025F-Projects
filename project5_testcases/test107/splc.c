int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int main0() {
    int seed;
    int n;
    int i;
    int x;
    int maxv;

    seed = readint();
    n = readint();

    setseed(seed);

    maxv = getrand();

    i = 1;
    while (i < n) {
        x = getrand();
        if (x > maxv) {
            maxv = x;
        }
        i = i + 1;
    }
    writeint(maxv);
    return 0;
}