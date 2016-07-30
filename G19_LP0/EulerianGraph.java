
import java.io.*;
import java.util.*;

public class EulerianGraph {

    private final int num_Vertices; // Number of Vertices
    private final Graph g; // Input Graph
    private static ArrayList pathVerts; // Static array list to store the paths in euler graph
    private static int numberOfEdges;
    private static int level=0,check_hw=0;
    private static Iterator<Vertex> vert;

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

    public static void main(String args[]) throws IOException {
        if(new File("output.txt").exists())
            new File("output.txt").delete();
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        Graph g = Graph.readGraph(in, false);
        vert=g.iterator();
        Queue<Vertex> Q = new LinkedList<Vertex>();

        for (Vertex u : g) {
            u.seen = false;
            u.parent = null;
            u.distance = Integer.MAX_VALUE;
        }

        EulerianGraph eulerGraph = new EulerianGraph(g);

        int res = eulerGraph.checkEulerian();

        //heirHolzerMain(g);
        if (res > 2) {
            outputToFile("Graph is not Eulerian with " + res + " odd vertices \n");
        } else {
            switch (res) {
                case -1:
                    outputToFile("Graph is not Connected \n");
                    break;
                case -2:
                    heirHolzerMain(g);
                    break;
                case -3:
                    outputToFile("Graph has a Euler cycle \n");
                    heirHolzerMain(g);
                    break;
                default:
                    outputToFile("Input issue, check input file \n");
                    break;
            }
        }

    }

    void performBFS(Vertex src) {
        Queue<Vertex> Q = new LinkedList<Vertex>();

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

        for (Vertex v_Iter : g) // Find a vertex with non-zero degree
        {
            if (!v_Iter.Adj.isEmpty()) {
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

        performBFS(v_local);          // Start BFS from non-zero degree

        for (Vertex v_iter : g) // Check if all non-zero degree vertices are visited
        {
            if (v_iter.seen == false && v_iter.Adj.size() > 0) {
                return false;
            }
        }

        return true;

    }
    static void outputToFile(String st) throws IOException {
        File outFile = new File ("output.txt");
        //if file doesnt exists, then create it
        if(!outFile.exists()){
            outFile.createNewFile();
        }
        FileWriter fWriter = new FileWriter (outFile,true);
        PrintWriter pWriter = new PrintWriter (fWriter);
        pWriter.print(st);
        //pWriter.println();
        pWriter.close();
    }
    public static Vertex next(Iterator<Vertex> it){
        if(it.hasNext())
            return it.next();
        else
            return null;
    }
    static void printTour(HashMap<Integer,List<Vertex>> path,int level,int traversed){
        if(path.size()==1)
            return;
        Iterator<Vertex> main_list= path.get(level).iterator();
        Iterator<Vertex> to_append= path.get(level+traversed).iterator();
        List<Vertex> path_final=new ArrayList<Vertex>();
        int flag=0;
        Vertex v1=next(main_list);
        Vertex v2=next(to_append);
        while (v1!=null){
           if(flag==1 && v2!=null)
           {
               path_final.add(v2);
               v2=next(to_append);
           }
            else{
                flag=0;
           }
            if(v1==v2 && flag==0){
                v1=next(main_list);
                flag=1;
            }
            else if(flag==0){
                path_final.add(v1);
                v1=next(main_list);
            }
        }
        path.remove(level+traversed);
        path.put(level,path_final);
        printTour(path,level,traversed+1);
    }
    static Vertex findPath(List<Vertex> path1, Vertex src) {

        Edge e = null;

        while (!src.Adj.isEmpty()) {

            e = src.Adj.get(0);

            Vertex v = e.otherEnd(src);

            e.remove();

            numberOfEdges--; //Keeping track of the edge count in the graph

            path1.add(v);
//            findPath(path1, v);
            src=v;
        }

        return src;

    }
    static void print_path(HashMap<Integer,List<Vertex>> path1) throws IOException {
        Iterator<Vertex> path_print=path1.get(1).iterator();
        Vertex temp=next(path_print);
        while (path_print.hasNext())
        {

            outputToFile("(" + temp);
            if(path_print.hasNext())
                outputToFile(",");
            temp=next(path_print);
            outputToFile(temp + ")");
            //System.out.print(path_print.next());
            outputToFile("\n");
        }
        outputToFile("\n");
    }
    static void heirHolzerMain(Graph g) throws IOException {

        HashMap<Integer,List<Vertex>> path1=new HashMap<Integer, List<Vertex>>();
        Vertex src = g.verts.get(1);
        numberOfEdges = g.edgeCount;
        long start_time=System.currentTimeMillis();
        path1 = heirholzerPath(src, path1);
        long end_time=System.currentTimeMillis();
        System.out.println("Time to run the algorithm : "+(end_time-start_time)+" milliseconds");
        if (numberOfEdges == 0 && path1!=null && check_hw==0) {
            printTour(path1,1,1);
            //System.out.println(path1);
            print_path(path1);
        } else {
            outputToFile("Starting and ending vertices are different\n");
            print_path(path1);
             //Edge count is zero
        }

    }

    static HashMap<Integer,List<Vertex>> heirholzerPath(Vertex src, HashMap<Integer,List<Vertex>> path1) throws IOException {

        level+=1;
        List<Vertex> v=new ArrayList<Vertex>();
        v.add(src);
        path1.put(level,v);

        Vertex returned = null;

        returned = findPath(v, src); // method to find the euler path using heirholzer's algorithm
        path1.put(level, v);
        if (returned == null || !returned.equals(src)) {
            outputToFile("Given graph does not have euler tour\n");
            check_hw=1;
            return path1; //Edge case where euler tour is wrongly reported
        }
        else {

            if(numberOfEdges>0){
                int flag=0;
                Iterator it=path1.entrySet().iterator();
                while (it.hasNext()){
                 Map.Entry pair=(Map.Entry)it.next();
                 List<Vertex> v1=(List<Vertex>)pair.getValue();
                 for(Vertex k:v1){
                     if (k.Adj.size()>0){
                         heirholzerPath(k,path1);
                         flag=1;
                         break;
                     }
                  }
                  if(flag==1) break;
                }
            }
            return path1;
        }

        //return null;
    }
}
