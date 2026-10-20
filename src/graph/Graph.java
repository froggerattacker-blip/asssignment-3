package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph implements WeightedGraph {
    private final int vertices;
    private final List<List<Edge>> adjacency;
    private final List<Edge> edges = new ArrayList<>();

    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjacency = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        edges.add(new Edge(from, to, weight));
        adjacency.get(from).add(new Edge(from, to, weight));
        adjacency.get(to).add(new Edge(to, from, weight));
    }

    @Override
    public int vertexCount() {
        return vertices;
    }

    @Override
    public int edgeCount() {
        return edges.size();
    }

    @Override
    public List<Edge> adjacent(int vertex) {
        return adjacency.get(vertex);
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }
}
