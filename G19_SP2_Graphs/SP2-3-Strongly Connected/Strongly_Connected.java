import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Strongly_Connected {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
        Graph g = Graph.readGraph(in, true);   // read undirected graph from stream "in"
        clear(g);
        // Run DFS on every component

        if(g.iterator().hasNext()){
            int cnt=stronglyConnectedComponents(g);
            System.out.println("The number of strongly connected components are:"+cnt);
        }
        else
            System.out.println("Graph is empty");
    }
    public static int stronglyConnectedComponents(Graph g){
        Stack<Vertex> S=new Stack<Vertex>();
        Stack<Vertex> S1=new Stack<Vertex>();
        S=DFS_Initial(g,0,S);
        clear(g);
        S1=DFS_Initial(g,1,S);
        return S1.pop().name;
    }
    public static void clear(Graph g){
        for(Vertex u: g){
            u.seen = false;
            u.parent = null;
            u.distance = Integer.MAX_VALUE;
        }
    }
    /* DFS_Initial - DFS main loop, running through every DFS component
       g - Graph g provided as input, S - DFS stack
       flag - =>0 - does normal DFS using adjacency list
                1 - does DFS using reverse adjacency list - Graph Transpose.
    * */
    public static Stack DFS_Initial(Graph g,int flag,Stack<Vertex> S){
        for(Vertex u:g)
        if(!u.seen && flag==0)
            DFSVisit(u,S);
        else if(!u.seen && flag==1)
            DFSrVisit(u,S);
        return S;
    }
    /* DFSrVisit - Takes in the starting component and a stack from original DFS.
    *  count - to count the number of strongly connected components present in the graph.
    *
    * */
    public static void DFSrVisit(Vertex u,Stack<Vertex> S){
        int count=0;
        while(!S.isEmpty()){
            Vertex v=S.pop();
            if(!v.seen){
                v.seen=true;
                count++;
                DFSRVisit(v);
            }
       }
       S.push(new Vertex(count));
    }
    /*
    * DFSRVisit - This is to do the DFS in the reverse adjacency list.
    *
    * */
    public static void DFSRVisit(Vertex u){
        u.seen=true;
        for(Edge e:u.revAdj){
            Vertex v=e.otherEnd(u);
            if(!v.seen){
                DFSRVisit(v);}
        }
    }
    /*
    *  This is to do the normal DFS traversal
    * */
    public static void DFSVisit(Vertex u,Stack<Vertex> S){
        u.seen=true;
        for(Edge e:u.Adj){
            Vertex v=e.otherEnd(u);
            if(!v.seen){
                v.parent=u;
                DFSVisit(v,S);}
        }
        S.push(u);
    }
}