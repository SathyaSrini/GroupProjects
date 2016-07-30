import java.util.ArrayList;
/**
 * Created by sags on 4/2/16.
 */
import java.util.*;

/**
 * Created by G119
 */

public class MDS {

    private int orig_pos=0;

    ArrayList<Entry> entry=new ArrayList<Entry>();

    //Temporary DS:
    TreeMap<Long,ArrayList<Entry>> desc_tempMap= new TreeMap<>();
    TreeMap<Long,Entry> id_tempMap= new TreeMap<>();
    TreeMap<Double,ArrayList<Entry>> price_tempMap= new TreeMap<>();
    LinkedHashMap<Long,Entry> id_tempHash=new LinkedHashMap<>();
    LinkedHashMap<Long,ArrayList<Entry>> desc_tempHash=new LinkedHashMap<>();
    LinkedHashMap<Long,Entry> greateightds=new LinkedHashMap<>();

    void deleteTempDesc(long[] del_desc, long del_id){
        int flag=0;
        for(long deldesc:del_desc){
            ArrayList<Entry> del_ele=desc_tempHash.get(deldesc);
            int index=0;
            for (Entry e:del_ele){
                index++;
                if (e.id==del_id){
                    flag=1;
                    break;
                }
            }
            if (index>0 && flag==1)
                del_ele.remove(index-1);
            desc_tempHash.put(deldesc,del_ele);
        }
        flag=0;
        for(long deldesc:del_desc){
            ArrayList<Entry> del_ele=desc_tempMap.get(deldesc);
            int index=0;
            for (Entry e:del_ele){
                index++;
                if (e.id==del_id){
                    flag=1;
                    break;
                }
            }
            if(index>0 && flag==1)
                del_ele.remove(index-1);
            desc_tempMap.put(deldesc,del_ele);
        }
    }

    void builddescTree(Entry e, boolean flag, long[] del_desc, long del_id){
        if (!flag)
            deleteTempDesc(del_desc,del_id);
        else {
            for (long desc : e.desc) {
                if (desc!=0) {
                    ArrayList<Entry> tempList = new ArrayList<>();

                    if (desc_tempMap.get(desc) != null) {
                        tempList = desc_tempMap.get(desc);
                        tempList.add(e);
                        desc_tempMap.put(desc, tempList);
                        desc_tempHash.put(desc, tempList);
                    } else {
                        tempList.add(e);
                        desc_tempMap.put(desc, tempList);
                        desc_tempHash.put(desc, tempList);
                    }
                }
            }
        }
    }

    void buildpriceTree(Entry e,boolean flag,double price1,long id){
        double price;
        if(flag)
            price=e.price;
        else
            price=price1;
        ArrayList<Entry>tempList = new ArrayList<>();
        if(price_tempMap.get(price)!=null)
        {
            tempList = price_tempMap.get(price);
            if(flag) tempList.add(e);
            else {
                int index=0;
                for (Entry e1:tempList){
                    index++;
                    if (e1.id==id)
                        break;
                }
                tempList.remove(index-1);
            }
            price_tempMap.put(price,tempList);
        }
        else
        {
            tempList.add(e);
            price_tempMap.put(price,tempList);
        }
    }

    void  rebuildIndices(boolean flag,long id)
    {
        Entry e = entry.get(entry.size()-1);
        if(flag){
            Entry e1=id_tempHash.get(id);
            if (e1!=null)
            {
                builddescTree(e1,false,e1.desc,e1.id);
                buildpriceTree(e1,false,e1.price,e1.id);
            }
            builddescTree(e,flag,e.desc,e.id);
            buildpriceTree(e,flag,e.price,e.id);
            id_tempMap.put(e.id,e);
            id_tempHash.put(e.id,e);
            if(e.desc.length>=8)
                greateightds.put(e.id,e);
        }
        else{
            Entry e1=id_tempHash.get(id);
            double price=e1.price;
            long[] desc=e1.desc;
            id_tempMap.remove(id);
            id_tempHash.remove(id);
            greateightds.remove(id);
            builddescTree(e, flag, desc, id);
            buildpriceTree(e,flag,price,id);
        }
    }

    void updateEntry(long id,double price){
        Entry tmp=id_tempHash.get(id);
        PriceDSChanged(tmp,price);
        tmp.price=price;
        id_tempHash.put(id,tmp);
        id_tempMap.put(id, tmp);
        DescDSChanged(tmp.desc,id,price);
    }

    int insert(long id, double price, long[] description, int size) {
        // Description of item is in description[0..size-1].
        // Copy them into your data structure.
        long[] desc1= new long[size];
        int cnt=0;
        for (long desc:description)
            if (cnt>=size) break;
            else  desc1[cnt++]=desc;
        int flag;
        if (id_tempHash.get(id)!=null)
            flag=1;
        else flag=0;

        if(size>0){
            entry.add(new Entry(id,price,desc1,size,orig_pos++));
            this.rebuildIndices(true,id);
        }
        else{
            updateEntry(id,price);
        }
        if(flag==0)
            return 1;
        else return 0;
    }

    /*void print()
    {
        Set<Long> key=id_tempHash.keySet();
        for(Long l:key)
        {
            Entry e=id_tempHash.get(l);
            System.out.println(e.toString());
        }
    }*/

    double find(long id)
    {
        Entry tmp=id_tempHash.get(id);
        /*System.out.println("Find is: "+tmp==null?0:tmp.price);*/
        return tmp==null?0:tmp.price;
    }

    long delete(long id) {
        int cnt=0;
        /*for(Entry e:entry){
            if (e.id==id){
                cnt++;
                break;
            }
        }
        if (cnt>0)
            entry.remove(cnt-1);*/
        long[] del_des=id_tempHash.get(id).desc;
        long sum=0;
        for (long del:del_des)
            sum+=del;
        this.rebuildIndices(false,id);
        return sum;
    }

    double findMinPrice(long des) {
        ArrayList<Entry> price_list=desc_tempMap.get(des);
        double price=Double.MAX_VALUE;
        for(Entry e:price_list){
            if (e.price<price)
                price=e.price;
        }
        return price==Double.MAX_VALUE?0:price;
    }

    double findMaxPrice(long des) {
        ArrayList<Entry> price_list=desc_tempMap.get(des);
        double price=0;
        for(Entry e:price_list){
            if (e.price>price){
                price=e.price;
                /*break;*/
            }
        }
/*        System.out.println("price is:"+price);*/
        return price;
    }

    int findPriceRange(long des, double lowPrice, double highPrice) {
        ArrayList<Entry> price_range=desc_tempMap.get(des);
        int count=0;
        for(Entry e:price_range){
            if (lowPrice<= e.price && e.price<=highPrice)
                count++;
        }
        return count;
    }

    void PriceDSChanged(Entry item,double rate){
        //change the price DS:
        ArrayList<Entry> price_mod=price_tempMap.get(item.price);
        double changed_price=item.price+(rate/100)*item.price;
        changed_price=Math.floor(changed_price*1e2)/1e2;
        int index=0;
        Entry to_add = null;
        for(Entry price_tem:price_mod){
            index++;
            if (price_tem.id==item.id){
                to_add=price_tem;
                break;}
        }
        if (index>0 && to_add!=null){
            price_mod.remove(index-1);
        }
        price_tempMap.put(item.price,price_mod);
        ArrayList<Entry> insert_price=price_tempMap.get(changed_price);
        if(insert_price!=null){
            insert_price.add(to_add);
            price_tempMap.put(changed_price,insert_price);
        }
        else{
            insert_price=new ArrayList<>();
            insert_price.add(to_add);
            price_tempMap.put(changed_price,insert_price);
        }
    }

    void DescDSChanged(long[] item,long id,double rate){
        //change the desc DS
        final double fixed_price=rate;
        for(long desc_change:item){
            ArrayList<Entry> map_desctmp=desc_tempMap.get(desc_change);
            int index=0;
            Entry tmp=null;

            for(Entry tmp_entry:map_desctmp){
                index++;
                if (tmp_entry.id==id){
                    tmp=tmp_entry;
                    break;
                }
            }

            if (index>0 && tmp!=null)
            {
                map_desctmp.remove(index-1);
                tmp.price=fixed_price;
                map_desctmp.add(tmp);
                desc_tempMap.put(desc_change,map_desctmp);
                desc_tempHash.put(desc_change,map_desctmp);
            }
        }
    }

    void IDDSChanged(Entry item,double rate){
        double changed_price=rate;
        Entry tmp;
        tmp=id_tempHash.get(item.id);
        tmp.price=changed_price;
        id_tempHash.put(item.id,tmp);
        id_tempMap.put(item.id,tmp);
    }


    double priceHike(long minid, long maxid, double rate) {
        Long final_min_id=id_tempMap.ceilingKey(minid); //minimum id
        Long final_max_id=id_tempMap.floorKey(maxid); //maximum id
        double net_increase=0;

        if(final_max_id!=null && final_min_id!=null)
        {
            SortedMap<Long, Entry> submap=id_tempMap.subMap(final_min_id, final_max_id);

            for(Entry item:submap.values()){
                //change price DS
                PriceDSChanged(item, rate);

                net_increase+=Math.floor((rate/100)*item.price*1e2)/1e2;

                double changed_price=item.price+(rate/100)*item.price;
                changed_price=Math.floor(changed_price*1e2)/1e2;

                entry.get(entry.indexOf(item)).price=changed_price;

                //change desc DS
                DescDSChanged(item.desc,item.id,changed_price);
                //change the ID DS:
                IDDSChanged(item,changed_price);
            }
            Entry item=id_tempMap.get(final_max_id);

            net_increase+=(rate/100)*item.price;
            net_increase=Math.floor(net_increase*1e2)/1e2;

            double changed_price=item.price+(rate/100)*item.price;
            changed_price=Math.floor(changed_price*1e2)/1e2;

            PriceDSChanged(item, rate);
            DescDSChanged(item.desc, item.id, changed_price);
            IDDSChanged(item, changed_price);
        }
        return net_increase;
    }

    int range(double lowPrice, double highPrice) {
        Set<Double> set = price_tempMap.keySet();
        int count=0;
        for(Double price:set){
            if (price>highPrice)
                break;
            else if (price>=lowPrice && price<=highPrice)
                count+=price_tempMap.get(price).size();             //efficiency terrible
        }
        return count;
    }

    int samesame()
    {
        int same_count=0,hit=0;
        HashSet<String> unique_Desc_Set=new HashSet<>();
        HashMap<String,Integer> unique_count=new HashMap<>();
        Iterator it=greateightds.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry item=(Map.Entry)it.next();
            Entry e= (Entry) item.getValue();

            Arrays.sort(e.desc);
            if(!(unique_Desc_Set.add(Arrays.toString(e.desc))))
            {
                same_count++;
                if (unique_count.get(Arrays.toString(e.desc))==null)
                {
                    same_count++;
                    unique_count.put(Arrays.toString(e.desc),same_count);
                }
            }
        }
        return same_count;
    }
}

