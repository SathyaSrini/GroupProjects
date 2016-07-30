

// Ver 1.0:  Wed, Feb 3.  Initial description.
// Ver 1.1:  Thu, Feb 11.  Simplified Index interface

import java.util.Comparator;

public class IndexedHeap<T extends Index> extends BinaryHeap<T> {
    /** Building a priority queue with a given array q */
    IndexedHeap(T[] q, Comparator<T> comp) {
	super(q, comp);
    }

    /** Creating an empty priority queue of given maximum size */
    IndexedHeap(int n, Class<T> elementType, Comparator<T> comp) {
	super(n, elementType, comp);
    }

    /** restoring heap order property after the priority of x has decreased */
    void decreaseKey(T x) {
	percolateUp(x.getIndex());
    }
    
    @Override
    protected void assign(int index, T element) {
    	element.putIndex(index);//Update the Index of the Vertex
    	super.assign(index, element);
    }
}
