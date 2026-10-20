package graph;

import java.util.List;

public interface WeightedGraph {
    int vertexCount();

    int edgeCount();

    List<Edge> adjacent(int vertex);

    List<Edge> edges();
}
