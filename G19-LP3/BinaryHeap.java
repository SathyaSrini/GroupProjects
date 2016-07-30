/**
 * Created by sags on 2/14/16.
 */
// Ver 1.0:  Wec, Feb 3.  Initial description.

import java.lang.reflect.Array;
import java.util.*;

public class BinaryHeap<T> implements PQ<T> {
    T[] pq;
    Comparator<T> c;
    int size;


    /** Create an empty priority queue of given maximum size */
    BinaryHeap(int n, Comparator<T> comp,Class<T> elementType) { /* to be implemented */
        pq = (T[])Array.newInstance(elementType, n+1);
        size=0;
        c=comp;
    }

    /** Build a priority queue with a given array q */
    BinaryHeap(T[] q, Comparator<T> comp) {
        pq = q;
        c = comp;
        size=q.length-1;
        buildHeap();
    }

    public void insert(T x) {
        add(x);
    }

    public T deleteMin() {
        return remove();
    }

    public T min() {
        return peek();
    }

    public boolean isEmpty(){
        return (size == 0);
    }

    public void resize(){
        T[] tmp=pq;
        pq=(T[]) Array.newInstance(getClass(),size*2);
        pq=Arrays.copyOf(tmp,pq.length);
    }

    public void assign(int index, T element){
        pq[index] = element;
    }

    public void add(T x) { /* to be implemented */
        if(size>=pq.length-1)
            resize();
        size++;
        assign(size,x);
        percolateUp(size);
    }

    public T remove() { /* to be implemented */
        if(size>0){
        T min=pq[1];
        assign(1,pq[size--]);
        //pq[size--]=null;
        percolateDown(1);
        return min;
        }
        return null;
    }

    public T peek() { /* to be implemented */
        return pq[1];
    }

    /** pq[i] may violate heap order with parent */
    void percolateUp(int i) { /* to be implemented */
        assign(0,pq[i]);
        while(c.compare(pq[i/2],pq[0])>0){
            assign(i,pq[i/2]);
            i=i/2;
        }
        assign(i, pq[0]);
    }

    /** pq[i] may violate heap order with children */
    void percolateDown(int i) { /* to be implemented */
        T x=pq[i];
        while (2*i<=size){
            if (2*i==size) //one child
              if(c.compare(x,pq[size])>0)
              {
                  assign(i,pq[size]);
                  i=size;
              }
              else break;
            else {//2children
                int schild=0;
                schild=(c.compare(pq[2*i],pq[(2*i)+1])<=0)?(2*i):(2*i)+1;
                if(c.compare(x,pq[schild])>0)
                {
                    assign(i,pq[schild]);
                    i=schild;
                }
                else break;
            }
        }
        assign(i,x);
        return;
    }

    /** Create a heap.  Precondition: none. */
    void buildHeap() {
        for(int i=pq.length/2;i>0;i--)
            percolateDown(i);
    }

    /* sort array A[1..n].  A[0] is not used.
       Sorted order depends on comparator used to buid heap.
       min heap ==> descending order
       max heap ==> ascending order
     */
    public <T> void heapSort(T[] A, Comparator<T> comp) { /* to be implemented */
        for(int i=this.size;i>0;i--){
            T x= (T) this.deleteMin();
            A[i]=x;
        }
    }
}
class MyComparator<T extends Comparable<T>> implements Comparator<T> {
    public int compare(T a, T b) {
        return a.compareTo(b);
    }
}
