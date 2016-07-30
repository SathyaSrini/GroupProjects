import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Contains the functions to find the MST using Prim's Algorithm in 2 methods.
 * @author Jayakarthigayan Sridharan
 *
 */
public class MST {
    static final int Infinity = Integer.MAX_VALUE;

    /**
     * Finds the MST using Priority Queue of Edges
     * @param g - Graph in which MST need to be found
     * @return - Returns the weight of the MST
     */
    static int Prim1MST(final Graph g)
    {
    	for(final Vertex u:g.verts){
    		if(u != null){
    			u.seen = false;
    			u.parent = null;
    		}
    	}
    	Timer timer = new Timer();
        int wmst = 0;
        Vertex src = g.verts.get(1);
        src.seen = true;
        
        //Invariants: edgeQueue contains the queue of edges orders based on their edge weight
        
        final BinaryHeap<Edge> edgeQueue = new BinaryHeap<>(g.numNodes, 
        													Edge.class, 
        													src.Adj.get(0));
        for(final Edge e: src.Adj){
        	edgeQueue.add(e);
        }
        while(!edgeQueue.isEmpty()){
        	final Edge e = edgeQueue.deleteMin();
        	if(!(e.From.seen && e.To.seen)){
        		//Invariant: newVertex contains the Vertex of the e outside the MST Forest/Tree
        		final Vertex newVertex = (e.From.seen)?e.To:e.From;
        		wmst += e.Weight;
        		newVertex.seen = true;
        		newVertex.parent = e.otherEnd(newVertex);
        		for(final Edge vAdjEdge: newVertex.Adj){
        			if(!vAdjEdge.otherEnd(newVertex).seen){
        				edgeQueue.add(vAdjEdge);
        			}
                }
        	}
        }        
        timer.end();
        System.out.println(timer);
        return wmst;
    }
    
    /**
     * Finds the MST using Indexed Priority Queue of Vertices
     * @param g - Graph in which MST need to be found
     * @return - Returns the weight of the MST
     */
    static int Prim2MST(final Graph g){
    	for(final Vertex u:g.verts){
    		if(u != null){
    			u.seen = false;
    			u.parent = null;
    			u.distance = Infinity;
    		}
    	}
    	Timer timer = new Timer();
    	int wmst = 0;
    	final Vertex src = g.verts.get(1);
    	src.distance = 0;
    	Comparator<Vertex> comp = new Vertex(0);
    	    	
    	//Invariants: vertexQueue contains the queue of vertices orders based on their distance
    	
    	IndexedHeap<Vertex> vertexQueue = new IndexedHeap<Vertex>(g.verts.size(), 
    															Vertex.class, 
    															comp);
    	for(final Vertex u:g.verts){
    		if(u != null){
    			vertexQueue.insert(u);
    		}
    	}    	
    	while(!vertexQueue.isEmpty()){
    		final Vertex u = vertexQueue.deleteMin();
    		u.seen = true;
    		wmst += u.distance;
    		for(final Edge e:u.Adj){
    			final Vertex v = e.otherEnd(u);
    			if(!v.seen && e.Weight < v.distance){
    				v.distance = e.Weight;
    				v.parent = u;
    				vertexQueue.decreaseKey(v);
    			}
    		}
    	}
    	timer.end();
    	System.out.println(timer);
    	return wmst;
    }

    public static void main(String[] args) throws FileNotFoundException
    {
    	if(args == null || args.length == 0){
    		throw new IllegalArgumentException("Not enough number of arguments passed"
					+ "\nUsage: MST <Prim's 1/2> <Input File Name> \n"
					+ "Example: MST 1 input.txt");	
    	}
        Scanner in;
        int option = 1;       
        option = Integer.parseInt(args[0]);
        if (args.length > 1) {        	
		    File inputFile = new File(args[1]);
		    in = new Scanner(inputFile);
		} else {
		    in = new Scanner(System.in);
		}
        
        Graph g = Graph.readGraph(in, false);    
        if(option == 1){
        	System.out.println(Prim1MST(g));
        } else{
        	System.out.println(Prim2MST(g));
        }              
    }
}
