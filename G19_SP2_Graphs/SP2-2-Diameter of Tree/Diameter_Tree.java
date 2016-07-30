import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Diameter_Tree {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
        Graph g = Graph.readGraph(in, false);   // read undirected graph from stream "in"


        for (Vertex u : g) {
            u.seen = false;
            u.parent = null;
            u.distance = Integer.MAX_VALUE;
        }

        // Run BFS on every component
        if(g.iterator().hasNext()){
        int dm=Diameter(g);
        if(dm==-1) System.out.println("It is not a tree, has cycle");
        else System.out.println("The diameter of the tree is:"+dm);
        }
        else
            System.out.println("Graph is empty");
    }
    public static int Diameter(Graph g){
        Queue<Vertex> Q = new LinkedList<Vertex>();
        Vertex src=g.iterator().next();
        int max=0,i=0; Vertex v1=new Vertex(0);
        while(i<2)
        {
            max=0;
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
                            if(v.distance>max){
                                max=v.distance;
                                v1=v;
                            }
                            Q.add(v);
                        } else {
                            if (u.distance == v.distance) {
                                return -1;
                            }
                        }
                    }
                }
            }
            clear(g);
            src=v1; src.seen=false;
            i++;
        }
        return max+1;
    }
    public static void clear(Graph g){
        for(Vertex u: g){
            u.seen = false;
            u.parent = null;
            u.distance = Integer.MAX_VALUE;
        }
    }
}
