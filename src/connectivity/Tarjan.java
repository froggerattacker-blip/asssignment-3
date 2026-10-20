package connectivity;

import graph.Digraph;
import graph.Edge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Tarjan {
    public static class Result {
        public final int[] component;
        public final int count;
        public final long operations;

        public Result(int[] component, int count, long operations) {
            this.component = component;
            this.count = count;
            this.operations = operations;
        }

        public List<List<Integer>> groups() {
            List<List<Integer>> groups = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                groups.add(new ArrayList<>());
            }
            for (int vertex = 0; vertex < component.length; vertex++) {
                groups.get(component[vertex]).add(vertex);
            }
            return groups;
        }
    }

    private final Digraph graph;
    private final int[] index;
    private final int[] low;
    private final int[] component;
    private final boolean[] onStack;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private int counter;
    private int components;
    private long operations;

    private Tarjan(Digraph graph) {
        this.graph = graph;
        int vertices = graph.vertexCount();
        this.index = new int[vertices];
        this.low = new int[vertices];
        this.component = new int[vertices];
        this.onStack = new boolean[vertices];
        Arrays.fill(index, -1);
    }

    public static Result run(Digraph graph) {
        Tarjan tarjan = new Tarjan(graph);
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (tarjan.index[vertex] < 0) {
                tarjan.visit(vertex);
            }
        }
        return new Result(tarjan.component, tarjan.components, tarjan.operations);
    }

    private void visit(int vertex) {
        index[vertex] = counter;
        low[vertex] = counter;
        counter++;
        stack.push(vertex);
        onStack[vertex] = true;
        for (Edge edge : graph.adjacent(vertex)) {
            operations++;
            if (index[edge.to] < 0) {
                visit(edge.to);
                low[vertex] = Math.min(low[vertex], low[edge.to]);
            } else if (onStack[edge.to]) {
                low[vertex] = Math.min(low[vertex], index[edge.to]);
            }
        }
        if (low[vertex] == index[vertex]) {
            while (true) {
                int member = stack.pop();
                onStack[member] = false;
                component[member] = components;
                if (member == vertex) {
                    break;
                }
            }
            components++;
        }
    }
}
