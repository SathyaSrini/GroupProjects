import java.lang.reflect.Array;
import java.util.Arrays;

// Ver 1.0:  Wec, Feb 3.  Initial description.

import java.util.Comparator;

public class BinaryHeap<T> implements PQ<T> {
	T[] pq;
	int size;
	Comparator<T> c;
	/** Building a priority queue with a given array q */
	BinaryHeap(T[] q, Comparator<T> comp) {
		pq = q;
		c = comp;
		size = q.length-1;
		buildHeap();		
	}

	/** Creating an empty priority queue of given maximum size */
	BinaryHeap(int n, Class<T> elementType, Comparator<T> comp) { 
		pq = (T[])Array.newInstance(elementType, n+1);
		c = comp;
		size = 0;	
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

	public void add(T x) { 
		if(size == pq.length-1){
			resize();
		}
		size++;
		assign(size,x);
		percolateUp(size);
	}

	public T remove() {
		if(size > 0){
			T min = pq[1];
			assign(1, pq[size--]);
			percolateDown(1);
			return min;
		}		
		return null;
	}

	public T peek() { 
		if(size > 0){
			return pq[1];
		}			
		return null;
	}
	
	public boolean isEmpty(){
		return (size == 0);
	}
	
	@Override
	public String toString() {	
		final StringBuffer output = new StringBuffer();
		for(int i=1;i<=size;i++){
			output.append(pq[i]+",");
		}
		return output.toString();
	}
	
	protected void assign(int index, T element){
		pq[index] = element;
	}
	
	void resize(){
		Class type = pq.getClass().getComponentType();
		T[] newPQ = (T[])Array.newInstance(type, 2 * size);
		System.arraycopy(pq, 1, newPQ, 1, pq.length-1);
		pq = newPQ;
	}

	/** pq[i] may violate heap order with parent */
	void percolateUp(int i) { 
		assign(0,pq[i]);
		int compResult = c.compare(pq[i/2], pq[0]);
		while(compResult > 0){
			assign(i, pq[i/2]);
			i = i/2;
			compResult = c.compare(pq[i/2], pq[0]);
		}
		assign(i, pq[0]);
	}

	/** pq[i] may violate heap order with children */
	void percolateDown(int i) { 
		T temp = pq[i];
		while(2 * i <= size){
			//Find the smallest of the children
			int compResult = c.compare(pq[2 * i], (2 * i < size)?pq[2 * i+1]:pq[2 * i]);
			int childPos = -1;
			if(compResult <= 0){
				childPos = 2 * i;
			}
			else{
				childPos = 2 * i+1;
			}
			//Compare the violated parent node with smaller child
			compResult = c.compare(temp, pq[childPos]);
			if(compResult > 0){
				assign(i,pq[childPos]);
				i = childPos;
			}
			else{
				break;
			}				
		}
		assign(i,temp);	
	}

	/** Creating a heap.  Precondition: none. */
	void buildHeap() {		
		for(int i=size/2;i>0;i--){
			percolateDown(i);
		}
	}		

	/* sort array A[1..n].  A[0] is not used. 
       Sorted order depends on comparator used to build heap.
       min heap ==> descending order
       max heap ==> ascending order
	 */
	public static<T> void heapSort(T[] A, Comparator<T> comp) { 
		final BinaryHeap<T> binaryHeap = new BinaryHeap<T>(A, comp.reversed());
		
		for(int i=A.length-1;i > 0; i--){
			T temp = binaryHeap.deleteMin();			
			A[i] = temp;
		}
	}
	
	public static void main(String args[]){
		final Integer[] testHeap = {0,2,9,4,10,6,7,3,1};
		final BinaryHeap<Integer> binaryHeap 
						= new BinaryHeap<Integer>(testHeap, new Comparator<Integer>(){
							@Override
							public int compare(Integer o1, Integer o2) {								
								return o1 - o2;
							}							
						});
		final Integer[] testHeap2 = {0,2,9,4,10,6,7,3,1};
		BinaryHeap.heapSort(testHeap2, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {								
				return o1 - o2;
			}							
		});
		System.out.println(Arrays.toString(binaryHeap.pq));
		System.out.println(Arrays.toString(testHeap2));
		final BinaryHeap<Integer> genericHeap = new BinaryHeap<>(5, Integer.class,
												new Comparator<Integer>(){
												@Override
												public int compare(Integer o1, Integer o2) {								
													return o1 - o2;
												}							
											});
		genericHeap.insert(10);
		genericHeap.insert(1);
		genericHeap.insert(5);
		genericHeap.insert(2);
		genericHeap.insert(7);
		genericHeap.insert(3);
		System.out.println(Arrays.toString(genericHeap.pq));
	}
}
