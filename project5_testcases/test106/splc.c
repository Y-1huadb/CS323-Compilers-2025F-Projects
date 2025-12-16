int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int main0() {
    int seed;
    int r1;
    int r2;
    int r3;

    seed = readint();
    int a = setseed(seed);
    r1 = getrand();
    r2 = getrand();
    r3 = getrand();
    writeint(a);
    writeint(r1);
    writeint(r2);
    writeint(r3);
    return 0;
}
