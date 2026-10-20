package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {
    public static final int MAX_WEIGHT = 100;

    public static Graph connectedGraph(int vertices, int edges, long seed) {
        Random random = new Random(seed);
        Graph graph = new Graph(vertices);
        Set<Long> used = new HashSet<>();
        int[] order = permutation(vertices, random);
        for (int i = 1; i < vertices; i++) {
            int from = order[i];
            int to = order[random.nextInt(i)];
            graph.addEdge(from, to, 1 + random.nextInt(MAX_WEIGHT));
            used.add(undirectedKey(from, to, vertices));
        }
        while (graph.edgeCount() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);
            if (from == to || !used.add(undirectedKey(from, to, vertices))) {
                continue;
            }
            graph.addEdge(from, to, 1 + random.nextInt(MAX_WEIGHT));
        }
        return graph;
    }

    public static Digraph dag(int vertices, int edges, long seed) {
        Random random = new Random(seed);
        Digraph graph = new Digraph(vertices);
        Set<Long> used = new HashSet<>();
        int[] order = permutation(vertices, random);
        while (graph.edgeCount() < edges) {
            int first = random.nextInt(vertices);
            int second = random.nextInt(vertices);
            if (first == second) {
                continue;
            }
            int from = order[Math.min(first, second)];
            int to = order[Math.max(first, second)];
            if (!used.add((long) from * vertices + to)) {
                continue;
            }
            graph.addEdge(from, to, 1 + random.nextInt(MAX_WEIGHT));
        }
        return graph;
    }

    public static Digraph randomDigraph(int vertices, int edges, long seed) {
        Random random = new Random(seed);
        Digraph graph = new Digraph(vertices);
        Set<Long> used = new HashSet<>();
        while (graph.edgeCount() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);
            if (from == to || !used.add((long) from * vertices + to)) {
                continue;
            }
            graph.addEdge(from, to, 1 + random.nextInt(MAX_WEIGHT));
        }
        return graph;
    }

    private static int[] permutation(int size, Random random) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = i;
        }
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = values[i];
            values[i] = values[j];
            values[j] = swap;
        }
        return values;
    }

    private static long undirectedKey(int a, int b, int vertices) {
        return (long) Math.min(a, b) * vertices + Math.max(a, b);
    }
}
