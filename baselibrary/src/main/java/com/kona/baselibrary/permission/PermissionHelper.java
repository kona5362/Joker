package com.kona.baselibrary.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2018/2/28.
 */

public class PermissionHelper {


    private Object mObj;
    private int mRequestCode;

    private PermissionHelper(Object obj) {
        this.mObj = obj;
    }

    public static PermissionHelper with(Object obj){
        return new PermissionHelper(obj);
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public void request(String... permissions){
        Activity activity = getActivity(mObj);
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permission);
            }
        }
        if (permissionList.size() == 0) {
            //执行权限允许后的方法
            excuteRequestPermissionSuccessedEvent(mObj,mRequestCode);
            return;
        }
        String[] strArr = new String[0];//给下面方法做模子用哒
        String[] permissionArr = permissionList.toArray(strArr);
        //请求权限
        ActivityCompat.requestPermissions(activity, permissionArr, mRequestCode);
    }



    public static void requestPermissions(Object obj, int reuqestCode, String... permissions) {
        Activity activity = getActivity(obj);
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permission);
            }
        }
        if (permissionList.size() == 0) {
            //执行权限允许后的方法
            excuteRequestPermissionSuccessedEvent(obj,reuqestCode);
            return;
        }
        String[] strArr = new String[0];//给下面方法做模子用哒
        String[] permissionArr = permissionList.toArray(strArr);
        //请求权限
        ActivityCompat.requestPermissions(activity, permissionArr, reuqestCode);
    }

    private static void excuteRequestPermissionSuccessedEvent(Object obj, int requestCode) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            PermissionRequestSuccessedEvent annotation = method.getAnnotation(PermissionRequestSuccessedEvent.class);
            if (annotation != null) {
                if (requestCode != annotation.requestCode()) {
                    return;
                }
                method.setAccessible(true);
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void excuteRequestPermissionFailureEvent(Object obj, int requestCode) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            PermissionRequestFailureEvent annotation = method.getAnnotation(PermissionRequestFailureEvent.class);
            if (annotation != null) {
                if (requestCode != annotation.requestCode()) {
                    return;
                }
                method.setAccessible(true);
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Activity getActivity(Object obj) {
        if (obj instanceof Activity) {
            return (Activity) obj;
        } else if (obj instanceof Fragment) {
            return ((Fragment) obj).getActivity();
        }
        throw new IllegalArgumentException("argument is not a activity or fragment!");
    }


    public static void requestPermissionsResult(int requestCode, int[] grantResults, Object obj) {
        if (grantResults == null || grantResults.length == 0) {
            return;
        }
        boolean isAllPermissionsGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionsGranted = false;
            }
        }

        if (isAllPermissionsGranted) {
            excuteRequestPermissionSuccessedEvent(obj, requestCode);
        }else{
            excuteRequestPermissionFailureEvent(obj, requestCode);
        }

    }
}
