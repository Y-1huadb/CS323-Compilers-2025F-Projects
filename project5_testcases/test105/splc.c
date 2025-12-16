int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int main0() {
    int a = readint();
    int b = readint();
    int c = readint();
    if (a - b * c < 0) {
        a = -a * 2;
    } else {
        a = a + 1;
        a = -a * 2;
    }
    writeint(a);
    return 0;
}
