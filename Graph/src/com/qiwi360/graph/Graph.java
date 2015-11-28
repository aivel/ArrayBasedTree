package com.qiwi360.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

/**
 * Created by Max on 17.11.2015.
 */
public class Graph<TVal, TWeight> {
    private List<Vertex> vertices = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public class Vertex {
        private TVal value;

        private List<Edge> incidentEdges = new ArrayList<>();

        public Vertex(TVal val) {
            value = val;
        }

        public TVal getValue() {
            return value;
        }

        public List<Vertex> adjacent() {
            return incidentEdges
                    .stream()
                    .map(edge -> edge.to.equals(this) ? edge.from : edge.to)
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        public void addIncidentEdge(Edge edge) {
            incidentEdges.add(edge);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Graph.Vertex)) return false;

            Vertex vertex = (Vertex) o;

            return value.equals(vertex.value);
        }

        @Override
        public int hashCode() {
            int result = value.hashCode();
            result = 31 * result + incidentEdges.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public boolean removeIncidentEdge(Edge edge) {
            return incidentEdges.remove(edge);
        }
    }

    public class Edge {
        private Vertex from;

        private Vertex to;

        private TWeight weight;

        public Edge(Vertex from, Vertex to, TWeight weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public TWeight getWeight() {
            return weight;
        }

        public Vertex getTo() {
            return to;
        }

        public Vertex getFrom() {
            return from;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "from=" + from +
                    ", to=" + to +
                    ", weight=" + weight +
                    '}';
        }
    }

    public void addVertex(TVal value) {
        vertices.add(new Vertex(value));
    }

    public void removeVertex(Vertex vertex) {
        vertices.remove(vertex);
    }

    public void addEdge(Vertex from, Vertex to, TWeight weight) {
        Edge edge = new Edge(from, to, weight);

        from.addIncidentEdge(edge);
        to.addIncidentEdge(edge);

        edges.add(edge);
    }

    public void addEdge(TVal from, TVal to, TWeight weight) {
        Vertex fromVertex = getVertex(from);
        Vertex toVertex = getVertex(to);

        if (fromVertex != null && toVertex != null) {
            addEdge(fromVertex, toVertex, weight);
        }
    }

    private Vertex getVertex(TVal from) {
        return vertices
                .stream()
                .filter(vertex -> vertex.value.equals(from))
                .findFirst().orElseGet(() -> null);
    }

    public void removeEdge(Vertex from, Vertex to) {
        List<Edge> edgesToRemoveList = edges
                .stream().filter(edge -> edge.from.equals(from) && edge.to.equals(to))
                .collect(Collectors.toCollection(LinkedList::new));

        edgesToRemoveList.forEach((edge) -> { edge.from.removeIncidentEdge(edge); edge.to.removeIncidentEdge(edge);});

        edges.removeAll(edgesToRemoveList);
    }

    public void removeEdge(TVal from, TVal to) {
        Vertex fromVertex = getVertex(from);
        Vertex toVertex = getVertex(to);

        if (fromVertex != null && toVertex != null) {
            removeEdge(fromVertex, toVertex);
        }
    }

    private void removeEdge(Edge edge) {
        removeEdge(edge.from, edge.to);
    }

    @Override
    protected Graph<TVal, TWeight> clone() throws CloneNotSupportedException {
        Graph<TVal, TWeight> graph = new Graph<>();

        edges.forEach(graph.edges::add);
        vertices.forEach(graph.vertices::add);

        return graph;
    }

    interface IVisitor<T> {
        void visit(T val);
    }

    public static class CountVisitor<T> implements IVisitor<T> {
        public int count = 0;

        @Override
        public void visit(T val) {
            count++;
        }

        public void clear() {
            count = 0;
        }
    }

    public static class StubVisitor<T> implements IVisitor<T> {

        @Override
        public void visit(T val) {
            // just do nothing
        }
    }

    public static class FlagVisitor<T extends Comparable> implements IVisitor<T> {
        public boolean flag = false;
        private T val;

        public FlagVisitor(T val) {
            this.val = val;
        }

        @Override
        public void visit(T val) {
            if (!flag) {
                flag = this.val.equals(val);
            }
        }

        public void clear() {
            flag = false;
        }

        public void setVal(T val) {
            this.val = val;
        }
    }

    private void DFSTraverse(TVal from, IVisitor<TVal> visitor) {
        Stack<Graph<TVal, TWeight>.Vertex> stack = new Stack<>();
        Graph<TVal, TWeight>.Vertex startingPoint = getVertex(from);
        Set<Graph<TVal, TWeight>.Vertex> visited = new HashSet<>();

        stack.push(startingPoint);

        while (!stack.isEmpty()) {
            Graph<TVal, TWeight>.Vertex currentVertex = stack.pop();

            if (!visited.contains(currentVertex)) {
                visitor.visit(currentVertex.value);
                visited.add(currentVertex);
                currentVertex.adjacent().stream().filter(vertex -> !visited.contains(vertex)).forEach(stack::push);
            }
        }
    }

    private void BFSTraverse(TVal from, IVisitor<TVal> visitor) {
        LinkedTransferQueue<Graph<TVal, TWeight>.Vertex> queue = new LinkedTransferQueue<>();
        Graph<TVal, TWeight>.Vertex startingPoint = getVertex(from);
        Set<Graph<TVal, TWeight>.Vertex> visited = new HashSet<>();

        queue.add(startingPoint);

        while (!queue.isEmpty()) {
            Graph<TVal, TWeight>.Vertex currentVertex = queue.remove();

            if (!visited.contains(currentVertex)) {
                visitor.visit(currentVertex.value);
                visited.add(currentVertex);
                currentVertex.adjacent().stream().filter(vertex -> !visited.contains(vertex)).forEach(queue::add);
            }
        }
    }

    static class Sorter<T extends Comparable> {
        // ---------- HEAP SORT --------------------------------------------------

        private void swap(T[] arr, int currentKey, int parentKey) {
            T tmpNode = arr[currentKey];
            arr[currentKey] = arr[parentKey];
            arr[parentKey] = tmpNode;
        }

        void shiftDown(T[] arr, int i, int j) {
            boolean done = false;
            int maximumChild;

            while ((i * 2 + 1 < j) && (!done)) {
                if (i * 2 + 1 == j - 1)
                    maximumChild = i * 2 + 1;
                else if (arr[i * 2 + 1].compareTo(arr[i * 2 + 2]) > 0)
                    maximumChild = i * 2 + 1;
                else
                    maximumChild = i * 2 + 2;

                if (arr[i].compareTo(arr[maximumChild]) < 0) {
                    swap(arr, i, maximumChild);
                    i = maximumChild;
                } else {
                    done = true;
                }
            }
        }

        public void heapSort(T[] arr) {
            for (int i = arr.length / 2 - 1; i >= 0; i--) {
                shiftDown(arr, i, arr.length);
            }

            for (int i = arr.length - 1; i >= 1; i--) {
                swap(arr, 0, i);
                shiftDown(arr, 0, i);
            }
        }

        // =========================================================================

        // ---------- MERGE SORT --------------------------------------------------

        private static int[] mergeSort(int[] arr) {
            if (arr.length < 2) {
                return arr;
            }

            int mid = arr.length / 2;
            int[] left = new int[mid];

            for (int i = 0; i < mid; i++) {
                left[i] = arr[i];
            }

            int rightSize = arr.length - mid;
            int[] right = new int[rightSize];

            for (int i = mid, j = 0; i < mid + rightSize; i++, j++) {
                right[j] = arr[i];
            }

            left = mergeSort(left);
            right = mergeSort(right);

            return merge(left, right);
        }

        private static int[] merge(int[] left, int[] right) {
            int[] result = new int[left.length + right.length];

            for (int l = 0, r = 0, i = 0; l < left.length || r < right.length; ) {

                if (l < left.length) {
                    if (r < right.length) {
                        if (left[l] < right[r]) {
                            result[i++] = left[l++];
                        } else {
                            result[i++] = right[r++];
                        }
                    } else {
                        result[i++] = left[l++];
                    }
                } else {
                    result[i++] = right[r++];
                }
            }

            return result;
        }

        // =========================================================================
    }

    private Graph<TVal, TWeight> applyFirstConstraint(Graph<TVal, TWeight> graph) throws CloneNotSupportedException {
        Graph<TVal, TWeight> newGraph = graph.clone();

        List<Edge> toDel = newGraph.edges.stream().filter(edge -> (edge.from.value.toString().endsWith("-R") && edge.to.value.toString().endsWith("-DU")) ||
                (edge.from.value.toString().endsWith("-DU") && edge.to.value.toString().endsWith("-R"))).collect(Collectors.toCollection(LinkedList::new));

        for (Edge tod: toDel) {
            newGraph.removeEdge(tod);
        }

        return newGraph;
    }

    private Graph<TVal, TWeight> applySecondConstraint(Graph<TVal, TWeight> graph) throws CloneNotSupportedException {
        Graph<TVal, TWeight> newGraph = graph.clone();

        List<Edge> toDel = newGraph.edges.stream().filter(edge -> (edge.from.value.toString().endsWith("-R") && edge.to.value.toString().endsWith("-DG")) ||
                (edge.from.value.toString().endsWith("-DG") && edge.to.value.toString().endsWith("-R"))).collect(Collectors.toCollection(LinkedList::new));

        for (Edge tod: toDel) {
            newGraph.removeEdge(tod);
        }

        return newGraph;
    }

    private static void writeToFile(String filename, String value) {
        try {
            File file = new File(filename);
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(value);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void problemA(Graph<TVal, TWeight> graph) {
        String[] citiesList = {"Donetsk-DU", "Kiev-U", "Lviv-U", "Batumi-G", "Rostov-R"};
        Sorter<String> sorter = new Sorter<>();
        StringJoiner resultJoiner = new StringJoiner("\n");

        for (String city: citiesList) {
            Graph<TVal, TWeight>.Vertex cityVertex = graph.getVertex((TVal)city);

            if (cityVertex != null) {
                String[] vertices = cityVertex.adjacent()
                        .stream().map(vertex -> vertex.toString()).toArray(String[]::new);
                sorter.heapSort(vertices);

                StringJoiner currentCityJoiner = new StringJoiner(" ");

                for (String vertex : vertices) {
                    currentCityJoiner.add(vertex);
                }

                resultJoiner.add(currentCityJoiner.toString());
            } else {
                resultJoiner.add("");
            }
        }

        writeToFile("around.txt", resultJoiner.toString());
    }

    private void problemB(Graph<TVal, TWeight> graph) throws CloneNotSupportedException {
        StringJoiner resultJoiner = new StringJoiner(" ");

        String startingPoint = "Rostov-R";
        CountVisitor<TVal> countVisitor = new CountVisitor<>();

        Graph<TVal, TWeight> newGraph = applyFirstConstraint(graph);
        newGraph = applySecondConstraint(newGraph);

        // first phase

        newGraph.DFSTraverse((TVal) startingPoint, countVisitor);

        resultJoiner.add(countVisitor.count == graph.vertices.size() ? "yes" : "no");

        countVisitor.clear();

        // second phase: there is a closed road

        String[] closedPaths = {"Vladikavkaz-R", "Tbilisi-G"};

        for (int i = 0; i < closedPaths.length; i += 2) {
            newGraph.removeEdge((TVal)closedPaths[i], (TVal)closedPaths[i + 1]);
        }

        newGraph.DFSTraverse((TVal)startingPoint, countVisitor);

        resultJoiner.add(countVisitor.count == graph.vertices.size() ? "yes" : "no");

        // result

        writeToFile("able.txt", resultJoiner.toString());
    }

    public class Heap<T> {
        private class HeapNode {
            private int key;
            private T value;

            public HeapNode(int key, T value) {
                this.key = key;
                this.value = value;
            }
        }

        protected ArrayList<HeapNode> nodes = new ArrayList<>();

        private int getLeftChildIndex(int i) {
            return 2 * i + 1;
        }

        private HeapNode getLeftChild(int i) {
            int leftChildIndex = getLeftChildIndex(i);

            if (leftChildIndex < nodes.size()) {
                HeapNode leftChild = nodes.get(leftChildIndex);

                if (leftChild != null) {
                    return leftChild;
                }
            }

            return null;
        }

        private int getRightChildIndex(int i) {
            return 2 * i + 2;
        }

        private HeapNode getRightChild(int i) {
            int rightChildIndex = getRightChildIndex(i);

            if (rightChildIndex < nodes.size()) {
                HeapNode rightChild = nodes.get(rightChildIndex);

                if (rightChild != null) {
                    return rightChild;
                }
            }

            return null;
        }

        private HeapNode getMaxChild(int index) {
            if (index < 0 || index >= nodes.size()) {
                return null;
            }

            HeapNode leftChild = getLeftChild(index);
            HeapNode rightChild = getRightChild(index);

            if (leftChild == null && rightChild == null) {
                // Node has no children
                return null;
            }

            // One of children is null

            if (leftChild == null) {
                return rightChild;
            }

            if (rightChild == null) {
                return leftChild;
            }

            return rightChild.key >= leftChild.key ? rightChild : leftChild;
        }

        private int getParent(int i) {
            return i < nodes.size() && i > 0 ? i / 2 : -1;
        }

        public void put(int key, T value) {
            nodes.add(new HeapNode(key, value));

            upHeap(nodes.size() - 1);
        }

        private void upHeap(int index) {
            while (index >= 0) {
                int parent = getParent(index);

                if (parent < 0) {
                    return;
                }

                if (nodes.get(index).key > nodes.get(parent).key) {
                    swap(index, parent);
                }

                index = parent;
            }
        }

        private void downHeap(int index) {
            int biggestChild;
            HeapNode start = nodes.get(index);

            while (index < nodes.size() / 2) {
                int leftChildIndex = getLeftChildIndex(index);
                int rightChildIndex = getRightChildIndex(index);

                if(rightChildIndex < nodes.size() && nodes.get(leftChildIndex).key < nodes.get(rightChildIndex).key) {
                    biggestChild = rightChildIndex;
                }
                else {
                    biggestChild = leftChildIndex;
                }

                if(start.key >= nodes.get(biggestChild).key) {
                    break;
                }
                else {
                    nodes.set(index, nodes.get(biggestChild));
                    index = biggestChild;
                }
            }

            nodes.set(index, start);
        }

        private void swap(int currentKey, int parentKey) {
            HeapNode tmpNode = nodes.get(currentKey);
            nodes.set(currentKey, nodes.get(parentKey));
            nodes.set(parentKey, tmpNode);
        }

        public T removeMax() {
            if (nodes.isEmpty()) {
                return null;
            }

            T val = nodes.get(0).value;

            if (nodes.size() > 1) {
                swap(0, nodes.size() - 1);
                nodes.remove(nodes.size() - 1);
                downHeap(0);
            } else {
                nodes.remove(0);
            }

            return val;
        }

        public HeapNode removeMaxNode() {
            if (nodes.isEmpty()) {
                return null;
            }

            HeapNode val = nodes.get(0);

            if (nodes.size() > 1) {
                swap(0, nodes.size() - 1);
                nodes.remove(nodes.size() - 1);
                downHeap(0);
            } else {
                nodes.remove(0);
            }

            return val;
        }

        class PriorityQueue<T> extends Heap<T> {
            public T peek() {
                return (T) removeMax();
            }

            public HeapNode peekNode() {
                return removeMaxNode();
            }

            public boolean isEmpty() {
                return nodes.isEmpty();
            }
        }

        public void heapSort(int[] arr) {
            for (int i = arr.length / 2 - 1; i >= 0; i--) {
                shiftDown(arr, i, arr.length);
            }

            for (int i = arr.length - 1; i >= 1; i--) {
                swap(arr, 0, i);
                shiftDown(arr, 0, i);
            }
        }

        private void swap(int[] arr, int currentKey, int parentKey) {
            int tmpNode = arr[currentKey];
            arr[currentKey] = arr[parentKey];
            arr[parentKey] = tmpNode;
        }

        void shiftDown(int[] arr, int i, int j) {
            boolean done = false;
            int maximumChild;

            while ((i * 2 + 1 < j) && (!done)) {
                if (i * 2 + 1 == j - 1)
                    maximumChild = i * 2 + 1;
                else if (arr[i * 2 + 1] > arr[i * 2 + 2])
                    maximumChild = i * 2 + 1;
                else
                    maximumChild = i * 2 + 2;

                if (arr[i] < arr[maximumChild]) {
                    swap(arr, i, maximumChild);
                    i = maximumChild;
                }
                else {
                    done = true;
                }
            }
        }
    }

    private List<TVal> BFSTravel(TVal from, TVal to) {
        /**
         * We'll go BFS just layer by layer. One level is one vertex further.
         * To fill the next layer we'll use the result of a previous one.
         * Then, once the destination point is found, we go back,
         * looking for the points that lead us to the one ahead.
         * Finally, we'll reach the starting point, having all the intermediate
         * points collected into the path.
         * Then we just reverse the path to make it ordered from Starting point
         * to Destination point.
         */

        Map<Integer, List<Vertex>> layers = new HashMap<>();
        Set<Vertex> visited = new HashSet<>();
        List<TVal> path = new LinkedList<>();

        LinkedList<Vertex> firstLayer = new LinkedList<>();
        firstLayer.add(getVertex(from));
        boolean found = false;

        layers.put(0, firstLayer);
        int layer = 0;

        while (layer < vertices.size()) {
            int nextLayer = layer + 1;
            LinkedList<Vertex> nextLayerList = new LinkedList<>();

            layers.put(nextLayer, nextLayerList);

            LinkedList<Vertex> prevLayer = (LinkedList<Vertex>) layers.get(layer);

            for (Vertex vertex: prevLayer) {
                if (vertex.value.equals(to)) {
                    // WOW, WIN!
                    path.add(vertex.value);

                    Vertex destVertex = vertex;

                    for (int lyr = layer - 1; lyr >= 0; lyr--) {
                        List<Vertex> layerList = layers.get(lyr);

                        for (Vertex potentialDestVertex: layerList) {
                            if (potentialDestVertex.adjacent().contains(destVertex)) {
                                path.add(potentialDestVertex.value);
                                destVertex = potentialDestVertex;
                                // Go back
                                break;
                            }
                        }
                    }

                    found = true;
                    break;
                }

                if (!visited.contains(vertex)) {
                    nextLayerList.addAll(vertex.adjacent().stream()
                            .filter(vert -> !visited.contains(vert))
                            .collect(Collectors.toCollection(LinkedList::new)));
                }

                visited.add(vertex);
            }

            if (found) {
                break;
            }

            layer = nextLayer;
        }

        Collections.reverse(path);

        return path;
    }

    private void problemC(Graph<TVal, TWeight> graph) {
        String[] from = {"Melitopol-U", "Sukhumi-DG"};
        String[] to = {"Rostov-R", "Lugansk-DU"};
        StringJoiner pathJoiner = new StringJoiner("\n");

        for (int i = 0; i < from.length; i++) {
            StringJoiner cityJoiner = new StringJoiner(" ");

            List<TVal> path = graph.BFSTravel((TVal) from[i], (TVal) to[i]);

            cityJoiner.add("" + (path.size() - 1));

            for (TVal val: path) {
                cityJoiner.add(val.toString());
            }

            pathJoiner.add(cityJoiner.toString());
        }

        writeToFile("travel.txt", pathJoiner.toString());
    }

    private void problemD(Graph<TVal, TWeight> graph) throws CloneNotSupportedException {
        String[] from = {"Melitopol-U", "Sukhumi-DG"};
        String[] to = {"Rostov-R", "Lugansk-DU"};
        StringJoiner pathJoiner = new StringJoiner("\n");

        graph = graph.applyFirstConstraint(graph);
        graph = graph.applySecondConstraint(graph);

        for (int i = 0; i < from.length; i++) {
            StringJoiner cityJoiner = new StringJoiner(" ");

            List<TVal> path = graph.BFSTravel((TVal) from[i], (TVal) to[i]);

            cityJoiner.add("" + (path.size() - 1));

            for (TVal val: path) {
                cityJoiner.add(val.toString());
            }

            pathJoiner.add(cityJoiner.toString());
        }

        writeToFile("travel-now.txt", pathJoiner.toString());
    }

    public static void main(String[] args) throws FileNotFoundException, CloneNotSupportedException {
        Graph<String, Integer> graph = new Graph<>();

        try (Scanner fileScanner = new Scanner(new File("cities.txt"))) {
            String firstLine = fileScanner.nextLine();
            String secondLine = fileScanner.nextLine();

            try (Scanner verticesScanner = new Scanner(firstLine)) {
                while (verticesScanner.hasNext()) {
                    String city = verticesScanner.next();

                    graph.addVertex(city);
                }
            }

            try (Scanner edgesScanner = new Scanner(secondLine)) {
                while (edgesScanner.hasNext()) {
                    String from = edgesScanner.next();
                    String to = edgesScanner.next();

                    graph.addEdge(from, to, 0);
                }
            }
        }

        graph.problemC(graph);
    }
}