/**
 * Created by sags on 3/16/16.
 */
/** @author rbk
 *  Binary search tree (nonrecursive version)
 *  Ver 1.1: Bug fixed - parent of child updated after removeOne
 **/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BST<T extends Comparable<? super T>> {
    class Entry<T> {
        T element;
        int level;
        Entry<T> left, right, parent;

        Entry(T x, Entry<T> l, Entry<T> r, Entry<T> p) {
            element = x;
            left = l;
            right = r;
            parent = p;
            level=0;
        }
    }

    Entry<T> root;
    int size;
    Comparable[] level_ord;

    BST() {
        root = null;
        size = 0;
    }

    //Create a BST from a sorted list
    BST(ArrayList<T> list){
        sortListtoBST(list,0,list.size()-1);
    }

    T sortListtoBST(ArrayList<T> list,int start,int end){
        if(start>=end) return null;
        int mid=(end+start)/2;
        this.add(list.get(mid));
        sortListtoBST(list,start,mid);
        sortListtoBST(list,mid+1,end);
        return null;
    }

    //Level order traversal in BST
    public Comparable[] levelOrderArray(){
        level_ord=new Comparable[size];
        Queue<Entry<T>> level_order=new ArrayDeque<>();
        level_order.add(this.root);
        level_order.add(new Entry<T>(null,null,null,null));
        int level=0;
        Comparable[] arr= new Comparable[this.size];
        int index=0;
        while (!level_order.isEmpty()){
            Entry<T> temp=level_order.poll();
            if (temp.element==null){
                if (!level_order.isEmpty())
                    level++;
            }
            else{
                arr[index]=  temp.element;temp.level=level;
                level_ord[index++]=temp.level;
                if (temp.left!=null)
                    level_order.add(temp.left);
                if(temp.right!=null)
                    level_order.add(temp.right);
                if(temp.right!=null || temp.left!=null && temp.parent!=null && (temp.parent.right==null || temp.parent.right==temp))
                    level_order.add(new Entry<T>(null,null,null,null));
            }
        }
        return arr;
    }

    // Find x in subtree rooted at node t.  Returns node where search ends.
    Entry<T> find(Entry<T> t, T x) {
        Entry<T> pre = t;
        while(t != null) {
            pre = t;
            int cmp = x.compareTo(t.element);
            if(cmp == 0) {
                return t;
            } else if(cmp < 0) {
                t = t.left;
            } else {
                t = t.right;
            }
        }
        return pre;
    }

    // Is x contained in tree?
    public boolean contains(T x) {
        Entry<T> node = find(root, x);
        return node == null ? false : x.equals(node.element);
    }


    // Add x to tree.  If tree contains a node with same key, replace element by x.
    // Returns true if x is a new element added to tree.
    public boolean add(T x) {
        if(size == 0) {
            root = new Entry<>(x, null, null, null);
        } else {
            Entry<T> node = find(root, x);
            int cmp = x.compareTo(node.element);
            if(cmp == 0) {
                node.element = x;
                return false;
            }
            Entry<T> newNode = new Entry<>(x, null, null, node);
            if(cmp < 0) {
                node.left = newNode;
            } else {
                node.right = newNode;
            }
        }
        size++;
        return true;
    }

    // Remove x from tree.  Return x if found, otherwise return null
    public T remove(T x) {
        T rv = null;
        if(size > 0) {
            Entry<T> node = find(root, x);
            if(x.equals(node.element)) {
                rv = node.element;
                remove(node);
                size--;
            }
        }
        return rv;
    }

    // Called when node has at most one child.  Returns that child.
    Entry<T> oneChild(Entry<T> node) {
        return node.left == null? node.right : node.left;
    }

    // Remove a node from tree
    void remove(Entry<T> node) {
        if(node.left != null && node.right != null) {
            removeTwo(node);
        } else {
            removeOne(node);
        }
    }

    // remove node that has at most one child
    void removeOne(Entry<T> node) {
        if(node == root) {
            Entry<T> nc = oneChild(root);
            root = nc;
            if(nc != null) nc.parent = root;
            root.parent = null;
        } else {
            Entry<T> p = node.parent;
            Entry<T> nc = oneChild(node);
            if(p.left == node) {
                p.left = nc;
            } else {
                p.right = nc;
            }
            if(nc != null) nc.parent = p;
        }
    }

    // remove node that has two children
    void removeTworight(Entry<T> node){
        System.out.println("Using remove two Right (minimum from right subtree) when random boolean is false");
        Entry<T> minRight = node.right;
        while(minRight.left != null) {
            minRight = minRight.left;
        }
        node.element = minRight.element;
        removeOne(minRight);
    }

    void removeTwoleft(Entry<T> node){
        System.out.println("Using remove two Left(maximum from left subtree) when random boolean is true");
        Entry<T> maxLeft = node.left;
        while(maxLeft.right != null) {
            maxLeft = maxLeft.right;
        }
        node.element = maxLeft.element;
        removeOne(maxLeft);
    }

    void removeTwo(Entry<T> node) {
        boolean toggle=new Random().nextBoolean();
        if(toggle)
            removeTworight(node);
        else
            removeTwoleft(node);
    }

    public static void main(String[] args) throws FileNotFoundException {
        //BST<Integer> t = new BST<>();
        BST<Integer> bst=new BST<>();

        Scanner in ;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
        ArrayList<Integer> l=new ArrayList<>();

        System.out.println("In this BST, the input given through command line is taken and is passed to both an array-list and added to BST as such,the array is sorted and then sent to sortTolist function which creates a balanced binary search tree," +
                "level order traversal is done for both, and remove is applicable for normal BST not list instantiated one ie; list will be obtained completely and then only BST is created.");
        while(in.hasNext()) {
            int x = in.nextInt();
            if(x > 0) {
                System.out.print("Add to BST" + x + " : ");
                bst.add(x);
                l.add(x);
                bst.printTree();
            } else if(x < 0) {
                System.out.print("Remove from BST" + x + " : ");
                bst.remove(-x);
                bst.printTree();
            } else {
                Collections.sort(l);
                BST<Integer> t = new BST<>(l);
                System.out.print("Final output of BST from sorted array/list: Can remove from the list instantiated BST if you want to.");
                t.printTree();
                System.out.println("\nLevel order print of list BST");
                Comparable[] sorted_array=t.levelOrderArray();
                t.printlevelOrder(sorted_array);
                System.out.println("\nLevel order print of BST instantiated using add");
                sorted_array=t.levelOrderArray();
                t.printlevelOrder(sorted_array);
                return;
            }
        }
    }

    //to print the level order
    public void printlevelOrder(Comparable[] arr){
        System.out.println("Level order traversal is :");
        int index=0;
        for(Comparable ele:arr)
            System.out.println("Level: "+level_ord[index++]+" element is: "+ele.toString());
    }
    // Create an array with the elements using in-order traversal of tree
    public Comparable[] toArray() {
        Comparable[] arr = new Comparable[size];
        inOrder(root, arr, 0);
        return arr;
    }

    // Recursive in-order traversal of tree rooted at "node".
    // "index" is next element of array to be written.
    // Returns index of next entry of arr to be written.
    int inOrder(Entry<T> node, Comparable[] arr, int index) {
        if(node != null) {
            index = inOrder(node.left, arr, index);
            arr[index++] = node.element;
            index = inOrder(node.right, arr, index);
        }
        return index;
    }

    public void printTree() {
        System.out.print("[" + size + "]");
        printTree(root);
        System.out.println();
    }

    // Inorder traversal of tree
    void printTree(Entry<T> node) {
        if(node != null) {
            printTree(node.left);
            System.out.print(" " + node.element);
            printTree(node.right);
        }
    }
}
