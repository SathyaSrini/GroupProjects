/**
 * Created by sags on 3/27/16.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class Level2Driver {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        Graph g = Graph.readGraph(in,true);

        Stack<Vertex> top_order= ShortestAlgo.toplogicalOrder2(g);
        if(top_order!=null && !top_order.isEmpty())
            ShortestAlgo.DAG_sp(top_order, g, false);
        else if(g.flag==1)
            ShortestAlgo.BFS_sp(g, false);
        else if(g.flag==2)
            ShortestAlgo.Dijkstra_sp(g, false);
        else if(g.flag==3){
            Timer time=new Timer();
            time.start();

            final Vertex result = BellmanFord.bellmanFord(g,false);
            if(result==null)
                BellmanFord.printVertexDistance(g,"B-F");
            else{
                System.out.println("Non-positive cycle in graph. DAC is not applicable");
                BellmanFord.findNegativeOrZeroCycle(g, result);
            }
            time.end();
            System.out.println("Time taken to run the algorithm"+time);
        }
    }
}

