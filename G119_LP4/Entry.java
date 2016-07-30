/**
 * Created by sags on 4/3/16.
 */
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by G119
 */
class Entry implements Comparable<Entry>
{
    long id;
    double price;
    long[] desc;
    int size,orig_position;

    Entry(long id1,double price1, long[] desc1,int size1,int orig_pos)
    {
        this.id=id1;
        this.price=price1;
        this.desc=desc1;
        this.size=size1;
        this.orig_position=orig_pos;
    }

    @Override
    public int compareTo(Entry o)
    {
        return (int)(this.id - o.id);

    }

    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("ID is: "+this.id+" \n Description is: ");
        for(long l:this.desc)
          sb.append(l+"  ");
        sb.append("\n Price is: "+this.price);
        return sb.toString();
    }
}
