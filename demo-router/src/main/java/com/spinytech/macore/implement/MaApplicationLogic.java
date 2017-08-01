package com.spinytech.macore.implement;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Configuration;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.spinytech.macore.base.IApplicationLogic;
import com.spinytech.macore.base.InitializeRouter;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.tools.Logger;
import com.spinytech.macore.tools.PriorityLogicUtils;

/**
 * @author gs
 * @version v1.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 2017/4/7
 */
public abstract class MaApplicationLogic implements IApplicationLogic, InitializeRouter {
    private static final String TAG = "MaApplication";
    @SuppressLint("StaticFieldLeak")
    private volatile static Application sInstance;


    @Override
    @NonNull
    public Application getApplication() {
        return sInstance;
    }


    @Override
    @SuppressWarnings({"ReturnInsideFinallyBlock", "PMD", "FindBugs"})
    public void init(@NonNull Application application) {
        MaApplicationLogic.sInstance = application;
    }

    @CallSuper
    @Override
    public void onCreate() {
        Logger.e(TAG, "Application onCreate start: " + System.currentTimeMillis());
        //初始化本地Router
        init();
        PriorityLogicUtils.getInstance().onCreate(sInstance);
        // Traverse the application logic.
        Logger.e(TAG, "Application onCreate end: " + System.currentTimeMillis());
    }


    private void init() {
        LocalRouter.init(getApplication());
        PriorityLogicUtils.getInstance().setInitialize(this);
    }


    @CallSuper
    @Override
    public void onTerminate() {
        PriorityLogicUtils.getInstance().onTerminate();
    }

    @CallSuper
    @Override
    public void onLowMemory() {
        PriorityLogicUtils.getInstance().onLowMemory();
    }

    @CallSuper
    @Override
    public void onTrimMemory(int level) {
        PriorityLogicUtils.getInstance().onTrimMemory(level);
    }

    @CallSuper
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        PriorityLogicUtils.getInstance().onConfigurationChanged(newConfig);
    }


    protected boolean registerApplicationLogic(String processName, int priority, @NonNull Class<?
            extends BaseApplicationLogic> logicClass) {
        return PriorityLogicUtils.getInstance().registerApplicationLogic(processName, priority,
                logicClass);
    }

}
