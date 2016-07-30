/**
 * Created by sags on 2/14/16.
 */
// Ver 1.0:  Wed, Feb 3.  Initial description.
// Ver 1.1:  Thu, Feb 11.  Simplified Index interface

import java.util.Comparator;

public class IndexedHeap<T extends Index> extends BinaryHeap<T> {
    /** Build a priority queue with a given array q */
    IndexedHeap(T[] q, Comparator<T> comp) {
        super(q, comp);
    }

    /** Create an empty priority queue of given maximum size */
    IndexedHeap(int n, Class<T> elementType,Comparator<T> comp) {
        super(n, comp,elementType);
    }

    @Override
    public void assign(int index,T x){
        x.putIndex(index);
        super.assign(index,x);
    }
    /** restore heap order property after the priority of x has decreased */
    void decreaseKey(T x) {
        percolateUp(x.getIndex());
    }
}
