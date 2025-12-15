int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

int L[256];
int R[256];
int H[256];
int coords[512];
int uniq[512];
int segH[512];

int swap(int i, int j) {
    int t = coords[i];
    coords[i] = coords[j];
    coords[j] = t;
    return 0;
}

int max(int a, int b) {
    if (a > b) return a;
    return b;
}

int main0() {
    int n = readint();
    int i = 0;
    int c = 0;
    while (i < n) {
        int l = readint();
        int r = readint();
        int h = readint();
        L[i] = l; R[i] = r; H[i] = h;
        coords[c] = l; c = c + 1;
        coords[c] = r; c = c + 1;
        i = i + 1;
    }
    /* sort coords */
    int x = 0;
    while (x < c) {
        int y = x + 1;
        while (y < c) {
            if (coords[x] > coords[y]) swap(x, y);
            y = y + 1;
        }
        x = x + 1;
    }
    /* unique */
    int m = 0;
    x = 0;
    while (x < c) {
        if (m == 0 || coords[x] != uniq[m - 1]) {
            uniq[m] = coords[x];
            m = m + 1;
        }
        x = x + 1;
    }
    /* compute height for each segment */
    int s = 0;
    while (s + 1 < m) {
        int curH = 0;
        i = 0;
        while (i < n) {
            if (L[i] <= uniq[s] && R[i] > uniq[s]) {
                curH = max(curH, H[i]);
            }
            i = i + 1;
        }
        segH[s] = curH;
        s = s + 1;
    }
    /* emit skyline change points */
    int prev = -1;
    s = 0;
    int first = 1;
    while (s + 1 < m) {
        int hseg = segH[s];
        if (first || hseg != prev) {
            writeint(uniq[s]);
            writeint(hseg);
            prev = hseg;
            first = 0;
        }
        s = s + 1;
    }
    /* drop to 0 at end */
    writeint(uniq[m - 1]);
    writeint(0);
    return 0;
}
