int readint();
int writeint(int out);
int setseed(int seed);
int getrand();

struct Stats {
    int minv;
    int maxv;
    int sum;
};

void init_stats(struct Stats* s, int x) {
    s->minv = x;
    s->maxv = x;
    s->sum  = x;
}

void update_stats(struct Stats* s, int x) {
    if (x < s->minv) {
        s->minv = x;
    }
    if (x > s->maxv) {
        s->maxv = x;
    }
    s->sum = s->sum + x;
}

int main() {
    int n;
    int i;
    int x;
    struct Stats s;
    struct Stats* ps;

    ps = &s;

    n = readint();

    if (n <= 0) {
        return 0;
    }

    x = readint();
    init_stats(ps, x);

    i = 1;
    while (i < n) {
        x = readint();
        update_stats(ps, x);
        i = i + 1;
    }
    writeint(ps->minv);
    writeint(ps->maxv);
    writeint(ps->sum);

    return 0;
}
