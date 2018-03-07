package com.kona.joker;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kona.baselibrary.ExceptionCrashHandler;
import com.kona.baselibrary.permission.PermissionHelper;
import com.kona.baselibrary.permission.PermissionRequestFailureEvent;
import com.kona.baselibrary.permission.PermissionRequestSuccessedEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
//        int i = 2 / 0;
        testPermission();

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


    private void testPermission(){
//        方式一：
//      PermissionHelper.requestPermissions(this,1, Manifest.permission.CALL_PHONE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //方式二：
        PermissionHelper.with(this)
                .requestCode(1)
                .request(Manifest.permission.CALL_PHONE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(requestCode, grantResults, this);
    }

    @PermissionRequestSuccessedEvent(requestCode = 1)
    public void callPhone(){
        Toast.makeText(this, "打电话！", Toast.LENGTH_SHORT).show();
    }
    @PermissionRequestFailureEvent(requestCode = 1)
    public void failure(){
        Toast.makeText(this, "全县拒绝！", Toast.LENGTH_SHORT).show();
    }
}
