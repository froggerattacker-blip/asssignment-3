package mst;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static MstResult run(Graph graph) {
        int vertices = graph.vertexCount();
        boolean[] inTree = new boolean[vertices];
        List<Edge> tree = new ArrayList<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        long weight = 0;
        long edgeChecks = 0;
        int components = 0;
        for (int start = 0; start < vertices; start++) {
            if (inTree[start]) {
                continue;
            }
            components++;
            inTree[start] = true;
            queue.clear();
            queue.addAll(graph.adjacent(start));
            while (!queue.isEmpty()) {
                Edge edge = queue.poll();
                edgeChecks++;
                if (inTree[edge.to]) {
                    continue;
                }
                inTree[edge.to] = true;
                tree.add(edge);
                weight += edge.weight;
                for (Edge next : graph.adjacent(edge.to)) {
                    if (!inTree[next.to]) {
                        queue.add(next);
                    }
                }
            }
        }
        return new MstResult(tree, weight, components, edgeChecks, 0);
    }
}
