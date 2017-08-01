package com.spinytech.maindemo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.spinytech.macore.router.LocalRouter;

/**
 * @author gs
 * @version v0.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 2017/8/1
 * @company 中鸿互联企业服务股份有限公司
 **/
public class MyApplication extends Application {


    private MyApplicationImp mMyApplicationImp;

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mMyApplicationImp = new MyApplicationImp();
        mMyApplicationImp.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplicationImp.init(this);
        LocalRouter.openDebug();

        mMyApplicationImp.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMyApplicationImp.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMyApplicationImp.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mMyApplicationImp.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mMyApplicationImp.onTrimMemory(level);
    }


}
