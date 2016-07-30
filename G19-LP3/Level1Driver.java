import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by sags on 3/27/16.
 */
public class Level1Driver {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        Graph g = Graph.readGraph(in,true);

        Timer time=new Timer();

        Stack<Vertex> top_order= ShortestAlgo.toplogicalOrder2(g);
        if(top_order!=null && !top_order.isEmpty())
            ShortestAlgo.DAG_sp(top_order, g, true);
        else if(g.flag==1)
            ShortestAlgo.BFS_sp(g, true);
        else if(g.flag==2)
            ShortestAlgo.Dijkstra_sp(g, true);
        else if(g.flag==3){
            BellmanFord bf=new BellmanFord();
            time.start();
            final Vertex result = bf.bellmanFord(g,true);

            if(result==null)
                bf.printVertexDistance(g,"B-F");
            else{
                System.out.println("Unable to solve problem. Graph has a negative cycle");
                bf.findNegativeOrZeroCycle(g, result);
            }
            time.end();
            System.out.println("Time taken to run the algorithm: " + time);
        }
    }
}
