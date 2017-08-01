package com.spinytech.macore.implement;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.spinytech.macore.base.IAction;
import com.spinytech.macore.base.IApplicationLogic;
import com.spinytech.macore.router.LocalRouter;

/**
 * 多进程的初始化分配
 * Created by wanglei on 2016/11/25.
 */
public abstract class BaseApplicationLogic implements IApplicationLogic {

    protected Application mApplication;

    public BaseApplicationLogic() {
    }

    public void setApplication(@NonNull Application application) {
        mApplication = application;
    }


    @Override
    public Application getApplication() {
        return mApplication;
    }

    @Override
    public void init(@NonNull Application application) {

    }

    @Override
    public void attachBaseContext(Context base) {

    }

    public void onCreate() {
    }

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    /**
     * 注册本地服务
     *
     * @param providerName
     * @param action
     */
    protected void registerAction(String providerName, IAction action) {
        LocalRouter.getInstance().registerProvider(providerName, action);
    }

}


