import java.util.*;


public class ShortestAlgo {

   static IndexedHeap<Vertex>v=new IndexedHeap<Vertex>(0,Vertex.class,new Comparator<Vertex>() {
        @Override
        public int compare(Vertex o1, Vertex o2) {
            return o1.name-o2.name; }});

    private static boolean dfsVist(final Vertex u,
                                   final Stack<Vertex> topologicalOrder){
        boolean isDAG = true;

        //Invariants - isDAG flag to check whether the component is DAG

        if(u != null){
            u.color = "grey";
            for(final Edge e:u.Adj){
                final Vertex v = e.otherEnd(u);
                v.visited=true;
                if("white".equals(v.color) && v.visited){
                    v.parent = u;
                    isDAG &= dfsVist(v, topologicalOrder);
                } else if("grey".equals(v.color) && v.visited){
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

    public static Stack<Vertex> toplogicalOrder2(Graph g) {
        final Stack<Vertex> topologicalOrder = new Stack<Vertex>();
        boolean isDAG = true;

        g.verts.get(1).visited=true;
        //Invariants - isDAG flag to check whether the component is DAG
        //topologicalOrder contains the ordered list of vertices

        for(final Vertex u:g){
            if (u.parent!=null && u.parent.visited) u.visited=true;
            if("white".equals(u.color) && u.visited){ //if not seen vertex then call DFS
                isDAG &= dfsVist(u, topologicalOrder);
            }
        }
        if(!isDAG)
            return null;
        return topologicalOrder;
    }

    static void clear(Graph g){
        g.verts.get(1).distance.setInfinity(false);
        g.verts.get(1).distance.setDistance(0);
        g.verts.get(1).parent=null;
    }

    static void printVertex(String algo,Graph g,IndexedHeap<Vertex> v,boolean level){
        final StringBuffer output = new StringBuffer();
        int total = 0,sum_sp=0;
        boolean first = true;
        for(final Vertex u:g){
            if(first){ //If source print the default values
                first = false;
                if(level) output.append("1 0 -\n");
                else output.append("1 0 1\n");
                sum_sp+=u.degree;
                if(v.size>0 && v.peek().equals(u)) v.deleteMin();
                continue;
            }
            if(v.size>0 && v.peek().equals(u))
            {
                v.deleteMin();
                total+=u.distance.getDistance();
                sum_sp+=u.degree;
                if(level) output.append(u.name+" "+u.distance.getDistance()+" "+((u.parent==null)?"-":u.parent.name)+"\n");
                else output.append(u.name+" "+u.distance.getDistance()+" "+u.degree+"\n");
            }
            else{
            sum_sp+=u.degree;
            total += (u.distance.isInfinity()?0:u.distance.getDistance()); //If infinity skip the distance
            if(level) output.append(u.name +" "+(u.distance.isInfinity()?"INF":u.distance.getDistance()) + " "+((u.parent==null)?"-":u.parent.name)+"\n");
            else output.append(u.name +" "+(u.distance.isInfinity()?"INF":u.distance.getDistance()) + " "+u.degree+"\n");
           }
        }
        if(level) System.out.println(algo+" "+total);
        else System.out.println(sum_sp);
        if(g.numNodes<=100)System.out.println(output.toString());
    }


    static void BFS_sp(Graph g,boolean level){
        Queue<Vertex> sp_vert=new ArrayDeque<>();
        clear(g);
        sp_vert.add(g.verts.get(1));

        Timer time=new Timer();
        time.start();
        while(!sp_vert.isEmpty()){
            Vertex From=sp_vert.poll();
            for(Edge e:From.Adj){
                Vertex To=e.otherEnd(From);
                if (!From.distance.isInfinity()){
                    if(To.distance.isInfinity())
                        To.distance.setInfinity(false);
                    if (To.distance.getDistance()==(From.distance.getDistance()+e.Weight))
                    {
                        To.degree+= From.degree;
                        To.parent=From;
                    }
                    else if (To.distance.getDistance()>(From.distance.getDistance()+e.Weight))
                    {
                        To.distance.setDistance(From.distance.getDistance()+e.Weight);
                        To.parent=From;
                        To.degree=From.degree;
                        sp_vert.add(To);
                    }
                }
            }
        }
        time.end();
        System.out.println(" Time taken to run the algorithm : " + time);

        printVertex("BFS",g,v,level);

    }

    static void Dijkstra_sp(Graph g,boolean level){
        clear(g);

        Timer time=new Timer();
        time.start();

        for(Vertex From:g){
            if (From!=null){
                for(Edge e:From.Adj){
                    Vertex To=e.otherEnd(From);
                    if (!From.distance.isInfinity()){
                        if(To.distance.isInfinity())
                            To.distance.setInfinity(false);
                        if (To.distance.getDistance()==(From.distance.getDistance()+e.Weight))
                        {
                            To.degree+= From.degree;
                         }
                        else if (To.distance.getDistance()>(From.distance.getDistance()+e.Weight))
                        {
                            To.distance.setDistance(From.distance.getDistance() + e.Weight);
                            To.parent=From;
                            To.degree=From.degree;
                        }
                    }
                }
            }
        }

        time.end();
        System.out.println(" Time taken to run the algorithm : " + time);

        printVertex("Dij", g, v, level);

    }

    public static void Initialize(final List<Vertex> printStack,int i){
        printStack.get(i).distance.setInfinity(false);
        printStack.get(i).distance.setDistance(0);
        printStack.get(i).degree=1;
    }

    public static void DAG_sp(final Stack<Vertex> printStack,Graph g,boolean level){

        IndexedHeap<Vertex> qv=new IndexedHeap<Vertex>(printStack.size(),Vertex.class,new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                int ans=o1.name-o2.name;
                if (ans>0) return 1;
                else if(ans<0) return -1;
                else return 0;
            }
        });

        Timer time=new Timer();
        time.start();

        Initialize(printStack, printStack.size() - 1);//stack is ulta array traversal so if you want to make "x" as source then ensure to go from back
        while (!printStack.isEmpty()){
            Vertex From=printStack.pop();
            qv.insert(From);
            for(Edge e:From.Adj){
                Vertex To=e.otherEnd(From);
                if (!From.distance.isInfinity()){
                    if(To.distance.isInfinity())
                        To.distance.setInfinity(false);
                    if (To.distance.getDistance()==(From.distance.getDistance()+e.Weight))
                    {
                        To.degree+= From.degree;
                        To.parent=From;
                    }
                    else if (To.distance.getDistance()>(From.distance.getDistance()+e.Weight))
                    {
                        To.distance.setDistance(From.distance.getDistance()+e.Weight);
                        To.parent=From;
                        To.degree=From.degree;
                        qv.decreaseKey(To);
                    }
                }
            }
        }

        time.end();
        System.out.println(" Time taken to run the algorithm : " + time);

        printVertex("DAG", g, qv, level);
    }


}
