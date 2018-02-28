package com.kona.joker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kona.baselibrary.ExceptionCrashHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        int i = 2 / 0;
    }


    private void initData() {
        File crashFile = ExceptionCrashHandler.getmInstance().getCrashFile();
        if (crashFile.exists()) {
            //上传到服务器
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(crashFile));
                char[] buffer = new char[1024];
                int len = -1;
                while ((len = inputStreamReader.read(buffer)) != -1) {
                    String str = new String(buffer, 0, len);
                    Log.e("kona", str );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void test(){
        int isPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (isPermission == PackageManager.PERMISSION_GRANTED) {
            //do
        }else {
//            1.ActivityCompat.requestPermissions();
/* 需要API23以上           2.requestPermissions(new String[]{
                    Manifest.permission.CALL_PHONE
            },1);*/
        }
    }
}
