import bench.Benchmark;
import connectivity.Tarjan;
import connectivity.TopologicalSort;
import graph.Digraph;
import graph.Edge;
import graph.Graph;
import mst.Kruskal;
import mst.MstResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.List;

public class Main {
    private static final String[] DISTRICTS = {
            "Center", "Airport", "Harbor", "University", "Industrial", "Suburb", "Stadium"};

    private static final String[] SERVICES = {
            "PowerGrid", "WaterPlant", "RoadWorks", "Lighting", "TrafficSystem", "Housing"};

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        String command = args.length == 0 ? "demo" : args[0];
        switch (command) {
            case "demo" -> demo();
            case "bench" -> Benchmark.run();
            case "test" -> Tests.main(new String[0]);
            default -> System.out.println("usage: java -cp out Main [demo|bench|test]");
        }
    }

    private static void demo() {
        Graph city = cityGraph();
        MstResult kruskal = Kruskal.run(city);
        MstResult prim = Prim.run(city);

        System.out.println("=== Infrastructure planning (MST) ===");
        System.out.println("Kruskal total cost: " + kruskal.weight + ", connected: " + kruskal.connected);
        for (Edge edge : kruskal.edges) {
            System.out.println("  " + DISTRICTS[edge.from] + " -- " + DISTRICTS[edge.to] + " : " + edge.weight);
        }
        System.out.println("Prim total cost:    " + prim.weight);
        System.out.println("Same total weight:  " + (kruskal.weight == prim.weight));

        System.out.println();
        System.out.println("=== Service dependencies (topological ordering) ===");
        Digraph services = serviceGraph();
        TopologicalSort.Result ordering = TopologicalSort.run(services);
        if (ordering.acyclic) {
            StringBuilder line = new StringBuilder();
            for (int vertex : ordering.order) {
                line.append(SERVICES[vertex]).append(" -> ");
            }
            System.out.println("  " + line.substring(0, line.length() - 4));
        }
        Digraph withCycle = serviceGraph();
        withCycle.addEdge(5, 0, 1);
        System.out.println("  after adding Housing -> PowerGrid, ordering possible: "
                + TopologicalSort.run(withCycle).acyclic);

        System.out.println();
        System.out.println("=== One-way road network (SCC) ===");
        Digraph roads = roadNetwork();
        Tarjan.Result scc = Tarjan.run(roads);
        System.out.println("  components: " + scc.count);
        for (List<Integer> group : scc.groups()) {
            StringBuilder line = new StringBuilder();
            for (int vertex : group) {
                line.append(DISTRICTS[vertex]).append(", ");
            }
            System.out.println("  { " + line.substring(0, line.length() - 2) + " }");
        }

        System.out.println();
        System.out.println("=== Route planning (Dijkstra from Center) ===");
        Dijkstra.Result shortest = Dijkstra.run(roads, 0);
        for (int vertex = 0; vertex < roads.vertexCount(); vertex++) {
            if (!shortest.reachable(vertex)) {
                System.out.println("  " + DISTRICTS[vertex] + ": unreachable");
                continue;
            }
            StringBuilder path = new StringBuilder();
            for (int step : shortest.pathTo(vertex)) {
                path.append(DISTRICTS[step]).append(" -> ");
            }
            System.out.println("  " + DISTRICTS[vertex] + ": " + shortest.distance[vertex]
                    + "  [" + path.substring(0, path.length() - 4) + "]");
        }
    }

    private static Graph cityGraph() {
        Graph graph = new Graph(DISTRICTS.length);
        graph.addEdge(0, 1, 7);
        graph.addEdge(0, 2, 5);
        graph.addEdge(0, 3, 9);
        graph.addEdge(1, 2, 8);
        graph.addEdge(1, 4, 7);
        graph.addEdge(2, 3, 15);
        graph.addEdge(2, 4, 6);
        graph.addEdge(3, 4, 8);
        graph.addEdge(3, 5, 5);
        graph.addEdge(4, 5, 8);
        graph.addEdge(4, 6, 9);
        graph.addEdge(5, 6, 11);
        return graph;
    }

    private static Digraph serviceGraph() {
        Digraph graph = new Digraph(SERVICES.length);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 3, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 4, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(2, 5, 1);
        graph.addEdge(4, 5, 1);
        return graph;
    }

    private static Digraph roadNetwork() {
        Digraph graph = new Digraph(DISTRICTS.length);
        graph.addEdge(0, 1, 7);
        graph.addEdge(1, 2, 8);
        graph.addEdge(2, 0, 5);
        graph.addEdge(1, 4, 7);
        graph.addEdge(4, 3, 8);
        graph.addEdge(3, 5, 5);
        graph.addEdge(5, 4, 8);
        graph.addEdge(0, 3, 9);
        return graph;
    }
}
