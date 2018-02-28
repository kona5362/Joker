package com.kona.joker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kona.baselibrary.ExceptionCrashHandler;
import com.kona.baselibrary.permission.PermissionHelper;

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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(requestCode, grantResults, this);
    }
}
