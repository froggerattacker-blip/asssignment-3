package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {
    public static MstResult run(Graph graph) {
        List<Edge> sorted = new ArrayList<>(graph.edges());
        Collections.sort(sorted);
        DSU dsu = new DSU(graph.vertexCount());
        List<Edge> tree = new ArrayList<>();
        long weight = 0;
        long edgeChecks = 0;
        for (Edge edge : sorted) {
            edgeChecks++;
            if (dsu.union(edge.from, edge.to)) {
                tree.add(edge);
                weight += edge.weight;
                if (tree.size() == graph.vertexCount() - 1) {
                    break;
                }
            }
        }
        return new MstResult(tree, weight, dsu.components(), edgeChecks, dsu.operations());
    }
}
