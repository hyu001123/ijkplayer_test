package com.example.administrator.ijkplayer_test.db;

import org.litepal.crud.DataSupport;

public class Others extends DataSupport {
    private int id;
    private String name;
    private String url;
    private boolean isLove;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLove() {
        return isLove;
    }

    public void setLove(boolean love) {
        isLove = love;
    }
}
