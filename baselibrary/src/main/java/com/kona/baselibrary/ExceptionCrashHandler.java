package com.kona.baselibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kona on 2018/1/25.
 */

public class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {

    private static ExceptionCrashHandler mInstance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private ExceptionCrashHandler() {

    }

    public static ExceptionCrashHandler getmInstance() {
        if (mInstance == null) {
            synchronized (ExceptionCrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new ExceptionCrashHandler();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;

/*        // 别写成这个了 = = 不小心把人家默认处理方式改了
        // 那getDefaultUncaughtExceptionHandler拿到的就是this了
        Thread.currentThread().setDefaultUncaughtExceptionHandler(this); */
        Thread.currentThread().setUncaughtExceptionHandler(this);
        defaultUncaughtExceptionHandler = Thread.currentThread().getDefaultUncaughtExceptionHandler();

    }

    //处理异常
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        //保存当前崩溃信息文件
        String crashFileName = saveInfoToSD(e);
        //缓存崩溃日志文件名称(方便应用启动时根据名称找到文件上传到服务器)
        cacheCrashFile(crashFileName);

        //系统默认的处理方式：
        defaultUncaughtExceptionHandler.uncaughtException(t,e);
    }

    private void cacheCrashFile(String crashFileName) {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        sp.edit().putString("CRASH_FILE_NAME", crashFileName).commit();
    }
    public File getCrashFile() {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        String crashFileName = sp.getString("CRASH_FILE_NAME", "");
        return new File(crashFileName);
    }
    private String saveInfoToSD(Throwable ex) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : obtainSimpleInfo(mContext).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }
        sb.append(obtainExceptionInfo(ex));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(mContext.getFilesDir() + File.separator + "crash" + File.separator);
            //先删除之前的异常信息
            if (dir.exists()) {
                deleteDir(dir);
            }
            //再重新创建文件夹
            if (!dir.exists()) {
                dir.mkdir();
            }

            fileName = dir.toString() + File.separator + getAssignTime("yyyy-MM-dd_HH_mm") + ".txt";
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return fileName;
    }

    private String getAssignTime(String format) {
        return new SimpleDateFormat(format).format(System.currentTimeMillis());
    }

    private boolean deleteDir(File dir) {
        //因为在这里文件夹里没有子文件夹，所以这样写就行了
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File child : children) {
                child.delete();
            }
        }
        return true;
        /* 未测试是否真的可以删除所有文件夹= =
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir,children[i]));
                if (!success) {
                    return false;
                }
            }

        }
        dir.delete();

        //目录此时为空，可以删除
        return true;
*/

    }

    private String obtainExceptionInfo(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    /**
     * 获取一些简单的信息，软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @param context
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> infoMap = new HashMap<>();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        infoMap.put("versionName", packageInfo.versionName);
        infoMap.put("versionCode", "" + packageInfo.versionCode);
        infoMap.put("MODEL", Build.MODEL);
        infoMap.put("PRODUCT", Build.PRODUCT);
        infoMap.put("MOBILE_INFO", "\n==================\n"+getMobileInfo()+"\n===============\n");

        return infoMap;
    }

    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        Field[] fields = Build.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value).append("\n");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



}
