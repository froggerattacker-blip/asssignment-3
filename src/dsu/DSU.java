package dsu;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int components;
    private long findCalls;
    private long unionCalls;
    private long pointerUpdates;

    public DSU(int size) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.components = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int vertex) {
        findCalls++;
        int root = vertex;
        while (parent[root] != root) {
            root = parent[root];
            pointerUpdates++;
        }
        while (parent[vertex] != root) {
            int next = parent[vertex];
            parent[vertex] = root;
            vertex = next;
        }
        return root;
    }

    public boolean union(int a, int b) {
        unionCalls++;
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return false;
        }
        if (rank[rootA] < rank[rootB]) {
            int swap = rootA;
            rootA = rootB;
            rootB = swap;
        }
        parent[rootB] = rootA;
        if (rank[rootA] == rank[rootB]) {
            rank[rootA]++;
        }
        components--;
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    public int components() {
        return components;
    }

    public long findCalls() {
        return findCalls;
    }

    public long unionCalls() {
        return unionCalls;
    }

    public long pointerUpdates() {
        return pointerUpdates;
    }

    public long operations() {
        return findCalls + unionCalls + pointerUpdates;
    }
}
