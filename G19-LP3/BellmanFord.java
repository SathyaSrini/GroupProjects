import java.util.Comparator;

/**
 * Class implements Bellman Ford's Algorithm to find the shortest path
 * @author Jayakarthigayan Sridharan, Sathyanarayanan Srinivasan, Saagarikha Srinivasan 
 *
 */
public class BellmanFord {

    /**
     * Finds the shortest path from source to all vertices in the graph using Bellman Ford
     * algorithm
     * @param g - Graph in which shortest path need to be found.
     * @param levelOne - Is it a level 1 execution
     * @return - Returns null if no cycle found
     * else returns the vertex possibly in the cycle
     */
    static Vertex bellmanFord(final Graph g, final boolean levelOne){
        for(final Vertex u:g){ //Initial Setting to default values
            u.parent = null;
            u.count = 0;
            u.seen = false;
        }
		
		/*
		 * Invariants - q contains all vertices whose adjacent vertices are not 
		 * processed yet 
		 */

        final Vertex source = g.verts.get(1); //Getting the source node
        source.distance.setInfinity(false);
        source.distance.setDistance(0);
        source.seen = true;
        final Comparator<Vertex> comp = g.verts.get(1);
        final IndexedHeap<Vertex> q = new IndexedHeap<>(g.numNodes, Vertex.class,comp);
        q.add(source);

        while(!q.isEmpty()){ //Repeat until no more vertex to process
            final Vertex u = q.deleteMin();
            u.seen = false;
            u.count++;
            //If processed more than total number of vertices then negative cycle exists
            if (u.count >= g.numNodes)
            {
                return u;
            }
            //Relax all adjacent vertices and add them to queue if not processed
            for(final Edge e:u.Adj){
                final Vertex v = e.otherEnd(u);
                if(v.distance.isInfinity() || (levelOne && v.distance.getDistance() > (u.distance.getDistance() + e.Weight)) ||
                        (!levelOne && v.distance.getDistance() >= (u.distance.getDistance() + e.Weight))){
                    v.distance.setInfinity(false);
                    v.distance.setDistance( u.distance.getDistance() + e.Weight);
                    v.parent = u;
                    if(!v.seen){
                        q.add(v);
                        v.seen = true;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Print all the vertices and their distance from the source vertex  along with
     * the parent node
     * @param g - Graph to be printed
     * @param algoName - Algorithm code used to find shortest path in the graph
     */
    public static void printVertexDistance(final Graph g, final String algoName){
        final StringBuffer output = new StringBuffer();
        int total = 0;
        boolean first = true;
        for(final Vertex u:g){
            if(first){ //If source print the default values
                first = false;
                output.append("1 0 -\n");
                continue;
            }
            total += (u.distance.isInfinity()?0:u.distance.getDistance()); //If infinity skip the distance
            output.append(u.name +" "
                    +(u.distance.isInfinity()?"INF":u.distance.getDistance()) + " "
                    +((u.parent==null)?"-":u.parent.name)+"\n");
        }
        System.out.println(algoName+" "+total);
        if(g.numNodes<=100) System.out.println(output.toString());
    }

    /**
     * Find the Edge present in a negative or zero cycle.
     * @param g - Graph in which negative or zero cycle exists
     * @param cycleVertex - Vertex possible to be in the cycle
     */
    static void findNegativeOrZeroCycle(final Graph g, final Vertex cycleVertex){
        for(final Vertex u:g){ //Initial Setting to default values
            u.seen = false;
        }

        Vertex cur = cycleVertex.parent;
        cycleVertex.seen = true;
        while((cur.name != cycleVertex.name) && (!cur.seen)){ //Identify the vertex part of cycle
            cur.seen = true;
            cur = cur.parent;
        }

        final Vertex cycleEnd = cur;
        cur = cur.parent;
        while(cur.name != cycleEnd.name){ //Process all the vertices in the cycle
            for(final Edge e:cur.revAdj){ //Find the edges in the cycle
                final Vertex v = e.otherEnd(cur);
                if(v.name == cur.parent.name){
                    System.out.println(e.From.name+" "+ e.To.name+" "+e.Weight);
                    break;
                }
            }
            cur = cur.parent;
        }
        for(final Edge e:cur.revAdj){ //Find the edges in the cycle
            final Vertex v = e.otherEnd(cur);
            if(v.name == cur.parent.name){
                System.out.println(e.From.name+" "+ e.To.name+" "+e.Weight);
                break;
            }
        }
    }
}
