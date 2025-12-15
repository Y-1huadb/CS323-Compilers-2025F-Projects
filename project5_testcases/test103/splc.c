int readint();
int writeint(int out);
int setseed(int seed);
int getrand();
int assert_eq(int where, int given, int expected);

struct Node {
    int v;
    int color; /* 0=black, 1=red */
    struct Node* l;
    struct Node* r;
    struct Node* p;
};

struct Node nodes_pool[256];
int pool_cnt;
struct Node* NIL;
struct Node* root;
struct Node* tmp_node;

int new_node(int v) {
    struct Node* n = &nodes_pool[pool_cnt];
    pool_cnt = pool_cnt + 1;
    n->v = v;
    n->color = 1;
    n->l = NIL;
    n->r = NIL;
    n->p = NIL;
    tmp_node = n;
    return 0;
}

int init_rb() {
    pool_cnt = 0;
    NIL = &nodes_pool[pool_cnt];
    pool_cnt = pool_cnt + 1;
    NIL->v = 0;
    NIL->color = 0;
    NIL->l = NIL;
    NIL->r = NIL;
    NIL->p = NIL;
    root = NIL;
    return 0;
}

int left_rotate(struct Node* x) {
    struct Node* y = x->r;
    x->r = y->l;
    if (y->l != NIL) y->l->p = x;
    y->p = x->p;
    if (x->p == NIL) {
        root = y;
    } else if (x == x->p->l) {
        x->p->l = y;
    } else {
        x->p->r = y;
    }
    y->l = x;
    x->p = y;
    return 0;
}

int right_rotate(struct Node* y) {
    struct Node* x = y->l;
    y->l = x->r;
    if (x->r != NIL) x->r->p = y;
    x->p = y->p;
    if (y->p == NIL) {
        root = x;
    } else if (y == y->p->l) {
        y->p->l = x;
    } else {
        y->p->r = x;
    }
    x->r = y;
    y->p = x;
    return 0;
}

int insert_fix(struct Node* z) {
    while (z->p->color == 1) {
        if (z->p == z->p->p->l) {
            struct Node* y = z->p->p->r;
            if (y->color == 1) {
                z->p->color = 0;
                y->color = 0;
                z->p->p->color = 1;
                z = z->p->p;
            } else {
                if (z == z->p->r) {
                    z = z->p;
                    left_rotate(z);
                }
                z->p->color = 0;
                z->p->p->color = 1;
                right_rotate(z->p->p);
            }
        } else {
            struct Node* y = z->p->p->l;
            if (y->color == 1) {
                z->p->color = 0;
                y->color = 0;
                z->p->p->color = 1;
                z = z->p->p;
            } else {
                if (z == z->p->l) {
                    z = z->p;
                    right_rotate(z);
                }
                z->p->color = 0;
                z->p->p->color = 1;
                left_rotate(z->p->p);
            }
        }
    }
    root->color = 0;
    return 0;
}

int insert_value(int v) {
    new_node(v);
    struct Node* z = tmp_node;
    struct Node* y = NIL;
    struct Node* x = root;
    while (x != NIL) {
        y = x;
        if (z->v < x->v) x = x->l; else x = x->r;
    }
    z->p = y;
    if (y == NIL) root = z; else if (z->v < y->v) y->l = z; else y->r = z;
    insert_fix(z);
    return 0;
}

int inorder(struct Node* x) {
    if (x == NIL) return 0;
    inorder(x->l);
    writeint(x->v);
    inorder(x->r);
    return 0;
}

int main0() {
    init_rb();
    int n = readint();
    int i = 0;
    while (i < n) {
        int v = readint();
        insert_value(v);
        i = i + 1;
    }
    inorder(root);
    return 0;
}
