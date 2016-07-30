import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Class implementing the Edmond's Branching Algorithm for directed graph.
 * @author Jayakarthigayan Sridharan, Sathyanarayanan Srinivasan, Saagarikha Srinivasan
 *
 */
public class EdmondBranching {	
	
	private static int NEXT_VERTEX_ID = 1; //next available vertex id 
	
	/**
	 * Find all zero edge nodes reachable from a Vertex
	 * @param u - starting vertex
	 */
	private static void DFSVist(final Vertex u){
		u.seen = true;
		for(final Edge e:u.Adj){
			if(e.Weight == 0){
				final Vertex v = e.otherEnd(u);
				if(!v.seen && v.active){
					DFSVist(v);
				}
			}
		}
	}
	
	/**
	 * Find all the vertices not reachable from root using zero edges
	 * @param g - current graph to be searched
	 * @return - list of vertices not reachable from root
	 */
	private static List<Vertex> zeroEdgeDFS(final Graph g){
		List<Vertex> notReachable = new ArrayList<Vertex>();
		for(final Vertex u:g){
			u.seen = false;
			u.parent = null;
		}
		final Vertex root = g.verts.get(1);
		DFSVist(root);
		for(final Vertex u:g){
			if(!u.seen && u.active){
				notReachable.add(u);
			}
		}
		return notReachable;
	}
	
	/**
	 * Get the weight of minimum incoming edge
	 * @param u - vertex to be processed
	 * @return - minimum weight of incoming edges 
	 */
	private static int getMinEdgeWeight(final Vertex u){
		if(u == null || u.minInEdge == null){
			return 0;
		}
		return u.minInEdge.Weight;
	}
	
	/**
	 * Transform all the minimum incoming edges as zero weight edge  
	 * @param g - Graph to be transformed
	 * @param nonReachableVertices - List of non-reachable vertices 
	 * @return - total weight of all the transformed min edges
	 */
	private static int transform(final Graph g, 
			final List<Vertex> nonReachableVertices){
		int delta = 0;
		boolean first = true;
		for(final Vertex u:g){
			if(first){
				first = false;
				continue;
			}
			if(u.active){ // If vertex active process
				final int minEdgeWeight = getMinEdgeWeight(u);
				delta += minEdgeWeight;
				for(final Edge e:u.revAdj){ //Process all incoming edges
					e.Weight -= minEdgeWeight;							
				}
			}
		}
		//get non reachable vertices through zero edges
		final List<Vertex> nonReachVertex = zeroEdgeDFS(g); 
		if(nonReachVertex != null && nonReachVertex.size() > 0){
			nonReachableVertices.addAll(nonReachVertex);
		}
		return delta;
	}
	
	/**
	 * Expand all the super vertices in to original vertices with updated zero 
	 * incoming edges
	 * @param g - Directed graph containing all the super vertices
	 */
	private static void expandSuperVertices(final Graph g){
		for(int i=NEXT_VERTEX_ID-1;i>g.numNodes;i--){ //expand all super vertices
			/*
			 * Invariants - u super vertex being expanded
			 * minEdge - Minimum edge replaced by the minimum edge of the super vertex			
			 */
			
			final Vertex u = g.verts.get(i);
			if(u.active){
				u.active = false;
				final Edge minEdge = u.minInEdge.replacementEdge;
				minEdge.active = true;
				final int minVertexId = minEdge.To.name;
				for(final Integer id:u.replacingVertices){ //Expand active incoming edges
					final Vertex v = g.verts.get(id);
					v.active = true;
					if(v.name != minVertexId){
						v.minInEdge.active = true;
					}
					else{
						v.minInEdge.active = false;
						minEdge.Weight = 0;					
						v.minInEdge = minEdge;					
					}					
				}
				for(final Edge e:u.Adj){ //Expand active outgoing edges
					if(e.active && e.replacementEdge != null){
						final Edge replace = e.replacementEdge;
						replace.active = true;
						replace.Weight = e.Weight;
						final Vertex v = e.To;
						if(v.minInEdge.Weight >= replace.Weight){
							v.minInEdge = replace;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Print all the zero edges after expanding all the super vertices
	 * @param g - Graph to be printed
	 */
	private static void printZeroEdges(final Graph g){
		expandSuperVertices(g); // Expand all the super vertices
		boolean first = true;
		int count = 1;
		for(final Vertex u:g){
			if(first){ //Skip root node
				first = false;
				continue;
			}			
			count++;
			if(count > g.numNodes) break; //Exit when all the original vertices are printed
			System.out.println(u.minInEdge);			
		}
	}
	
	/**
	 * Check whether the given vertex is part of a zero edge cycle
	 * @param u - starting vertex
	 * @return - list of vertices in the zero edge cycle
	 */
	private static List<Vertex> detectZeroCycle(final Vertex u){
		final List<Vertex> zeroCycle = new ArrayList<>();
		Vertex cur = u;
		boolean cycle = false;
		while(!cur.seen){ //check whether vertex repeated
			cur.seen = true;
			if(cur.minInEdge != null && 
					cur.minInEdge.Weight == 0 &&
					cur.active){
				cur = cur.minInEdge.otherEnd(cur);
				cycle = true;
			}			
			else{
				cycle = false;
				break;
			}
		}
		if(cycle){ // If cycle found get all vertices in the cycle.
			zeroCycle.add(cur);
			Vertex walkVertex = cur.minInEdge.otherEnd(cur);
			while(walkVertex.name != cur.name){
				zeroCycle.add(walkVertex);
				walkVertex = walkVertex.minInEdge.otherEnd(walkVertex);
			}
		}
		return zeroCycle;
	}

	/**
	 * Find a zero edge cycle in one of the non reachable vertices
	 * @param g - Current graph
	 * @param nonReachableVertices - Non reachable vertices from root through zero edges 
	 * @return - List of vertices in the zero edge cycle
	 */
	private static List<Vertex> getZeroCycle(final Graph g,
			final List<Vertex> nonReachableVertices){
		for(final Vertex u:g){
			u.seen = false;
			u.parent = null;
		}
		final List<Vertex> cycleVertices = new ArrayList<>();
		for(final Vertex u:nonReachableVertices){
			if(!u.active) continue;
			final List<Vertex> zeroCycle = detectZeroCycle(u);
			if(zeroCycle != null && zeroCycle.size() > 0){
				cycleVertices.addAll(zeroCycle);
				break;
			}				
		}
		return cycleVertices;
	}
	
	/**
	 * Create new outgoing edges for the given super vertex
	 * @param newVertex - Super Vertex
	 * @param minEdges - Ordered list of outgoing edges of the vertices replaced by super
	 * vertex 
	 */
	private static void addOutGoingEdges(final Vertex newVertex,
			final PriorityQueue<Edge> minEdges){
		/*
		 * Invariant - processedVertices contains the vertices to which outgoing edge 
		 * from super vertex is already set
		 */
		
		final HashSet<Integer> processedVertices = new HashSet<>();
		while(!minEdges.isEmpty()){
			final Edge e = minEdges.remove();
			if(!processedVertices.contains(e.To.name)){				
				final Edge newEdge = new Edge(newVertex, e.To, e.Weight);
				newEdge.replacementEdge = e;
				newVertex.Adj.add(newEdge);
				e.To.addIncomingEdge(newEdge);
				processedVertices.add(e.To.name);
			}
			e.active = false; //deactivate the old edges
		}
	}
	
	/**
	 * Create new incoming edges for the given super vertex
	 * @param newVertex - Super Vertex
	 * @param minEdges - Ordered list of incoming edges of the vertices replaced by super
	 * vertex 
	 */
	private static void addIncomingEdges(final Vertex newVertex,
			final PriorityQueue<Edge> minEdges){
		/*
		 * Invariant - processedVertices contains the vertices to which incoming edge 
		 * from super vertex is already set
		 */
		
		final HashSet<Integer> processedVertices = new HashSet<>();
		while(!minEdges.isEmpty()){
			final Edge e = minEdges.remove();
			if(!processedVertices.contains(e.From.name)){				
				final Edge newEdge = new Edge(e.From,newVertex, e.Weight);
				newEdge.replacementEdge = e;
				e.From.Adj.add(newEdge);
				newVertex.addIncomingEdge(newEdge);
				processedVertices.add(e.From.name);
			}
			e.active = false;//deactivate the old edges
		}
	}
	
	/**
	 * Shrink vertices in the zero edge cycle in to a single super vertex
	 * @param g - Directed graph to be processed
	 * @param zeroCycle - List of vertices in the zero edge cycle to be shrunk
	 */
	private static void resolveZeroCycle(final Graph g,
			final List<Vertex> zeroCycle){
		/*
		 * Invariants - cycleVertices contains all the vertices in the zero edge cycle
		 * newVertex - super vertex which will replace all the vertices in the cycle
		 * minOutEdges - contains all the outgoing edges to the vertices outside the cycle
		 * in the ascending order of their weight
		 * minInEdges -  contains all the incoming edges from the vertices outside the cycle
		 * in the ascending order of their weight
		 */
		
		final HashSet<Integer> cycleVertices = new HashSet<>();
		final Vertex newVertex = new Vertex(NEXT_VERTEX_ID);
		newVertex.replacingVertices = new ArrayList<>();
		NEXT_VERTEX_ID++;
		for(final Vertex id:zeroCycle){			
			newVertex.replacingVertices.add(id.name);
			cycleVertices.add(id.name);
			id.active = false;
		}
		final PriorityQueue<Edge> minOutEdges = new PriorityQueue<>();
		final PriorityQueue<Edge> minInEdges = new PriorityQueue<>();
		for(final Vertex u:zeroCycle){
			for(final Edge e:u.Adj){ 
				//Add edges coming from vertices outside the cycle to the priority queue
				if(e.active && 
						!cycleVertices.contains(e.To.name)){
					minOutEdges.add(e);
				}
				else if(e.active){
					e.active = false;
				}
			}
			for(final Edge e:u.revAdj){ 
				//Add edges leading to vertices outside the cycle to the priority queue
				if(e.active && 
						!cycleVertices.contains(e.From.name)){
					minInEdges.add(e);
				}
				else if(e.active){
					e.active = false;
				}
			}
		}
		addOutGoingEdges(newVertex, minOutEdges); //Add Outgoing edges to the super vertex
		addIncomingEdges(newVertex, minInEdges); //Add incoming edges to the super vertex
		g.verts.add(newVertex.name, newVertex); //Add the super vertex to the graph 
	}
	
	/**
	 * Edmond's Branching algorithm to find to Minimum Spanning Tree in directed graph
	 * @param g - Directed graph in which MST need to be found
	 */
	private static void edmondBranching(final Graph g){
		/*
		 * Invariants - nonReachableVertices contains the vertices that are not
		 * reachable from the root using the zero edges
		 * delta - contains the total weight of all the edges that are transformed 
		 * at each stage
		 */
		
		final List<Vertex> nonReachableVertices = new ArrayList<>();
		int delta = transform(g, nonReachableVertices); //Convert min edges to zero edges
        
		if(nonReachableVertices.size() == 0){ //If no non reachable vertices then output
			System.out.println(delta);
			if(g.numNodes <= 50){ //If less or equal to 50 vertices expand and print zero edges
				printZeroEdges(g);
			}
			return;
		}
		
		// Repeat shrink until all the vertices become reachable from the root
		while(nonReachableVertices.size() > 0){ 			
			final List<Vertex> zeroCycles = getZeroCycle(g,nonReachableVertices);
			if(zeroCycles == null || zeroCycles.size() == 0){ //If no zero edge cycle then output
				if(g.numNodes <= 50){//If less or equal to 50 vertices expand and print zero edges
					System.out.println(delta);
					printZeroEdges(g);
				}
				return;
			}
			else{ //If non reachable vertices exist then shrink the vertices in cycle
				resolveZeroCycle(g,zeroCycles);				
			}
			nonReachableVertices.clear();
			delta += transform(g, nonReachableVertices);			
		}
		
        System.out.println(delta);
        if(g.numNodes <= 50){//If less or equal to 50 vertices expand and print zero edges
        	printZeroEdges(g);
        }
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
        NEXT_VERTEX_ID = g.numNodes+1;
        Timer time = new Timer();
        time.start();
        edmondBranching(g);
        time.end();
        System.out.println(time);
	}

}
