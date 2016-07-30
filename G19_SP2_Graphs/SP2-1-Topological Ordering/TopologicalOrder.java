import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class TopologicalOrder {
	
	/**
	 * Algorithm 1. Remove vertices with no incoming edges, one at a
	 * time, along with their incident edges, and add them to a list.  
	 * @param g - A Directed Graph which need to be topologically ordered.
	 * @return - Topologically Ordered List of vertices if the graph is DAG else null
	 */
	public static List<Vertex> toplogicalOrder1(Graph g) { 
		final List<Vertex> topologicalOrder = new ArrayList<Vertex>();
		final Queue<Vertex> processQueue = new LinkedList<Vertex>();
		
		//Invariants - topologicalOrder contains the ordered list of vertices
		//processQueue contains all the vertices with zero in-degree
		
		for(final Vertex u:g){
			if(u.degree == 0){
				processQueue.offer(u);
			}				
		}
			
		while(!processQueue.isEmpty()){
			final Vertex u = processQueue.poll();
			topologicalOrder.add(u);
			for(final Edge e: u.Adj){
				final Vertex v = e.otherEnd(u);
				if(v.degree > 0){
					v.degree--;
					if(v.degree == 0){
						processQueue.offer(v);
					}
				}				
			}
		}
			
		if(topologicalOrder.size() != (g.verts.size()-1)){
			return null;
		}
		
		return topologicalOrder;		
	}
	
	/**
	 * Recursively traverse through the graph using the DFS algorithm and 
	 * orders the vertices in the traversal order.
	 * Color Codes: 
	 * white - unseen vertex
	 * grey - processing vertex
	 * black - processed vertex 
	 * @param u - Vertex which need to be processed
	 * @param topologicalOrder - Stack in which the ordered vertices need to be stored.
	 * @return - true if the graph is DAG else false
	 */
	private static boolean dfsVist(final Vertex u, 
								final Stack<Vertex> topologicalOrder){
		boolean isDAG = true;
		
		//Invariants - isDAG flag to check whether the component is DAG
		
		if(u != null){
			u.color = "grey";			
			for(final Edge e:u.Adj){
				final Vertex v = e.otherEnd(u);				
				if("white".equals(v.color)){
					v.parent = u;
					isDAG &= dfsVist(v, topologicalOrder);
				} else if("grey".equals(v.color)){
					return false;
				}
				if(!isDAG){
					return isDAG;
				}
			}			
			u.color = "black";
			topologicalOrder.push(u);			
		}
		return isDAG;
	}

	/**
	 * Algorithm 2. Run DFS on g and push nodes to a stack in the
     * order in which they finish.  Write code without using global variables.
	 * @param g - A Directed Graph which need to be topologically ordered.
	 * @return - Topologically Ordered stack of vertices if the graph is DAG else null
	 */
    public static Stack<Vertex> toplogicalOrder2(Graph g) {
    	final Stack<Vertex> topologicalOrder = new Stack<Vertex>();
    	boolean isDAG = true;
    	
    	//Invariants - isDAG flag to check whether the component is DAG
    	//topologicalOrder contains the ordered list of vertices
    	
    	for(final Vertex u:g){
    		if("white".equals(u.color)){ //if not seen vertex then call DFS    			
    			isDAG &= dfsVist(u, topologicalOrder);
    		}
    		if(!isDAG){
        		return null;
        	}
    	}    		    	
    	return topologicalOrder;
    }
    
    /**
     * Converts the stack content from top to bottom in to a string
     * @param printStack - Stack which need to be converted to a string value
     * @return - String containing stack elements from top to bottom
     */
    private static String printStack(final Stack<Vertex> printStack){
    	if(printStack == null){
    		return "null";
    	}
    	final StringBuffer output = new StringBuffer();
    	output.append("[");
    	final boolean removeEndComma = !printStack.isEmpty();
    	while(!printStack.isEmpty()){
    		final Vertex u = printStack.pop(); 
    		output.append(u.name+", ");
    	}    	
    	if(removeEndComma){
    		output.replace(output.length()-2, output.length(), "");    		
    	} 
    	output.append("]");    	
    	
    	return output.toString();
    }

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		if (args.length > 0) {
		    File inputFile = new File(args[0]);
		    in = new Scanner(inputFile);
		} else {
		    in = new Scanner(System.in);
		}
		
		Graph g = Graph.readGraph(in, true);
		Util.printOutput("g19_topological_1.txt", String.valueOf(toplogicalOrder1(g)));
		Util.printOutput("g19_topological_2.txt", printStack(toplogicalOrder2(g)));
	}

}
