/**
 * Class to represent a vertex of a graph
 * 
 *
 */

import java.util.*;

/**
 * 
 * @author Jayakarthigayan Sridharan, Sathyanarayanan Srinivasan, Saagarikha Srinivasan
 *
 */
public class Vertex {
    public int name; // name of the vertex
    public boolean seen; // flag to check if the vertex has already been visited
    public Vertex parent; // parent of the vertex
    public int distance; // distance to the vertex from the source vertex
    public List<Edge> Adj, revAdj; // adjacency list; use LinkedList or ArrayList
    public Edge minInEdge; //Minimum Weight incoming edge to this vertex
    public boolean active = true; //Status of the vertex in current graph
    public List<Integer> replacingVertices = null; //vertices replaced by this vertex

    /**
     * Constructor for the vertex
     * 
     * @param n
     *            : int - name of the vertex
     */
    Vertex(int n) {
	name = n;
	seen = false;
	parent = null;
	Adj = new ArrayList<>();
	revAdj = new ArrayList<>();	
    }
    
    /**
     * Used to add incoming edge to the vertex and update the minimum incoming edge
     * @param e - Incoming edge to the vertex
     */
    public void addIncomingEdge(final Edge e){
    	this.revAdj.add(e);    	
    	if(minInEdge == null || 
    			(e.Weight <= minInEdge.Weight && e.active)){
    		minInEdge = e;
    	}
    }

    /**
     * Method to represent a vertex by its name
     */
    public String toString() {
	return Integer.toString(name);
    }
}
