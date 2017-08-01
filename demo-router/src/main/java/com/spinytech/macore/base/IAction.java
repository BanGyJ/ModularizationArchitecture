package com.spinytech.macore.base;

import android.content.Context;

import com.spinytech.macore.action.MaActionResult;

import java.util.HashMap;

/**
 * 对外提供活动接口
 * Created by wanglei on 2016/11/29.
 */
public interface IAction {
    boolean isAsync(Context context, HashMap<String, String> requestData);

    MaActionResult invoke(Context context, HashMap<String, String> requestData);

    boolean isAsync(Context context, HashMap<String, String> requestData, Object object);

    MaActionResult invoke(Context context, HashMap<String, String> requestData, Object
            object);
}
