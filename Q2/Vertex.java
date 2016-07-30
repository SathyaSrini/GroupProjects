/**
 * Class to represent a vertex of a graph
 * 
 *
 */

import java.util.*;

public class Vertex implements Index, Comparator<Vertex>{
    public int name; // name of the vertex
    public boolean seen; // flag to check if the vertex has already been visited
    public Vertex parent; // parent of the vertex
    public int distance; // distance to the vertex from the source vertex
    public List<Edge> Adj, revAdj; // adjacency list; use LinkedList or ArrayList
    public int degree; // number of in-flow edges to the vertex
    public String color; // state of the vertex while processing. white/grey/black
    public int index;

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
	degree = 0;
	color = "white"; // Default state of the vertex
	Adj = new ArrayList<Edge>();
	revAdj = new ArrayList<Edge>();   /* only for directed graphs */
    }

    @Override
	public int compare(Vertex arg0, Vertex arg1) {
		return arg0.distance - arg1.distance;
	}

	@Override
	public void putIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIndex() {		
		return this.index;
	}

	/**
     * Method to represent a vertex by its name
     */
    public String toString() {
	return Integer.toString(name);
    }
}