int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

struct Node { int v; int l; int r; int h; };
struct Node nodes[256];
int node_cnt = 0;
int root = -1;

int max(int a, int b) { return a > b ? a : b; }
int height(int idx) { return idx == -1 ? 0 : nodes[idx].h; }

int new_node(int v) {
    int idx = node_cnt;
    nodes[idx].v = v;
    nodes[idx].l = -1;
    nodes[idx].r = -1;
    nodes[idx].h = 1;
    node_cnt = node_cnt + 1;
    return idx;
}

int rotate_right(int y) {
    int x = nodes[y].l;
    int t2 = nodes[x].r;
    nodes[x].r = y;
    nodes[y].l = t2;
    nodes[y].h = max(height(nodes[y].l), height(nodes[y].r)) + 1;
    nodes[x].h = max(height(nodes[x].l), height(nodes[x].r)) + 1;
    return x;
}

int rotate_left(int x) {
    int y = nodes[x].r;
    int t2 = nodes[y].l;
    nodes[y].l = x;
    nodes[x].r = t2;
    nodes[x].h = max(height(nodes[x].l), height(nodes[x].r)) + 1;
    nodes[y].h = max(height(nodes[y].l), height(nodes[y].r)) + 1;
    return y;
}

int balance_factor(int idx) {
    return height(nodes[idx].l) - height(nodes[idx].r);
}

int insert_avl(int idx, int v) {
    if (idx == -1) return new_node(v);
    if (v < nodes[idx].v) nodes[idx].l = insert_avl(nodes[idx].l, v);
    else nodes[idx].r = insert_avl(nodes[idx].r, v);

    nodes[idx].h = max(height(nodes[idx].l), height(nodes[idx].r)) + 1;
    int bf = balance_factor(idx);

    if (bf > 1 && v < nodes[nodes[idx].l].v) return rotate_right(idx);
    if (bf < -1 && v > nodes[nodes[idx].r].v) return rotate_left(idx);
    if (bf > 1 && v > nodes[nodes[idx].l].v) {
        nodes[idx].l = rotate_left(nodes[idx].l);
        return rotate_right(idx);
    }
    if (bf < -1 && v < nodes[nodes[idx].r].v) {
        nodes[idx].r = rotate_right(nodes[idx].r);
        return rotate_left(idx);
    }
    return idx;
}

void inorder(int idx) {
    if (idx == -1) return;
    inorder(nodes[idx].l);
    writeint(nodes[idx].v);
    inorder(nodes[idx].r);
}

int main0() {
    int n = readint();
    int i = 0;
    while (i < n) {
        int v = readint();
        root = insert_avl(root, v);
        i = i + 1;
    }
    inorder(root);
    return 0;
}