
public class TestGraph {
    public static void main(String[] args) {
        System.out.println("Тестирование неориентированного графа");
        Graph<String> undirectedGraph = new Graph<>();

        undirectedGraph.addVertex("A");
        undirectedGraph.addVertex("B");
        undirectedGraph.addVertex("C");
        undirectedGraph.addVertex("D");
        undirectedGraph.addVertex("E");
       
        undirectedGraph.addEdge("A", "B");
        undirectedGraph.addEdge("A", "C");
        undirectedGraph.addEdge("B", "D");
        undirectedGraph.addEdge("C", "D");
        undirectedGraph.addEdge("D", "E");
        
        System.out.println(undirectedGraph);
        
        System.out.println("Обход из вершины A:");
        undirectedGraph.dfs("A");
        undirectedGraph.bfs("A");
        
        System.out.println("\nОбход из вершины D:");
        undirectedGraph.dfs("D");
        undirectedGraph.bfs("D");
        System.out.println("\nСмежные вершины для D: " + undirectedGraph.getAdjacent("D"));
        
        System.out.println("\nПосле удаления ребра B-D ");
        undirectedGraph.removeEdge("B", "D");
        System.out.println(undirectedGraph);
        
        System.out.println("Обход из вершины A после удаления ребра:");
        undirectedGraph.dfs("A");
        
        System.out.println("\n После удаления вершины C");
        undirectedGraph.removeVertex("C");
        System.out.println(undirectedGraph);

        System.out.println("\nТестирование ориентированного графа ");
        Graph<Integer> directedGraph = new Graph<>(true);
        
        directedGraph.addEdge(1, 2);
        directedGraph.addEdge(1, 3);
        directedGraph.addEdge(2, 4);
        directedGraph.addEdge(3, 4);
        directedGraph.addEdge(4, 5);
        
        System.out.println(directedGraph);
        
        System.out.println("Обход из вершины 1:");
        directedGraph.dfs(1);
        directedGraph.bfs(1);
        System.out.println("\nСмежные вершины для 1: " + directedGraph.getAdjacent(1));
        System.out.println("Смежные вершины для 4: " + directedGraph.getAdjacent(4));
  
        System.out.println("\nТестирование взвешенного графа");
        Graph<String> weightedGraph = new Graph<>();
        
        weightedGraph.addEdge("Moscow", "SPB", 700);
        weightedGraph.addEdge("Moscow", "Kazan", 800);
        weightedGraph.addEdge("SPB", "Kazan", 1200);
        
        System.out.println(weightedGraph);
        System.out.println("Обход из Moscow:");
        weightedGraph.dfs("Moscow");

        System.out.println("\nГраф с символьными вершинами");
        Graph<Character> charGraph = new Graph<>();
        
        charGraph.addEdge('A', 'B');
        charGraph.addEdge('B', 'C');
        charGraph.addEdge('C', 'D');
        charGraph.addEdge('A', 'D');
        
        System.out.println(charGraph);
        charGraph.dfs('A');
        charGraph.bfs('A');
        
        System.out.println("\nПроверка существования");
        System.out.println("Существует ли вершина 'A'? " + undirectedGraph.containsVertex("A"));
        System.out.println("Существует ли вершина 'X'? " + undirectedGraph.containsVertex("X"));
        System.out.println("Существует ли ребро A-B? " + undirectedGraph.containsEdge("A", "B"));
        System.out.println("Существует ли ребро A-X? " + undirectedGraph.containsEdge("A", "X"));
        
        System.out.println("Количество вершин: " + undirectedGraph.getVertexCount());
        System.out.println("Количество ребер: " + undirectedGraph.getEdgeCount());
        

    }
}