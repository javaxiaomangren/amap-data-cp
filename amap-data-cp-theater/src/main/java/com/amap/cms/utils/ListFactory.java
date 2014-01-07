 package com.amap.cms.utils;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 public class ListFactory {

     private List<Map<Integer,Integer>>mlist=null;

     @SuppressWarnings({ "rawtypes", "unchecked" })
     public List<Map<Integer,Integer>> getlist(int min,int max,int num)
     {
         mlist=new ArrayList<Map<Integer,Integer>>();
         try{
             int statnum=min;
             int endnum=0;
             for (int i=1;i<=max/num+1;i++){
                 Map map=new HashMap<Integer,Integer>();
                 endnum=num*i;
                 if(endnum>max){
                     endnum=max;
                 }
                 map.put(statnum,endnum);
                 mlist.add(map);
                 statnum=endnum+1;
                 if(statnum>max){
                     break;
                 }
             }
         }
         catch(Exception e){
             e.printStackTrace();
         }

         return mlist;
     }


 }
