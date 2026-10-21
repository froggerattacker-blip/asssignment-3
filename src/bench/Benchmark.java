package bench;

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

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Benchmark {
    private static final String[] NAMES = {"Small", "Medium", "Large"};
    private static final int[] VERTICES = {10, 100, 1000};
    private static final int[] EDGES = {30, 500, 5000};
    private static final long SEED = 20251021L;
    private static final int WARMUPS = 3;
    private static final int REPEATS = 5;

    public static void run() throws IOException {
        Files.createDirectories(Paths.get("results/tables"));
        Files.createDirectories(Paths.get("results/plots"));

        List<String[]> mstRows = new ArrayList<>();
        List<String[]> dsuRows = new ArrayList<>();
        List<String[]> connectivityRows = new ArrayList<>();
        List<String[]> dijkstraRows = new ArrayList<>();

        double[] vertexAxis = new double[NAMES.length];
        double[] edgeAxis = new double[NAMES.length];
        double[] kruskalTimes = new double[NAMES.length];
        double[] primTimes = new double[NAMES.length];
        double[] sccTimes = new double[NAMES.length];
        double[] topoTimes = new double[NAMES.length];
        double[] dijkstraTimes = new double[NAMES.length];
        double[] optimizedHops = new double[NAMES.length];
        double[] naiveHops = new double[NAMES.length];

        for (int i = 0; i < NAMES.length; i++) {
            int vertices = VERTICES[i];
            int edges = EDGES[i];
            vertexAxis[i] = vertices;
            edgeAxis[i] = edges;

            Graph undirected = GraphGenerator.connectedGraph(vertices, edges, SEED + i);
            Digraph dag = GraphGenerator.dag(vertices, edges, SEED + i);
            Digraph cyclic = GraphGenerator.randomDigraph(vertices, edges, SEED + i);

            MstResult kruskal = Kruskal.run(undirected);
            MstResult prim = Prim.run(undirected);
            kruskalTimes[i] = measure(() -> Kruskal.run(undirected));
            primTimes[i] = measure(() -> Prim.run(undirected));
            mstRows.add(new String[]{
                    NAMES[i], String.valueOf(vertices), String.valueOf(edges),
                    time(kruskalTimes[i]), time(primTimes[i]),
                    String.valueOf(kruskal.weight), String.valueOf(prim.weight),
                    String.valueOf(kruskal.edgeChecks), String.valueOf(prim.edgeChecks),
                    kruskal.connected ? "yes" : "no"});

            long[] optimized = unionAll(undirected, true);
            long[] naive = unionAll(undirected, false);
            optimizedHops[i] = optimized[2];
            naiveHops[i] = naive[2];
            dsuRows.add(new String[]{
                    NAMES[i], String.valueOf(vertices), String.valueOf(edges),
                    String.valueOf(optimized[0]), String.valueOf(optimized[1]),
                    String.valueOf(optimized[2]), String.valueOf(naive[2]),
                    String.format("%.2f", naive[2] / (double) Math.max(1, optimized[2]))});

            TopologicalSort.Result dagOrder = TopologicalSort.run(dag);
            TopologicalSort.Result cyclicOrder = TopologicalSort.run(cyclic);
            Tarjan.Result scc = Tarjan.run(cyclic);
            topoTimes[i] = measure(() -> TopologicalSort.run(dag));
            sccTimes[i] = measure(() -> Tarjan.run(cyclic));
            connectivityRows.add(new String[]{
                    NAMES[i], String.valueOf(vertices), String.valueOf(edges),
                    time(topoTimes[i]), String.valueOf(dagOrder.operations),
                    dagOrder.acyclic ? "yes" : "no", cyclicOrder.acyclic ? "no" : "yes",
                    time(sccTimes[i]), String.valueOf(scc.operations), String.valueOf(scc.count),
                    String.valueOf(largestComponent(scc))});

            Dijkstra.Result shortest = Dijkstra.run(cyclic, 0);
            dijkstraTimes[i] = measure(() -> Dijkstra.run(cyclic, 0));
            int reachable = 0;
            long longest = 0;
            for (int vertex = 0; vertex < vertices; vertex++) {
                if (shortest.reachable(vertex)) {
                    reachable++;
                    longest = Math.max(longest, shortest.distance[vertex]);
                }
            }
            dijkstraRows.add(new String[]{
                    NAMES[i], String.valueOf(vertices), String.valueOf(edges),
                    time(dijkstraTimes[i]), String.valueOf(shortest.operations),
                    reachable + " / " + vertices, String.valueOf(vertices - reachable),
                    String.valueOf(longest)});
        }

        writeTable("results/tables/mst.md", "MST: Kruskal vs Prim",
                new String[]{"Graph", "V", "E", "Kruskal, ms", "Prim, ms", "Kruskal weight", "Prim weight",
                        "Kruskal edge checks", "Prim edge checks", "Connected"}, mstRows);
        writeTable("results/tables/dsu.md", "DSU: optimized vs naive",
                new String[]{"Graph", "V", "E", "find calls", "union calls", "hops (compressed + rank)",
                        "hops (naive)", "speedup in hops"}, dsuRows);
        writeTable("results/tables/connectivity.md", "Topological ordering and SCC",
                new String[]{"Graph", "V", "E", "Topo, ms", "Topo ops", "DAG ordered", "Cycle detected",
                        "SCC, ms", "SCC ops", "SCC count", "Largest SCC"}, connectivityRows);
        writeTable("results/tables/dijkstra.md", "Dijkstra from district 0",
                new String[]{"Graph", "V", "E", "Time, ms", "Edge relaxations", "Reachable", "Unreachable",
                        "Longest shortest path"}, dijkstraRows);

        List<Plot.Series> byAlgorithm = List.of(
                new Plot.Series("Kruskal", vertexAxis, kruskalTimes, new Color(0x1F77B4)),
                new Plot.Series("Prim", vertexAxis, primTimes, new Color(0xD62728)),
                new Plot.Series("SCC (Tarjan)", vertexAxis, sccTimes, new Color(0x2CA02C)),
                new Plot.Series("Topological sort", vertexAxis, topoTimes, new Color(0xFF7F0E)),
                new Plot.Series("Dijkstra", vertexAxis, dijkstraTimes, new Color(0x9467BD)));
        Plot.lineChart("results/plots/time_vs_vertices.png", "Execution time vs number of vertices",
                "Vertices (V)", "Time, ms", byAlgorithm);

        List<Plot.Series> byEdges = new ArrayList<>();
        for (Plot.Series line : byAlgorithm) {
            byEdges.add(new Plot.Series(line.name, edgeAxis, line.y, line.color));
        }
        Plot.lineChart("results/plots/time_vs_edges.png", "Execution time vs number of edges",
                "Edges (E)", "Time, ms", byEdges);

        Plot.lineChart("results/plots/mst_kruskal_vs_prim.png", "Kruskal vs Prim", "Edges (E)", "Time, ms",
                List.of(new Plot.Series("Kruskal", edgeAxis, kruskalTimes, new Color(0x1F77B4)),
                        new Plot.Series("Prim", edgeAxis, primTimes, new Color(0xD62728))));

        Plot.lineChart("results/plots/dsu_optimizations.png", "DSU parent hops: with and without optimizations",
                "Vertices (V)", "Parent pointer hops",
                List.of(new Plot.Series("path compression + union by rank", vertexAxis, optimizedHops,
                                new Color(0x2CA02C)),
                        new Plot.Series("no compression, no rank", vertexAxis, naiveHops, new Color(0xD62728))));

        System.out.println("Tables written to results/tables, plots written to results/plots");
        printTable(new String[]{"Graph", "Kruskal ms", "Prim ms", "Weight"}, mstRows, new int[]{0, 3, 4, 5});
    }

    private static long[] unionAll(Graph graph, boolean optimized) {
        DSU dsu = new DSU(graph.vertexCount(), optimized);
        for (Edge edge : graph.edges()) {
            dsu.union(edge.from, edge.to);
        }
        return new long[]{dsu.findCalls(), dsu.unionCalls(), dsu.pointerUpdates()};
    }

    private static int largestComponent(Tarjan.Result scc) {
        int[] sizes = new int[scc.count];
        for (int component : scc.component) {
            sizes[component]++;
        }
        return Arrays.stream(sizes).max().orElse(0);
    }

    private static double measure(Runnable task) {
        for (int i = 0; i < WARMUPS; i++) {
            task.run();
        }
        long total = 0;
        for (int i = 0; i < REPEATS; i++) {
            long start = System.nanoTime();
            task.run();
            total += System.nanoTime() - start;
        }
        return total / (double) REPEATS / 1_000_000.0;
    }

    private static String time(double millis) {
        return String.format("%.3f", millis);
    }

    private static void writeTable(String path, String title, String[] header, List<String[]> rows)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("# " + title);
            writer.println();
            writer.println("| " + String.join(" | ", header) + " |");
            writer.println("|" + "---|".repeat(header.length));
            for (String[] row : rows) {
                writer.println("| " + String.join(" | ", row) + " |");
            }
        }
    }

    private static void printTable(String[] header, List<String[]> rows, int[] columns) {
        System.out.println(String.join("\t", header));
        for (String[] row : rows) {
            StringBuilder line = new StringBuilder();
            for (int column : columns) {
                line.append(row[column]).append('\t');
            }
            System.out.println(line.toString().trim());
        }
    }
}
