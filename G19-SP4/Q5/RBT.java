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

public class RBT<T extends Comparable<? super T>> {
    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    class RBEntry<T>  {
        boolean color;
        T element;
        int level;
        RBEntry<T> left, right, parent;
        RBEntry(T x, RBEntry<T> l, RBEntry<T> r, RBEntry<T> p,boolean clr) {
            element = x;
            left = l;
            right = r;
            parent = p;
            color = clr;
            level=0;
        }
    }

    RBEntry<T> rb_root;
    int size;
    Comparable[] level_ord;

    RBT() {
        rb_root=null;
        size = 0;
    }

    //Create a BST from a sorted list
    RBT(ArrayList<T> list){
        sortListtoBST(list,0,list.size()-1);
    }



    T sortListtoBST(ArrayList<T> list,int start,int end){
        if(start>=end) return null;
        int mid=(end+start)/2;
        this.addRBT(list.get(mid));
        sortListtoBST(list,start,mid);
        sortListtoBST(list,mid+1,end);
        return null;
    }

    //Level order traversal in BST
    public Comparable[] levelOrderArray(){
        level_ord=new Comparable[size];
        Queue<RBEntry<T>> level_order=new ArrayDeque<>();
        level_order.add(this.rb_root);
        level_order.add(new RBEntry<T>(null,null,null,null,BLACK));
        int level=0;
        Comparable[] arr= new Comparable[this.size];
        int index=0;
        while (!level_order.isEmpty()){
            RBEntry<T> temp=level_order.poll();
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
                    level_order.add(new RBEntry<T>(null,null,null,null,BLACK));
            }
        }
        return arr;
    }
    
    // Find x in subtree rooted at node t.  Returns node where search ends.
    RBEntry<T> find(RBEntry<T> t, T x) {
        RBEntry<T> pre = t;
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
        RBEntry<T> node = find(rb_root, x);
        return node == null ? false : x.equals(node.element);
    }

    void case1Repair(RBEntry<T> grandparent){
        grandparent.right.color=BLACK;
        grandparent.left.color=BLACK;
        grandparent.color=RED;
        if (grandparent.parent==null)
        {
            grandparent.color=BLACK;
            return;
        }
        if (grandparent.parent.color==grandparent.color==RED)
            case1Repair(grandparent.parent);
        return;
    }

    void case2Repair(RBEntry<T> grandparent,RBEntry<T> parent, RBEntry<T> child,boolean toggle,boolean toggle1){
        parent.color=BLACK;
        grandparent.color=RED;
        //if toggle true right to be changed
        if (toggle){
            grandparent.left=parent.right;
            parent.right=grandparent;
            parent.parent=grandparent.parent;
            grandparent.parent=parent;
            if(toggle1 && parent.parent!=null)
                parent.parent.left=parent;
            else if(parent.parent!=null)
                parent.parent.right=parent;
            else if(parent.parent==null)
                rb_root=parent;
        }
        else{
            grandparent.right=parent.left;
            parent.left=grandparent;
            parent.parent=grandparent.parent;
            grandparent.parent=parent;
            if(toggle1 && parent.parent!=null)
                parent.parent.left=parent;
            else if(parent.parent!=null)
                parent.parent.right=parent;
            else if(parent.parent==null)
                rb_root=parent;
        }
    }

    void case3Repair(RBEntry<T> grandparent,RBEntry<T> parent, RBEntry<T> child,boolean toggle){
        if (parent.left==child){
            child.parent=grandparent;
            grandparent.right=child;
            parent.left=child.right;
            parent.parent=child;
            child.right=parent;
            case2Repair(grandparent,child,parent,false,toggle);
        }
        else{
            child.parent=grandparent;
            grandparent.left=child;
            parent.right=child.left;
            parent.parent=child;
            child.left=parent;
            case2Repair(grandparent,child,parent,true,toggle);
        }
    }

    void Repair(RBEntry<T> child){
        RBEntry<T> parent=child.parent;
        RBEntry<T> grandparent=parent.parent;
        RBEntry<T> greatgrandpa;
        if(grandparent!=null)
            greatgrandpa=grandparent.parent;
        else greatgrandpa=null;
        boolean toggle,toggle1;
        if (grandparent.left!=null && grandparent.left==parent)
            toggle=true;
        else toggle=false;
        if (greatgrandpa!=null && greatgrandpa.left==grandparent)
            toggle1=true;
        else if (greatgrandpa!=null)toggle1=false;
        else toggle1=toggle;

        if (grandparent.left!=null && grandparent.right!=null && !(grandparent.left.color)&& !(grandparent.right.color))
             case1Repair(grandparent);
        else if ((grandparent.left==parent && parent.left==child) || (grandparent.right==parent && parent.right==child)){
            {
                case2Repair(grandparent,parent,child,toggle,toggle1);
            }
        }
        else{
            //case 3
            case3Repair(grandparent,parent,child,toggle1);
        }
    }

    public boolean addRBT(T x){
        if (size==0)
        {
            rb_root=new RBEntry<>(x,null,null,null,BLACK);
        }
        else{
            RBEntry<T> node = find(rb_root, x);
            int cmp = x.compareTo(node.element);
            if(cmp == 0) {
                node.element = x;
                return false;
            }
            RBEntry<T> newNode = new RBEntry<>(x, null, null, node,RED);
            if(cmp < 0 && !(newNode.color) && (node.color)) {
                node.left = newNode;
            }
            else if(cmp<0)
            {
                node.left=newNode;
                Repair(node.left);
            }
            else if(!(newNode.color) && node.color  && cmp>0){
                node.right = newNode;
            }
            else if(cmp>0) {
                node.right=newNode;
                Repair(node.right);
            }
        }
        size++;
        return true;
    }
    public static void main(String[] args) throws FileNotFoundException {
        //BST<Integer> t = new BST<>();
        RBT<Integer> t=new RBT<>();
        Scanner in ;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
	    System.out.println("File not given, so enter the numbers for RB Tree:");		
            in = new Scanner(System.in);
        }

        while(in.hasNext()) {
            int x = in.nextInt();
            if(x > 0) {
                System.out.print("Add " + x + " : ");
                t.addRBT(x);
                t.printTree();
            } else if(x < 0) {
                System.out.print("Remove method for RBT not implemented.");
            } else {
                  System.out.println("Level order traversal for RBT: ");
                  Comparable[] level_order=t.levelOrderArray();
                  t.printlevelOrder(level_order);
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
        inOrder(rb_root, arr, 0);
        return arr;
    }

    // Recursive in-order traversal of tree rooted at "node".
    // "index" is next element of array to be written.
    // Returns index of next entry of arr to be written.
    int inOrder(RBEntry<T> node, Comparable[] arr, int index) {
        if(node != null) {
            index = inOrder(node.left, arr, index);
            arr[index++] = node.element;
            index = inOrder(node.right, arr, index);
        }
        return index;
    }

    public void printTree() {
        System.out.print("[" + size + "]");
        printTree(rb_root);
        System.out.println();
    }

    // Inorder traversal of tree
    void printTree(RBEntry<T> node) {
        if(node != null) {
            printTree(node.left);
            System.out.print(" " + node.element);
            printTree(node.right);
        }
    }

    // Preorder traversal of tree
    void preTree(RBEntry<T> node) {
        if(node != null) {
            System.out.print(" " + node.element);
            preTree(node.left);
            preTree(node.right);
        }
    }
}

