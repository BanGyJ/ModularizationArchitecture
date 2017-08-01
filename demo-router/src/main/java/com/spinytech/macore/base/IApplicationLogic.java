package com.spinytech.macore.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

/**
 * @author gs
 * @version v1.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 2017/4/7
 */
public interface IApplicationLogic {

    Application getApplication();

    void init(@NonNull Application application);


    void attachBaseContext(Context base);

    void onCreate();

    void onTerminate();

    void onLowMemory();

    void onTrimMemory(int level);

    void onConfigurationChanged(Configuration newConfig);
}
