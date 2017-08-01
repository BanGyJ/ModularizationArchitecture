package com.spinytech.macore.base;

import android.app.Application;

/**
 * @author gs
 * @version v1.0.0
 * @title 初始化对外服务
 * @descp ${DES}
 * @date 2017/7/31
 */
public interface IProvider {

    /**
     *
     *
     * @param context ctx
     */
    void init(Application context);

}
