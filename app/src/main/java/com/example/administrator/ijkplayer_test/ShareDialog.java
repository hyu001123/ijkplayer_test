package com.example.administrator.ijkplayer_test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ijkplayer_test.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ShareDialog extends DialogFragment implements View.OnClickListener{
    private static Bitmap bitmap;
    private ImageView shotPhoto;
    private TextView tvShare;
    private TextView tvCancel;
    private TextView tvStore;
    private String path;
    private String filepath;
    private boolean isDismiss;
    private boolean shareok;
    private boolean isShared;

    public static ShareDialog newInstance(String title) {
        ShareDialog shareDialog = new ShareDialog();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        shareDialog.setArguments(arg);
        return shareDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"));
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_share, null);
        path=getContext().getCacheDir().getPath()+ File.separator+"ijkPlayerScreenShot";
        setUIView(view);
        builder.setView(view);
        AlertDialog dialog = (AlertDialog) builder.create();
        return dialog;
    }


    @Override
    public void onResume() {
        if(isShared){
            shareok=true;
            isShared=false;
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDismiss=true;
        LogUtil.logUtilMyself("tag","destroyView");
    }

    private void setUIView(View view) {
        shotPhoto = (ImageView) view.findViewById(R.id.iv_screenshot_photo);
        tvShare=(TextView)view.findViewById(R.id.btn_share);
        tvCancel=(TextView)view.findViewById(R.id.btn_cancel);
        tvStore=(TextView)view.findViewById(R.id.btn_store);
        ViewGroup.LayoutParams para = shotPhoto.getLayoutParams();
        para.width=getResources().getDisplayMetrics().widthPixels*7/10;
        para.height=getResources().getDisplayMetrics().heightPixels*7/10;
        shotPhoto.setLayoutParams(para);
        if (bitmap != null) {
            shotPhoto.setImageBitmap(bitmap);
        }
        tvStore.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvShare.setOnClickListener(this);
    }

    public  ShareDialog  setPhoto(Bitmap photo) {
        bitmap = photo;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_store:
                storePhoto();
                break;
            case R.id.btn_share:
                sharePhoto();
                break;
            case R.id.btn_cancel:
                break;
        }
        dismiss();
    }


    public boolean DialogOver(){
        if(shareok){
             return true;
        }else{
            return isDismiss;
        }
    }

    private void sharePhoto() {
        filepath=path+File.separator+System.currentTimeMillis()+".jpg";
        File shareFile=new File(filepath);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(shareFile));
            String url=insertImageToSystem(getActivity(), filepath,shareFile);;
//用Intent.ACTION_SEND 创建intent对象
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(url));
            startActivity(Intent.createChooser(intent,"分享"));
            isShared=true;
        } catch (Exception e) {
            Toast.makeText(getContext(),"保存本地失败",Toast.LENGTH_LONG).show();
        }
    }

    private String insertImageToSystem(FragmentActivity activity, String filepath,File sharefile) {
        String url = "";
        try {
            url = MediaStore.Images.Media.insertImage(activity.getContentResolver(), filepath, sharefile.getName(), "share_photo");
            LogUtil.logUtilMyself("info","sharePhoto================"+sharefile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }

    private void storePhoto(){
        filepath=path+File.separator+System.currentTimeMillis()+".jpg";
        File file=new File(filepath);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                insertImageToSystem(getActivity(),filepath,file);
                Toast.makeText(getContext(),"保存本地成功",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getContext(),"保存本地失败"+e,Toast.LENGTH_LONG).show();
                e.printStackTrace();
        }
    }
}
