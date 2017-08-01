package com.spinytech.macore.tools;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.spinytech.macore.base.Consts;
import com.spinytech.macore.base.InitializeRouter;
import com.spinytech.macore.helper.WideRouterHelper;
import com.spinytech.macore.implement.BaseApplicationLogic;
import com.spinytech.macore.implement.PriorityLogicWrapper;
import com.spinytech.macore.implement.WideRouterApplicationLogic;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.router.WideRouter;
import com.spinytech.macore.service.WideRouterConnectService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author gs
 * @version v1.0.0
 * @title Logic注册帮助类
 * @descp ${DES}
 * @date 17-4-7
 * @company 中鸿互联企业服务股份有限公司
 */
public final class PriorityLogicUtils {
    private ArrayList<PriorityLogicWrapper> mLogicList;
    private HashMap<String, ArrayList<PriorityLogicWrapper>> mLogicClassMap = new HashMap<>();
    private InitializeRouter initialize;
    private Application sInstance;


    /**
     * 注册初始化
     *
     * @param processName
     * @param priority
     * @param logicClass
     * @return
     */
    public boolean registerApplicationLogic(String processName, int priority, @NonNull Class<?
            extends BaseApplicationLogic> logicClass) {

        if (null == mLogicClassMap) return false;

        ArrayList<PriorityLogicWrapper> tempList = mLogicClassMap.get(processName);
        if (null == tempList) {
            tempList = new ArrayList<>();
            mLogicClassMap.put(processName, tempList);
        } else {
            //避免重复注册
            for (PriorityLogicWrapper priorityLogicWrapper : tempList) {
                if (logicClass.getName().equals(priorityLogicWrapper.logicClass.getName())) {
                    throw new RuntimeException(logicClass.getName() + " has registered.");
                }
            }
        }
        PriorityLogicWrapper priorityLogicWrapper = new PriorityLogicWrapper(priority,
                logicClass);
        tempList.add(priorityLogicWrapper);

        return true;

    }


    private void dispatchLogic() {
        if (null != mLogicClassMap) {
            mLogicList = mLogicClassMap.get(ProcessUtil.getProcessName());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void instantiateLogic() {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        Collections.sort(mLogicList);
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper) continue;
            try {
                priorityLogicWrapper.instance = priorityLogicWrapper.logicClass
                        .newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Logger.e("PriorityLogicUtils", e.getMessage());
            }
            if (null == priorityLogicWrapper.instance) return;

            priorityLogicWrapper.instance.setApplication(sInstance);
        }


    }

    public void onCreate(Application application) {
        sInstance = application;

        startWideRouter();
        initializeLogic();
        dispatchLogic();
        instantiateLogic();

        init();


    }

    private void init() {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper || null == priorityLogicWrapper.instance) continue;
            priorityLogicWrapper.instance.onCreate();
        }
    }


    public void onTerminate() {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper || null == priorityLogicWrapper.instance) continue;
            priorityLogicWrapper.instance.onTerminate();
        }

    }

    public void onLowMemory() {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper || null == priorityLogicWrapper.instance) continue;
            priorityLogicWrapper.instance.onLowMemory();
        }

    }


    public void onTrimMemory(int level) {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper || null == priorityLogicWrapper.instance) continue;
            priorityLogicWrapper.instance.onTrimMemory(level);
        }

    }


    public void onConfigurationChanged(Configuration newConfig) {
        if (null == mLogicList || mLogicList.size() <= 0) return;
        for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
            if (null == priorityLogicWrapper || null == priorityLogicWrapper.instance) continue;
            priorityLogicWrapper.instance.onConfigurationChanged(newConfig);
        }
    }


    /**
     * 是否需要开启远程路由
     */
    protected  void startWideRouter() {
        if (needMultipleProcess()) {
            LocalRouter.getInstance().setWideRouter(new WideRouterHelper());
            //开启AIDL远程服务
            PriorityLogicUtils.getInstance()
                    .registerApplicationLogic(WideRouter.PROCESS_NAME, 1000,
                            WideRouterApplicationLogic
                                    .class);
            Logger.e(Consts.TAG, (sInstance == null) + "::" + sInstance);
                    Intent intent = new Intent(sInstance, WideRouterConnectService.class);
                    sInstance.startService(intent);
        }
    }


    public void initializeAllProcessRouter() {
        if (initialize == null) return;
        initialize.initializeAllProcessRouter();
    }


    public void initializeLogic() {
        if (initialize == null) return;
        initialize.initializeLogic();
    }


    public boolean needMultipleProcess() {
        if (initialize == null) return false;
        return initialize.needMultipleProcess();
    }

    public void setInitialize(InitializeRouter router) {
        initialize = router;
    }


    public static PriorityLogicUtils getInstance() {
        return Holder.PRIORITY_LOGIC_UTILS;
    }

    @SuppressWarnings("StaticFieldLeak")
    private static final class Holder {
        private static final PriorityLogicUtils PRIORITY_LOGIC_UTILS = new PriorityLogicUtils();
    }
}
