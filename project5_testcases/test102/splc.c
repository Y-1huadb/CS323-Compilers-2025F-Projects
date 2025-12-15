int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int a[256];
int dp[256];

int max(int x, int y) { return x > y ? x : y; }

int main0() {
    int n = readint();
    int i = 0;
    while (i < n) { a[i] = readint(); i = i + 1; }

    int ans = 0;
    i = 0;
    while (i < n) {
        dp[i] = 1;
        int j = 0;
        while (j < i) {
            if (a[j] < a[i]) {
                dp[i] = max(dp[i], dp[j] + 1);
            }
            j = j + 1;
        }
        if (dp[i] > ans) ans = dp[i];
        i = i + 1;
    }
    writeint(ans);
    return 0;
}
