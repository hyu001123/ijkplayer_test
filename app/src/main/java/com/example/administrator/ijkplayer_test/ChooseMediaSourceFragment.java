package com.example.administrator.ijkplayer_test;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.ijkplayer_test.db.CCTV;
import com.example.administrator.ijkplayer_test.db.GDTV;
import com.example.administrator.ijkplayer_test.db.MyLove;
import com.example.administrator.ijkplayer_test.db.Others;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseMediaSourceFragment extends Fragment implements View.OnClickListener{
    private Button btn_cv;
    private Button btn_gd;
    private Button btn_love;
    private Button btn_other;
    private ListView lv_tv;
    private List<Map<String ,String>> dataList=new ArrayList<>();
    private List<CCTV> DbList;
    private int currentLevel;
    private static final int LEVEL_CCTV=1;
    private static final int LEVEL_GDTV=2;
    private static final int LEVEL_MYLOVE=3;
    private static final int LEVEL_OTHERS=4;
    private List<GDTV> GDList;
    private List<MyLove> MyList;
    private List<Others> OtList;
    private TVAdapter adapter;
    private boolean isLove=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_mediasource,container,false);
        btn_cv=(Button)view.findViewById(R.id.CCTV);
        btn_gd=(Button)view.findViewById(R.id.GDTV);
        btn_love=(Button)view.findViewById(R.id.myLove);
        btn_other=(Button)view.findViewById(R.id.others);
        lv_tv=(ListView)view.findViewById(R.id.lv_TV);
        adapter=new TVAdapter(getContext(),dataList);
        lv_tv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initData();
        btn_cv.setOnClickListener(this);
        btn_gd.setOnClickListener(this);
        btn_love.setOnClickListener(this);
        btn_other.setOnClickListener(this);
        btn_cv.setSelected(true);
        lv_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_CCTV){
                    List<CCTV> Db = DataSupport.findAll(CCTV.class);
                    MainActivity activity = (MainActivity) getActivity();
                        activity.init_PathAndTitle(Db.get(position).getUrl(),Db.get(0).getName());
                    activity.drawerlayout.closeDrawers();
                }else if(currentLevel==LEVEL_GDTV){
                    List<GDTV> Db = DataSupport.findAll(GDTV.class);
                    MainActivity activity = (MainActivity) getActivity();
                        activity.init_PathAndTitle(Db.get(position).getUrl(),Db.get(0).getName());
                    activity.drawerlayout.closeDrawers();
                }else if(currentLevel==LEVEL_MYLOVE){
                    List<MyLove> Db = DataSupport.findAll(MyLove.class);
                    MainActivity activity = (MainActivity) getActivity();
                        activity.init_PathAndTitle(Db.get(position).getUrl(),Db.get(0).getName());
                    activity.drawerlayout.closeDrawers();
                }else if(currentLevel==LEVEL_OTHERS){
                    List<Others> Db = DataSupport.findAll(Others.class);
                    MainActivity activity = (MainActivity) getActivity();
                        activity.init_PathAndTitle(Db.get(position).getUrl(),Db.get(0).getName());
                    activity.drawerlayout.closeDrawers();
                }
            }
        });
        querySource_CCTV();
    }

    private void initData() {
        CCTV cv = new CCTV();
        cv.setName("湖南卫视HD");
        cv.setUrl("http://124.224.238.171/PLTV/88889012/224/3221225929/10000100000000060000000000758059_0.smil");
        cv.setLove(false);
        cv.save();
        CCTV cv1=new CCTV();
        cv1.setName("CCTV-1综合-HD");
        cv1.setUrl("http://hnsyx.chinashadt.com:2036/live/tv16.stream/playlist.m3u8");
        cv1.setLove(false);
        cv1.save();
        GDTV gd = new GDTV();
        gd.setName("南方卫视");
        gd.setUrl("http://stream1.grtn.cn/tvs2/sd/live.m3u8?_upt=c53b7f701516548742");
        gd.setLove(false);
        gd.save();
        GDTV gd1 = new GDTV();
        gd1.setName("旅游卫视");
        gd1.setUrl("http://stream1.hnntv.cn/lywsgq/sd/live.m3u8");
        gd1.setLove(false);
        gd1.save();
        MyLove mylove = new MyLove();
        mylove.setName("凤凰中文");
        mylove.setUrl("rtmp://hbzx.chinashadt.com:2036/zhibo/zx2.stream");
        mylove.setLove(true);
        mylove.save();
        Others other = new Others();
        other.setName("凤凰香港");
        other.setUrl("http://223.110.243.165/ott.js.chinamobile.com/PLTV/2510088/224/3221227183/index.m3u8");
        other.setLove(false);
        other.save();

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CCTV:
                querySource_CCTV();
                break;
            case R.id.GDTV:
                GDList = DataSupport.findAll(GDTV.class);
                if(GDList.size()>0){
                    dataList.clear();
                    for(GDTV  gdtv:GDList){
                        Map<String,String> map=new HashMap<>();
                        map.put("name",gdtv.getName());
                        map.put("love",String.valueOf(gdtv.isLove()));
                        dataList.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    lv_tv.setSelection(0);
                    currentLevel=LEVEL_GDTV;
                }else{
                    initData();
                }
                break;
            case R.id.myLove:
                MyList = DataSupport.findAll(MyLove.class);
                if(MyList.size()>0){
                    dataList.clear();
                    for(MyLove  my:MyList){
                        Map<String,String> map=new HashMap<>();
                        map.put("name",my.getName());
                        map.put("love",String.valueOf(my.isLove()));
                        dataList.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    lv_tv.setSelection(0);
                    currentLevel=LEVEL_MYLOVE;
                }else{
                    initData();
                }
                break;
            case R.id.others:
                OtList = DataSupport.findAll(Others.class);
                if(OtList.size()>0){
                    dataList.clear();
                    for(Others  ot:OtList){
                        Map<String,String> map=new HashMap<>();
                        map.put("name",ot.getName());
                        map.put("love",String.valueOf(ot.isLove()));
                        dataList.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    lv_tv.setSelection(0);
                    currentLevel=LEVEL_OTHERS;
                }else{
                    initData();
                }
                break;
        }
    }

    private void querySource_CCTV() {
        DbList = DataSupport.findAll(CCTV.class);
        if(DbList.size()>0){
            dataList.clear();
            for(CCTV  cctv:DbList){
                Map<String,String> map=new HashMap<>();
                map.put("name",cctv.getName());
                map.put("love",String.valueOf(cctv.isLove()));
                dataList.add(map);
            }
            adapter.notifyDataSetChanged();
            lv_tv.setSelection(0);
            currentLevel=LEVEL_CCTV;
        }else{
            initData();
        }
    }


    public class TVAdapter extends BaseAdapter{


        private final Context context;
        private final List<Map<String, String>> listdata;


        public TVAdapter(Context context, List<Map<String, String>> listdata){
            this.context=context;
            this.listdata=listdata;
        }

        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Object getItem(int position) {
            return listdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
               convertView= View.inflate(context,R.layout.item_tv,null);
                holder.tv_name=(TextView)convertView.findViewById(R.id.tv_tvName);
                holder.btn_fab=(FloatingActionButton)convertView.findViewById(R.id.btn_fab);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            holder.tv_name.setText(listdata.get(position).get("name"));
            if(currentLevel!=LEVEL_MYLOVE) {
                holder.btn_fab.setVisibility(View.VISIBLE);
                if (listdata.get(position).get("love").equals("true")) {
                    holder.btn_fab.setImageResource(R.drawable.ic_favorite_red_700_24dp);
                } else {
                    holder.btn_fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }else{
                holder.btn_fab.setVisibility(View.INVISIBLE);
            }
            final ViewHolder finalHolder = holder;
            holder.btn_fab.setOnClickListener(new View.OnClickListener() {
                public String tvName;
                public String tvUrl;
                public boolean tvIslove;

                @Override
                public void onClick(View v) {
                    if (currentLevel== LEVEL_CCTV) {
                        List<CCTV> Db = DataSupport.where("id=?", String.valueOf(position + 1)).find(CCTV.class);
                        isLove = Db.get(0).isLove();
                        finalHolder.btn_fab.setImageResource(!isLove ? R.drawable.ic_favorite_red_700_24dp : R.drawable.ic_favorite_border_white_24dp);
                        Db.get(0).setLove(!isLove);
                        Db.get(0).save();
                        tvName=Db.get(0).getName();
                        tvUrl=Db.get(0).getUrl();
                        tvIslove=!isLove;
                    }else if(currentLevel==LEVEL_GDTV) {
                        List<GDTV> Db1 = DataSupport.where("id=?", String.valueOf(position + 1)).find(GDTV.class);
                        isLove = Db1.get(0).isLove();
                        finalHolder.btn_fab.setImageResource(!isLove ? R.drawable.ic_favorite_red_700_24dp : R.drawable.ic_favorite_border_white_24dp);
                        Db1.get(0).setLove(!isLove);
                        Db1.get(0).save();
                        tvName=Db1.get(0).getName();
                        tvUrl=Db1.get(0).getUrl();
                        tvIslove=!isLove;
                    }else if(currentLevel==LEVEL_OTHERS) {
                        List<Others> Db2 = DataSupport.where("id=?", String.valueOf(position + 1)).find(Others.class);
                        isLove = Db2.get(0).isLove();
                        finalHolder.btn_fab.setImageResource(!isLove ? R.drawable.ic_favorite_red_700_24dp : R.drawable.ic_favorite_border_white_24dp);
                        Db2.get(0).setLove(!isLove);
                        Db2.get(0).save();
                        tvName=Db2.get(0).getName();
                        tvUrl=Db2.get(0).getUrl();
                        tvIslove=!isLove;
                    }
                    if(tvIslove){
                        MyLove myLove=new MyLove();
                        myLove.setName(tvName);
                        myLove.setUrl(tvUrl);
                        myLove.setLove(tvIslove);
                        myLove.save();
                    }else{
                       DataSupport.deleteAll(MyLove.class,"name = ?",tvName);
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView tv_name;
            private FloatingActionButton btn_fab;
        }

    }
}
