package com.example.administrator.ijkplayer_test.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

public class BiliJsonDanmakuParser extends BaseDanmakuParser{

    public BiliJsonDanmakuParser(){

    }
    @Override
    protected IDanmakus parse() {
        if(this.mDataSource!=null&&this.mDataSource instanceof JSONSource){
            JSONSource jsonSource=(JSONSource)this.mDataSource;
            return this.doParser(jsonSource.data());
        }else{
            return new Danmakus();
        }
    }

    private IDanmakus doParser(JSONArray data) {
        Danmakus danmaku = new Danmakus();
        if(data!=null&&data.length()>0){
            for(int i=0;i<data.length();i++){
                try{
                    JSONObject obj = data.getJSONObject(i);
                    if(obj!=null){
                        danmaku=_parser(obj,danmaku);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return danmaku;
        }else{
            return danmaku;
        }
    }

    private Danmakus _parser(JSONObject obj, Danmakus danmaku) {
        if(danmaku!=null){
            danmaku=new Danmakus();
        }
        if(obj!=null&&obj.length()>0){
            for(int i=0;i<obj.length();i++){
                try{
                    String c=obj.getString("c");
                    String[] values = c.split(",");
                    if(values.length>0){
                        int type=Integer.parseInt(values[2]);
                        if(type!=7){
                            long time=(long)(Float.parseFloat(values[0])*1000.0f);
                            int color=Integer.parseInt(values[1])|-16777216;
                            Float textSize=Float.parseFloat(values[3]);
                            BaseDanmaku item = this.mContext.mDanmakuFactory.createDanmaku(type, this.mContext);
                            if(item!=null){
                                item.setTime(time);
                                item.textSize=textSize*(this.mDispDensity-0.6f);
                                item.textColor=color;
                                item.textShadowColor=color<-16777216?-1:-16777216;
                                DanmakuUtils.fillText(item,obj.optString("m","......"));
                                item.index=i;
                                item.setTimer(this.mTimer);
                                danmaku.addItem(item);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return  danmaku;
        }else{
            return  danmaku;
        }
    }
}
