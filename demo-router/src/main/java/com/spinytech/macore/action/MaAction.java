package com.spinytech.macore.action;

import android.content.Context;

import com.spinytech.macore.base.IAction;

import java.util.HashMap;

/**
 * Created by wanglei on 2016/11/29.
 */

public abstract class MaAction implements IAction {
    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData, Object object) {
        return false;
    }

    @Override
    public MaActionResult invoke(Context context, HashMap<String, String> requestData,
                                 Object object) {
        return new MaActionResult.Builder().code(MaActionResult.CODE_NOT_IMPLEMENT).msg(
                "This method has not yet been implemented.").build();
    }


}
