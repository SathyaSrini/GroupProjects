
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class EulerianGraph {

    private final int num_Vertices; // Number of Vertices
    private final Graph g; // Input Graph
    private static ArrayList pathVerts; // Static array list to store the paths in euler graph

    EulerianGraph(Graph g_In) {
        g = g_In;
        num_Vertices = g.numNodes;

    }

    
  
    // CORE FUNCTIONS //
    
      int checkEulerian() {

                                      // Check if all non-zero degree vertices are connected
        if (isConnected() == false) {

            return -1;
        }
                                      // Count vertices with odd degree
        int odd = 0;


        for (Vertex v_Iter : g) {

            if (v_Iter.Adj.size() % 2 == 0) {

            } else {

                odd++;

                if (odd == 1) {
                    pathVerts = new ArrayList();
                }

                pathVerts.add((Vertex) v_Iter);

            }
        }

        
        if (odd > 2) {
            return odd;
        }
        
       
        return (odd == 2) ? -2 : -3;   // If odd count is 2, then semi-eulerian.
                                       // If odd count is 0, then eulerian

    }
  
      public static void main(String args[]) throws FileNotFoundException {

        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        Graph g = Graph.readGraph(in, false);

        Queue<Vertex> Q = new LinkedList<>();

        for (Vertex u : g) {
            u.seen = false;
            u.parent = null;
            u.distance = Integer.MAX_VALUE;
        }

        EulerianGraph eulerGraph = new EulerianGraph(g);

        int res = eulerGraph.checkEulerian();

        if (res > 2) {
            System.out.println("Graph is not Eulerian with " + res + " odd vertices");
        } else {
            switch (res) 
            {
                case -1:
                    System.out.println("Graph is not Connected");
                    break;
                case -2:
                    printEulerPath();
                    break;
                case -3:
                    System.out.println("Graph has a Euler cycle");
                    break;
                default :
                    System.out.println("Something is wrong");
                    break;
            }
        }

    }
    
      void performBFS(Vertex src) {
        Queue<Vertex> Q = new LinkedList<>();

        if (!src.seen) {
            src.distance = 0;
            Q.add(src);
            src.seen = true;
            while (!Q.isEmpty()) {
                Vertex u = Q.remove();
                for (Edge e : u.Adj) {
                    Vertex v = e.otherEnd(u);
                    if (!v.seen) {
                        v.seen = true;
                        v.parent = u;
                        v.distance = u.distance + 1;
                        Q.add(v);
                    }
                }
            }

        } else {
            performBFS(src);
        }

    }

    // HELPER FUNCTIONS //
    
      boolean isConnected() {

        int i = 0;

        Vertex v_local = null;

        for (Vertex v_Iter : g) {
            v_Iter.seen = false;
        }
        
        for (Vertex v_Iter : g)        // Find a vertex with non-zero degree
        {                                
            if (v_Iter.Adj.isEmpty()) 
            {
                i++;
            } else {
                v_local = v_Iter;
                break;
            }
        }

                                       // If  no edges in the graph, return true
        if (i == num_Vertices) {
            return true;
        }
        
        performBFS(v_local);          // Start DFS from non-zero degree

        for (Vertex v_iter : g)       // Check if all non-zero degree vertices are visited
        {
            if (v_iter.seen == false && v_iter.Adj.size() > 0) {
                return false;
            }
        }

        return true;

    }

      static void printEulerPath() 
    {
        System.out.print("Graph has a Euler path between ");

        if (!pathVerts.isEmpty()) {
            for (Object iter : pathVerts) {
                System.out.print((Vertex) iter);

                if (!iter.equals(pathVerts.get(pathVerts.size() - 1))) {
                    System.out.print(" and ");
                }

            }

        }
    }

}