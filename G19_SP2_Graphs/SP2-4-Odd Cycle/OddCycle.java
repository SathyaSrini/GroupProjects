import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class OddCycle {
	
	/**
	 * Return a list of vertices in the odd cycle in each component of a graph.
	 * @param g - The graph in which the odd cycles need to be found
	 * @return - list of vertices in odd cycles in each component if graph is not bipartite
	 * 		   - else null
	 */
	public static List<Vertex> oddLengthCycle(Graph g) {
		final Queue<Vertex> Q = new LinkedList<Vertex>();
		final List<Vertex> oddCycleVetices = new ArrayList<Vertex>();	
		
		//Invariants - Q contains all the vertices in the same level
		//oddCycleVetices contains all the vertices in odd cycles from all components
		
		for (final Vertex u : g) {
		    u.seen = false;
		    u.parent = null;
		    u.distance = Integer.MAX_VALUE;
		}

		// Run BFS on every component
		for (Vertex src : g) {
		    if (!src.seen) {
				src.distance = 0;
				Q.add(src);
				src.seen = true;
				boolean cycleFound = false; //flag to check whether current component contain odd cycle
				while (!Q.isEmpty()) {
				    Vertex u = Q.remove();
				    for (Edge e : u.Adj) {
						Vertex v = e.otherEnd(u);
						if (!v.seen) {
						    v.seen = true;
						    v.parent = u;
						    v.distance = u.distance + 1;
						    Q.add(v);
						} else {
							if(!cycleFound){
							    if (u.distance == v.distance) {
							    	//Traverse to the least common ancestor and record the cycle
							    	oddCycleVetices.add(u);
							    	oddCycleVetices.add(v);
							    	Vertex uParent = u.parent;
							    	Vertex vParent = v.parent;
							    	while(uParent != vParent)
							    	{
							    		oddCycleVetices.add(uParent);
							    		oddCycleVetices.add(vParent);
							    		uParent = uParent.parent;
							    		vParent = vParent.parent;
							    	}
							    	if(uParent == vParent)
							    	{
							    		oddCycleVetices.add(uParent);
							    	}
							    	cycleFound = true;
							    }
							}
						}
				    }
				}
		    }
		}
		if(oddCycleVetices.isEmpty())
		{
			return null;
		}
		return oddCycleVetices;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		if (args.length > 0) {
		    File inputFile = new File(args[0]);
		    in = new Scanner(inputFile);
		} else {
		    in = new Scanner(System.in);
		}
		
		Graph g = Graph.readGraph(in, false);
		Util.printOutput("odd_cycle_g19.txt", String.valueOf(oddLengthCycle(g)));		
	}

}
