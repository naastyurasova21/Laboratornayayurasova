
public class Graph<V> {
    private static final int INITIAL_CAPACITY = 16;
    private Vertex<V>[] vertices;
    private int size;
    private final boolean isDirected;
    
    @SuppressWarnings("unchecked")
    public Graph(boolean isDirected) {
        this.vertices = new Vertex[INITIAL_CAPACITY];
        this.size = 0;
        this.isDirected = isDirected;
    }
    
    public Graph() {
        this(false);
    }
    
    private static class Vertex<V> {
        V value;
        EdgeList<V> edges;
        
        Vertex(V value) {
            this.value = value;
            this.edges = new EdgeList<>();
        }
    }
    
    private static class EdgeList<V> {
        private static final int INITIAL_CAPACITY = 8;
        private Vertex<V>[] targetVertices;
        private int[] weights;
        private int size;
        
        @SuppressWarnings("unchecked")
        EdgeList() {
            this.targetVertices = new Vertex[INITIAL_CAPACITY];
            this.weights = new int[INITIAL_CAPACITY];
            this.size = 0;
        }
        
        void addEdge(Vertex<V> target, int weight) {
            if (size == targetVertices.length) {
                resizeArrays();
            }
            targetVertices[size] = target;
            weights[size] = weight;
            size++;
        }
        
        void removeEdge(Vertex<V> target) {
            for (int i = 0; i < size; i++) {
                if (targetVertices[i] == target) {
                    for (int j = i; j < size - 1; j++) {
                        targetVertices[j] = targetVertices[j + 1];
                        weights[j] = weights[j + 1];
                    }
                    targetVertices[size - 1] = null;
                    size--;
                    break;
                }
            }
        }
        
        boolean containsEdge(Vertex<V> target) {
            for (int i = 0; i < size; i++) {
                if (targetVertices[i] == target) {
                    return true;
                }
            }
            return false;
        }
        
        int getWeight(Vertex<V> target) {
            for (int i = 0; i < size; i++) {
                if (targetVertices[i] == target) {
                    return weights[i];
                }
            }
            return -1;
        }
        
        int size() {
            return size;
        }
        
        Vertex<V> getTarget(int index) {
            if (index < 0 || index >= size) return null;
            return targetVertices[index];
        }
        
        int getWeight(int index) {
            if (index < 0 || index >= size) return -1;
            return weights[index];
        }
        
        @SuppressWarnings("unchecked")
        private void resizeArrays() {
            int newCapacity = targetVertices.length * 2;
            Vertex<V>[] newTargets = new Vertex[newCapacity];
            int[] newWeights = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                newTargets[i] = targetVertices[i];
                newWeights[i] = weights[i];
            }
            
            targetVertices = newTargets;
            weights = newWeights;
        }
    }
    
    private static class Stack<T> {
        private static final int INITIAL_CAPACITY = 16;
        private Object[] elements;
        private int size;
        
        Stack() {
            this.elements = new Object[INITIAL_CAPACITY];
            this.size = 0;
        }
        
        void push(T element) {
            if (size == elements.length) {
                resizeElements();
            }
            elements[size++] = element;
        }
        
        @SuppressWarnings("unchecked")
        T pop() {
            if (size == 0) throw new IllegalStateException("Stack is empty");
            T element = (T) elements[--size];
            elements[size] = null;
            return element;
        }
        
        boolean isEmpty() {
            return size == 0;
        }
        
        private void resizeElements() {
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
    
    private static class Queue<T> {
        private static final int INITIAL_CAPACITY = 16;
        private Object[] elements;
        private int head;
        private int tail;
        private int size;
        
        Queue() {
            this.elements = new Object[INITIAL_CAPACITY];
            this.head = 0;
            this.tail = 0;
            this.size = 0;
        }
        
        void offer(T element) {
            if (size == elements.length) {
                resize();
            }
            elements[tail] = element;
            tail = (tail + 1) % elements.length;
            size++;
        }
        
        @SuppressWarnings("unchecked")
        T poll() {
            if (size == 0) throw new IllegalStateException("Queue is empty");
            T element = (T) elements[head];
            elements[head] = null;
            head = (head + 1) % elements.length;
            size--;
            return element;
        }
        
        boolean isEmpty() {
            return size == 0;
        }
        
        private void resize() {
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[(head + i) % elements.length];
            }
            elements = newElements;
            head = 0;
            tail = size;
        }
    }
    public static class List<T> {
        private static final int INITIAL_CAPACITY = 16;
        private Object[] elements;
        private int size;
        
        public List() {
            this.elements = new Object[INITIAL_CAPACITY];
            this.size = 0;
        }
        
        public void add(T element) {
            if (size == elements.length) {
                resizeElements();
            }
            elements[size++] = element;
        }
        
        @SuppressWarnings("unchecked")
        public T get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            return (T) elements[index];
        }
        
        public boolean contains(T element) {
            if (element == null) {
                for (int i = 0; i < size; i++) {
                    if (elements[i] == null) return true;
                }
            } else {
                for (int i = 0; i < size; i++) {
                    if (element.equals(elements[i])) return true;
                }
            }
            return false;
        }
        
        public int size() {
            return size;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < size; i++) {
                if (i > 0) sb.append(", ");
                sb.append(elements[i]);
            }
            sb.append("]");
            return sb.toString();
        }
        
        private void resizeElements() {
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
    
    private Vertex<V> findVertex(V value) {
        for (int i = 0; i < size; i++) {
            Vertex<V> vertex = vertices[i];
            if (vertex.value.equals(value)) {
                return vertex;
            }
        }
        return null;
    }
    
    private int findVertexIndex(V value) {
        for (int i = 0; i < size; i++) {
            Vertex<V> vertex = vertices[i];
            if (vertex.value.equals(value)) {
                return i;
            }
        }
        return -1;
    }
    
    @SuppressWarnings("unchecked")
    private void resizeVertices() {
        int newCapacity = vertices.length * 2;
        Vertex<V>[] newVertices = new Vertex[newCapacity];
        for (int i = 0; i < size; i++) {
            newVertices[i] = vertices[i];
        }
        vertices = newVertices;
    }
    
    public void addVertex(V v) {
        if (v == null) throw new IllegalArgumentException("Vertex cannot be null");
        if (findVertex(v) != null) return;
        
        if (size == vertices.length) {
            resizeVertices();
        }
        vertices[size++] = new Vertex<>(v);
    }
    
    public void addEdge(V from, V to, int weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        
        addVertex(from);
        addVertex(to);
        
        Vertex<V> fromVertex = findVertex(from);
        Vertex<V> toVertex = findVertex(to);
        
        fromVertex.edges.addEdge(toVertex, weight);
        
        if (!isDirected) {
            toVertex.edges.addEdge(fromVertex, weight);
        }
    }
    
    public void addEdge(V from, V to) {
        addEdge(from, to, 1);
    }
    
    public void removeVertex(V v) {
        int index = findVertexIndex(v);
        if (index == -1) return;
        
        Vertex<V> vertexToRemove = vertices[index];
        
        for (int i = 0; i < size; i++) {
            Vertex<V> vertex = vertices[i];
            vertex.edges.removeEdge(vertexToRemove);
        }
        
        for (int i = index; i < size - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        vertices[--size] = null;
    }
    
    public void removeEdge(V from, V to) {
        Vertex<V> fromVertex = findVertex(from);
        Vertex<V> toVertex = findVertex(to);
        
        if (fromVertex == null || toVertex == null) return;
        
        fromVertex.edges.removeEdge(toVertex);
        
        if (!isDirected) {
            toVertex.edges.removeEdge(fromVertex);
        }
    }
    
    public List<V> getAdjacent(V v) {
        Vertex<V> vertex = findVertex(v);
        List<V> adjacent = new List<>();
        if (vertex == null) return adjacent;
        
        for (int i = 0; i < vertex.edges.size(); i++) {
            Vertex<V> target = vertex.edges.getTarget(i);
            adjacent.add(target.value);
        }
        return adjacent;
    }
    
    public void dfs(V start) {
        Vertex<V> startVertex = findVertex(start);
        if (startVertex == null) return;
        
        List<V> visited = new List<>();
        Stack<V> stack = new Stack<>();
        
        stack.push(start);
        
        System.out.print("DFS: ");
        
        while (!stack.isEmpty()) {
            V current = stack.pop();
            
            if (!visited.contains(current)) {
                System.out.print(current + " ");
                visited.add(current);
                
                List<V> adjacent = getAdjacent(current);
                for (int i = 0; i < adjacent.size(); i++) {
                V neighbor = adjacent.get(i);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
                }
            }
        }
        System.out.println();
    }
    
    public void bfs(V start) {
        Vertex<V> startVertex = findVertex(start);
        if (startVertex == null) return;
        
        List<V> visited = new List<>();
        Queue<V> queue = new Queue<>();
        
        queue.offer(start);
        visited.add(start);
        
        System.out.print("BFS: ");
        
        while (!queue.isEmpty()) {
            V current = queue.poll();
            System.out.print(current + " ");
            
            List<V> adjacent = getAdjacent(current);
            for (int i = 0; i < adjacent.size(); i++) {
                V neighbor = adjacent.get(i);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        System.out.println();
    }
    
    public boolean containsVertex(V v) {
        return findVertex(v) != null;
    }
    
    public boolean containsEdge(V from, V to) {
        Vertex<V> fromVertex = findVertex(from);
        Vertex<V> toVertex = findVertex(to);
        return fromVertex != null && toVertex != null && 
               fromVertex.edges.containsEdge(toVertex);
    }
    
    public int getVertexCount() {
        return size;
    }
    
    public int getEdgeCount() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            Vertex<V> vertex = vertices[i];
            count += vertex.edges.size();
        }
        return isDirected ? count : count / 2;
    }
    
    public boolean isDirected() {
        return isDirected;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(isDirected ? "directed" : "undirected").append("):\n");
        
        for (int i = 0; i < size; i++) {
            Vertex<V> vertex = vertices[i];
            sb.append(vertex.value).append(" -> ");
            
            List<V> adjacent = getAdjacent(vertex.value);
            sb.append(adjacent).append("\n");
        }
        
        return sb.toString();
    }
}