package com.example.administrator.ijkplayer_test;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "http://vimg2.ws.126.net/image/snapshot/2016/11/I/M/VC62HMUIM.jpg";
    private static final int CODE_FOR_WRITE_PERMISSION = 100;

    private boolean mBackPressed;
    private TableLayout mHudView;
    private Toolbar toolbar;
    private ViewPlayer viewPlayer;
    private LinearLayout llLayout;
    private TextView tvSomething;
    public DrawerLayout drawerlayout;
    private boolean PermisstionWriteExternalStorage;
    private static boolean isFirstTime=true;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        viewPlayer = (ViewPlayer) findViewById(R.id.viewplayer);
        llLayout = (LinearLayout) findViewById(R.id.ll_layout);
        tvSomething = (TextView) findViewById(R.id.tv_something);
        drawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);

        if (Build.VERSION.SDK_INT > 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                Activity activty = this;
                ActivityCompat.requestPermissions(activty, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CODE_FOR_WRITE_PERMISSION);
                return;
            }
        }
        drawerlayout.openDrawer(Gravity.START);
        //Glide.with(this).load(IMAGE_URL).fitCenter().into(viewPlayer.thumbView);
        setSupportActionBar(toolbar);
        drawerToggle= new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerToggle.syncState();
        drawerlayout.addDrawerListener(drawerToggle);

    }

    public void init_PathAndTitle(String path,String title){
        if(!isFirstTime){
            viewPlayer.onPause();
            viewPlayer.onDestroy();
        }
        viewPlayer.init()
                .setSourcePath(path)
                .setTitle(title)
                .enableDanmaku(false) ;
        isFirstTime=false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意使用write
                PermisstionWriteExternalStorage = true;
            } else {
                //用户不同意，自行处理即可
                PermisstionWriteExternalStorage = false;
            }
        }
        return;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            tvSomething.setVisibility(View.GONE);
            llLayout.setVisibility(View.GONE);
        } else {
            tvSomething.setVisibility(View.VISIBLE);
            llLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        if (viewPlayer.sharedSuccess()) {
            viewPlayer.playStart();
       }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPlayer.onResume();
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
        viewPlayer.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        int destroyPosition=viewPlayer.onDestroy();
        isFirstTime=true;
        finish();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPlayer.onPause();
    }
}