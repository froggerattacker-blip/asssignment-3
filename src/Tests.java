import connectivity.Tarjan;
import connectivity.TopologicalSort;
import dsu.DSU;
import graph.Digraph;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MstResult;
import mst.Prim;
import shortestpath.Dijkstra;

public class Tests {
    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        dsuBasics();
        smallGraphMst();
        disconnectedGraph();
        singleVertex();
        duplicateWeights();
        multipleValidMsts();
        kruskalMatchesPrimOnGeneratedGraphs();
        topologicalOrderIsValid();
        cycleIsDetected();
        sccPartitionsEveryVertexOnce();
        knownSccStructure();
        dijkstraDistances();
        unreachableVertices();
        negativeWeightsRejected();

        System.out.println();
        System.out.println("passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void dsuBasics() {
        DSU dsu = new DSU(5);
        check(dsu.components() == 5, "DSU starts with one component per vertex");
        check(dsu.union(0, 1), "union of different sets succeeds");
        check(!dsu.union(1, 0), "union inside one set is rejected");
        dsu.union(1, 2);
        check(dsu.connected(0, 2), "transitive connectivity");
        check(!dsu.connected(0, 4), "separate sets stay separate");
        check(dsu.components() == 3, "component count after two unions");
    }

    private static void smallGraphMst() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);
        graph.addEdge(0, 3, 10);
        graph.addEdge(0, 2, 7);
        MstResult kruskal = Kruskal.run(graph);
        MstResult prim = Prim.run(graph);
        check(kruskal.weight == 6, "Kruskal finds MST weight 6");
        check(prim.weight == 6, "Prim finds MST weight 6");
        check(kruskal.edges.size() == 3, "MST of 4 vertices has 3 edges");
        check(kruskal.connected && prim.connected, "graph is reported as connected");
    }

    private static void disconnectedGraph() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(1, 2, 6);
        graph.addEdge(3, 4, 2);
        MstResult kruskal = Kruskal.run(graph);
        MstResult prim = Prim.run(graph);
        check(!kruskal.connected, "Kruskal detects a disconnected graph");
        check(!prim.connected, "Prim detects a disconnected graph");
        check(kruskal.components == 2, "two components are found");
        check(kruskal.weight == prim.weight && kruskal.weight == 12, "spanning forest weight is 12");
    }

    private static void singleVertex() {
        Graph graph = new Graph(1);
        MstResult kruskal = Kruskal.run(graph);
        MstResult prim = Prim.run(graph);
        check(kruskal.weight == 0 && prim.weight == 0, "single vertex MST weight is 0");
        check(kruskal.connected && prim.connected, "single vertex graph is connected");

        Digraph digraph = new Digraph(1);
        TopologicalSort.Result ordering = TopologicalSort.run(digraph);
        check(ordering.acyclic && ordering.order.length == 1, "single vertex has a topological ordering");
        check(Tarjan.run(digraph).count == 1, "single vertex forms one SCC");
        check(Dijkstra.run(digraph, 0).distance[0] == 0, "distance to the source is 0");
    }

    private static void duplicateWeights() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 5);
        graph.addEdge(2, 3, 5);
        graph.addEdge(3, 4, 5);
        graph.addEdge(0, 4, 5);
        graph.addEdge(1, 3, 5);
        MstResult kruskal = Kruskal.run(graph);
        MstResult prim = Prim.run(graph);
        check(kruskal.weight == 20 && prim.weight == 20, "duplicate weights give MST weight 20");
    }

    private static void multipleValidMsts() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 0, 1);
        MstResult kruskal = Kruskal.run(graph);
        MstResult prim = Prim.run(graph);
        check(kruskal.weight == prim.weight && kruskal.weight == 3,
                "several valid MSTs still share one total weight");
    }

    private static void kruskalMatchesPrimOnGeneratedGraphs() {
        int[] vertices = {10, 100, 1000};
        int[] edges = {30, 500, 5000};
        for (int i = 0; i < vertices.length; i++) {
            Graph graph = GraphGenerator.connectedGraph(vertices[i], edges[i], 7 + i);
            MstResult kruskal = Kruskal.run(graph);
            MstResult prim = Prim.run(graph);
            check(kruskal.weight == prim.weight,
                    "Kruskal and Prim agree on V=" + vertices[i] + ", E=" + edges[i]);
            check(kruskal.edges.size() == vertices[i] - 1,
                    "MST has V-1 edges on V=" + vertices[i]);
            check(graph.edgeCount() == edges[i], "generator produced exactly E=" + edges[i] + " edges");
        }
    }

    private static void topologicalOrderIsValid() {
        Digraph graph = GraphGenerator.dag(100, 500, 11);
        TopologicalSort.Result ordering = TopologicalSort.run(graph);
        check(ordering.acyclic, "generated DAG is acyclic");
        int[] position = new int[graph.vertexCount()];
        for (int i = 0; i < ordering.order.length; i++) {
            position[ordering.order[i]] = i;
        }
        boolean valid = true;
        for (Edge edge : graph.edges()) {
            if (position[edge.from] >= position[edge.to]) {
                valid = false;
            }
        }
        check(valid, "every edge goes forward in the topological order");
    }

    private static void cycleIsDetected() {
        Digraph graph = new Digraph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        TopologicalSort.Result ordering = TopologicalSort.run(graph);
        check(!ordering.acyclic && ordering.order == null, "cycle makes topological ordering impossible");
        check(Tarjan.run(graph).count == 1, "a 3-cycle is one SCC");
    }

    private static void sccPartitionsEveryVertexOnce() {
        Digraph graph = GraphGenerator.randomDigraph(1000, 5000, 13);
        Tarjan.Result scc = Tarjan.run(graph);
        int[] sizes = new int[scc.count];
        boolean inRange = true;
        for (int component : scc.component) {
            if (component < 0 || component >= scc.count) {
                inRange = false;
                continue;
            }
            sizes[component]++;
        }
        int total = 0;
        for (int size : sizes) {
            total += size;
        }
        check(inRange, "every component id is in range");
        check(total == graph.vertexCount(), "every vertex belongs to exactly one SCC");
        check(scc.count > 1, "a random sparse digraph splits into several SCCs");
    }

    private static void knownSccStructure() {
        Digraph graph = new Digraph(5);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        Tarjan.Result scc = Tarjan.run(graph);
        check(scc.count == 3, "graph splits into 3 strongly connected components");
        check(scc.component[0] == scc.component[1] && scc.component[1] == scc.component[2],
                "the 3-cycle forms one component");
        check(scc.component[3] != scc.component[4], "tail vertices are separate components");
    }

    private static void dijkstraDistances() {
        Digraph graph = new Digraph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 1);
        graph.addEdge(2, 1, 2);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 8);
        graph.addEdge(3, 4, 3);
        Dijkstra.Result result = Dijkstra.run(graph, 0);
        check(result.distance[1] == 3, "shortest distance to 1 is 3");
        check(result.distance[3] == 8, "shortest distance to 3 is 8");
        check(result.distance[4] == 11, "shortest distance to 4 is 11");
        check(result.pathTo(4).toString().equals("[0, 2, 1, 3, 4]"), "reconstructed path is correct");
    }

    private static void unreachableVertices() {
        Digraph graph = new Digraph(4);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 2);
        Dijkstra.Result result = Dijkstra.run(graph, 0);
        check(!result.reachable(3), "isolated vertex stays unreachable");
        check(result.distance[3] == Dijkstra.UNREACHABLE, "unreachable distance stays at the sentinel value");
        check(result.pathTo(3).isEmpty(), "no path is returned for an unreachable vertex");
    }

    private static void negativeWeightsRejected() {
        Digraph graph = new Digraph(2);
        graph.addEdge(0, 1, -5);
        try {
            Dijkstra.run(graph, 0);
            check(false, "negative weights are rejected");
        } catch (IllegalArgumentException expected) {
            check(true, "negative weights are rejected");
        }
    }

    private static void check(boolean condition, String description) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + description);
        } else {
            failed++;
            System.out.println("FAIL  " + description);
        }
    }
}
