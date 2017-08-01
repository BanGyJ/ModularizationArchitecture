package com.spinytech.macore.action;


import android.app.Application;

import com.spinytech.macore.base.IAction;

import java.util.HashMap;

/**
 * Created by wanglei on 2016/11/29.
 * 服务提供者
 */
@SuppressWarnings("FieldCanBeLocal")
public  class GroupMate {
    //TODO this field is used for control the provider on and off
    private boolean mValid = true;
    /*服务所对应的活动表*/
    private HashMap<String, IAction> mActions;

    public GroupMate() {
        mActions = new HashMap<>();
       // registerActions();
    }

    /**
     * 初始化当前服务
     *
     * @param application
     */
    public void init(Application application) {

    }


    public void registerAction(String actionName, IAction action) {
        mActions.put(actionName, action);
    }

    /**
     * 查找活动
     *
     * @param actionName
     * @return
     */
    public IAction findAction(String actionName) {
        return mActions.get(actionName);
    }

    public boolean isValid() {
        return mValid;
    }

   /* *//**
     * 向服务中注册活动
     *//*
    protected abstract void registerActions();*/
}
