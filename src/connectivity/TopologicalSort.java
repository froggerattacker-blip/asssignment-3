package connectivity;

import graph.Digraph;
import graph.Edge;

import java.util.ArrayDeque;
import java.util.Deque;

public class TopologicalSort {
    public static class Result {
        public final int[] order;
        public final boolean acyclic;
        public final long operations;

        public Result(int[] order, boolean acyclic, long operations) {
            this.order = order;
            this.acyclic = acyclic;
            this.operations = operations;
        }
    }

    public static Result run(Digraph graph) {
        int vertices = graph.vertexCount();
        int[] indegree = new int[vertices];
        long operations = 0;
        for (Edge edge : graph.edges()) {
            indegree[edge.to]++;
            operations++;
        }
        Deque<Integer> ready = new ArrayDeque<>();
        for (int vertex = 0; vertex < vertices; vertex++) {
            if (indegree[vertex] == 0) {
                ready.add(vertex);
            }
        }
        int[] order = new int[vertices];
        int placed = 0;
        while (!ready.isEmpty()) {
            int vertex = ready.poll();
            order[placed++] = vertex;
            for (Edge edge : graph.adjacent(vertex)) {
                operations++;
                if (--indegree[edge.to] == 0) {
                    ready.add(edge.to);
                }
            }
        }
        boolean acyclic = placed == vertices;
        return new Result(acyclic ? order : null, acyclic, operations);
    }
}
