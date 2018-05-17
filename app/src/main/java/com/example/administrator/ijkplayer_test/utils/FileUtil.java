package com.example.administrator.ijkplayer_test.utils;

import java.io.File;

public class FileUtil {
    public static void CreateFileDir(String path){
        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }else if(fileDir.isDirectory()){
            fileDir.delete();
            fileDir.mkdirs();
        }
    }
}
