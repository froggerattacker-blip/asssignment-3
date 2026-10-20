package mst;

import graph.Edge;

import java.util.List;

public class MstResult {
    public final List<Edge> edges;
    public final long weight;
    public final boolean connected;
    public final int components;
    public final long edgeChecks;
    public final long dsuOperations;

    public MstResult(List<Edge> edges, long weight, int components, long edgeChecks, long dsuOperations) {
        this.edges = edges;
        this.weight = weight;
        this.components = components;
        this.connected = components <= 1;
        this.edgeChecks = edgeChecks;
        this.dsuOperations = dsuOperations;
    }
}
