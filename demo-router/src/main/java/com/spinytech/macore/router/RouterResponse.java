package com.spinytech.macore.router;

import com.spinytech.macore.tools.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 路由请求返回参数bean
 * Created by wanglei on 2016/12/27.
 */
public class RouterResponse {
    //标准超时时间
    private static final int TIME_OUT = 30 * 1000;
    //异步超时时间
    private long mTimeOut = 0;
    //是否已经存在
    private volatile boolean mHasGet = false;
    //是否是异步
    public boolean mIsAsync = true;
    //返回码
    int mCode = -1;
    //返回消息
    String mMessage = "";
    //反复数据
    String mData;
    //实体
    Object mObject;

    /**
     * 返回结果串
     * This field is MaActionResult.toString()
     */
    public String mResultString;
    //异步数据处理
    public Future<String> mAsyncResponse;

    public RouterResponse() {
        this(TIME_OUT);
    }

    public RouterResponse(long timeout) {
        if (timeout > TIME_OUT * 2 || timeout < 0) {
            timeout = TIME_OUT;
        }
        mTimeOut = timeout;
    }

    public boolean isAsync() {
        return mIsAsync;
    }

    /**
     * 获取异步返回结果
     *
     * @return
     * @throws Exception
     */
    public synchronized String get() throws Exception {
        if (mIsAsync) {
            mResultString = mAsyncResponse.get(mTimeOut, TimeUnit.MILLISECONDS);
            if (!mHasGet) {
                try {
                    JSONObject jsonObject = new JSONObject(mResultString);
                    this.mCode = jsonObject.getInt("code");
                    this.mMessage = jsonObject.getString("msg");
                    this.mData = jsonObject.getString("data");
                } catch (JSONException e) {
                    Logger.e("RouterResponse", e.getMessage());
                }
                mHasGet = true;
            }
        }
        return mResultString;
    }

    public int getCode() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mCode;
    }

    public String getMessage() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mMessage;
    }

    public String getData() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mData;
    }

    public Object getObject() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mObject;
    }

}
