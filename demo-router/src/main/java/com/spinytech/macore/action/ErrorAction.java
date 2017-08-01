package com.spinytech.macore.action;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by wanglei on 2016/12/28.
 */

/**
 * @author gs
 * @version v0.0.0
 * @title 错误Action信息
 * @descp
 * @date 2017/8/1
 **/
public class ErrorAction extends MaAction {

    private static final String DEFAULT_MESSAGE = "Something was really wrong. Ha ha!";
    private int mCode;
    private String mMessage;
    private boolean mAsync;

    public ErrorAction() {
        mCode = MaActionResult.CODE_ERROR;
        mMessage = DEFAULT_MESSAGE;
        mAsync = false;
    }

    public ErrorAction(boolean isAsync, int code, String message) {
        this.mCode = code;
        this.mMessage = message;
        this.mAsync = isAsync;
    }

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return mAsync;
    }

    @Override
    public MaActionResult invoke(Context context, HashMap<String, String> requestData) {
        MaActionResult result = new MaActionResult.Builder()
                .code(mCode)
                .msg(mMessage)
                .data(null)
                .object(null)
                .build();
        return result;
    }

}
