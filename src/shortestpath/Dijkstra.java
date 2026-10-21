package shortestpath;

import graph.Edge;
import graph.WeightedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static final long UNREACHABLE = Long.MAX_VALUE;

    public static class Result {
        public final long[] distance;
        public final int[] previous;
        public final long operations;

        public Result(long[] distance, int[] previous, long operations) {
            this.distance = distance;
            this.previous = previous;
            this.operations = operations;
        }

        public boolean reachable(int vertex) {
            return distance[vertex] != UNREACHABLE;
        }

        public List<Integer> pathTo(int vertex) {
            List<Integer> path = new ArrayList<>();
            if (!reachable(vertex)) {
                return path;
            }
            for (int current = vertex; current != -1; current = previous[current]) {
                path.add(current);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static Result run(WeightedGraph graph, int source) {
        for (Edge edge : graph.edges()) {
            if (edge.weight < 0) {
                throw new IllegalArgumentException("Dijkstra requires non-negative weights, found " + edge);
            }
        }
        int vertices = graph.vertexCount();
        long[] distance = new long[vertices];
        int[] previous = new int[vertices];
        boolean[] settled = new boolean[vertices];
        Arrays.fill(distance, UNREACHABLE);
        Arrays.fill(previous, -1);
        distance[source] = 0;
        PriorityQueue<long[]> queue = new PriorityQueue<>(Comparator.comparingLong(entry -> entry[0]));
        queue.add(new long[]{0, source});
        long operations = 0;
        while (!queue.isEmpty()) {
            int vertex = (int) queue.poll()[1];
            if (settled[vertex]) {
                continue;
            }
            settled[vertex] = true;
            for (Edge edge : graph.adjacent(vertex)) {
                operations++;
                long candidate = distance[vertex] + edge.weight;
                if (candidate < distance[edge.to]) {
                    distance[edge.to] = candidate;
                    previous[edge.to] = vertex;
                    queue.add(new long[]{candidate, edge.to});
                }
            }
        }
        return new Result(distance, previous, operations);
    }
}
