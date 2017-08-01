package com.spinytech.macore.router;

import android.app.Application;
import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.spinytech.macore.action.ErrorAction;
import com.spinytech.macore.action.GroupMate;
import com.spinytech.macore.action.MaActionResult;
import com.spinytech.macore.base.IAction;
import com.spinytech.macore.exception.HandlerException;
import com.spinytech.macore.exception.InitException;
import com.spinytech.macore.helper.WideRouterHelper;
import com.spinytech.macore.service.LocalRouterConnectService;
import com.spinytech.macore.tools.Logger;
import com.spinytech.macore.tools.ProcessUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地路由
 * The Local Router
 * <p>
 * TODO AOP 注解
 */
@SuppressWarnings("StaticFieldLeak")
public final class LocalRouter {
    private static final String TAG = "LocalRouter";

    private String mProcessName = ProcessUtil.UNKNOWN_PROCESS_NAME;
    private static LocalRouter sInstance;
    //本地路由表
    private HashMap<String, GroupMate> mGroupMates = null;
    private Application mApplication;
    private WideRouterHelper mWideRouterHelper;
    private static ExecutorService threadPool = null;
    private ErrorAction defaultNotFoundAction = new ErrorAction(false, MaActionResult
            .CODE_NOT_FOUND,
            "Not found the action.");

    private LocalRouter(Application mApplication) {
        this.mApplication = mApplication;
        mProcessName = ProcessUtil.getProcessName(mApplication);
        mGroupMates = new LinkedHashMap<>();
    }


    public static LocalRouter getInstance() {
        if (sInstance == null) {
            throw new InitException("Not init");
        }
        return sInstance;
    }

    public static synchronized LocalRouter init(@NonNull Application application) {
        if (sInstance == null) {
            sInstance = new LocalRouter(application);
        }
        return sInstance;
    }

    public static synchronized void openDebug() {
        Logger.LOG_LEVEL = Log.ASSERT;

    }

    /**
     * 设置远程服务
     *
     * @param wideRouterHelper
     */
    public void setWideRouter(WideRouterHelper wideRouterHelper) {
        mWideRouterHelper = wideRouterHelper;
        mWideRouterHelper.init(mApplication, mProcessName);
    }

    /**
     * 获取线程池
     *
     * @return
     */
    private static synchronized ExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }


    @SuppressWarnings("ConstantConditions")
    public boolean answerWiderAsync(@NonNull RouterRequest routerRequest) {
        return !(mProcessName.equals(routerRequest.getDomain()) && mWideRouterHelper != null
                && mWideRouterHelper.checkWideRouterConnection())
                || findRequestAction(routerRequest).isAsync(mApplication, routerRequest.getData());
    }

    public void registerProvider(String path, IAction action) {
        String group = extractGroup(path);
        GroupMate groupMate = mGroupMates.get(group);
        if (groupMate == null) {
            groupMate = new GroupMate();
            mGroupMates.put(group, groupMate);
        }
        groupMate.registerAction(path, action);
    }


    /**
     * Extract the default group from path.
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new HandlerException(
                    "Extract the default group failed, the path must be start with '/' and "
                            + "contain more than 2 '/'!");
        }

        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new HandlerException(
                        "Extract the default group failed! There's nothing between 2 '/'!");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            Logger.e(TAG, "Failed to extract default group! " + e.getMessage());
            return null;
        }
    }

    public RouterResponse route(Context context, @NonNull RouterRequest routerRequest) {
        timeLog("Local route start");
        // Local request,判断包名是否相同,是不是本进程内请求
        if (mProcessName.equals(routerRequest.getDomain()))
            return loadLocal(context, routerRequest);

        //如果不是本地进程名,还设置不是多进程
        if (mWideRouterHelper == null) {
            throw new HandlerException(
                    "Please make sure the returned value of needMultipleProcess in "
                            + "MaApplication is true, so that you can invoke other process action"
                            + "(未开启多进程,或进程名设置错误).");
        }
        // IPC request,远程
        try {
            return mWideRouterHelper.loadWide(routerRequest);
        } catch (RemoteException e) {
            Logger.e(TAG, "Not Load Wide Router " + e.getMessage());
        }
        return null;

    }


    /**
     * 本地路由跳转
     *
     * @param context
     * @param routerRequest
     * @return
     */
    private RouterResponse loadLocal(Context context, @NonNull RouterRequest routerRequest) {
        RouterResponse routerResponse = new RouterResponse();
        //是否传递Object
        Object attachment = routerRequest.getAndClearObject();
        //字符数据
        HashMap<String, String> params = routerRequest.getData();
        timeLog("Local find action start");
        //获取到动作实例
        IAction targetAction = findRequestAction(routerRequest);
        routerRequest.unIdle();
        if (targetAction == null) {
            routerResponse.mResultString = defaultNotFoundAction.invoke(context, params,
                    attachment).toString();
            return routerResponse;
        }
        timeLog("Local find action end");
        routerResponse.mIsAsync = attachment == null ? targetAction.isAsync(context, params)
                : targetAction.isAsync(context, params, attachment);
        //判断是不是异步
        // Sync result, return the result immediately.
        if (!routerResponse.mIsAsync) {
            //非异步方法
            MaActionResult result = attachment == null ? targetAction.invoke(context, params)
                    : targetAction.invoke(context, params, attachment);
            routerResponse.mResultString = result.toString();
            routerResponse.mObject = result.getObject();
            return routerResponse;
        }
        timeLog("Local sync start");
        // Async result, use the thread pool to execute the task.
        //异步执行制定操作
        LocalTask task = new LocalTask(routerResponse, params, attachment, context,
                targetAction);
        routerResponse.mAsyncResponse = getThreadPool().submit(task);
        timeLog("Local sync end");
        return routerResponse;
    }

    private void timeLog(String msg) {
        Logger.d(TAG, "Process:" + mProcessName + "\n" + msg + "+: " + System
                .currentTimeMillis());
    }


    /**
     * 根据路由表,查找服务是否存在,及活动
     *
     * @param routerRequest
     * @return
     */
    private IAction findRequestAction(RouterRequest routerRequest) {
        GroupMate targetProvider = mGroupMates.get(extractGroup(routerRequest.getPath()));
        if (null == targetProvider) return null;
        IAction targetAction = targetProvider.findAction(routerRequest.getPath());
        if (null == targetAction) return null;
        return targetAction;
    }

    public void disconnectWideRouter() {
        if (mWideRouterHelper == null) return;
        mWideRouterHelper.disconnectWideRouter();
    }

    public void connectWideRouter() {
        if (mWideRouterHelper == null) return;
        mWideRouterHelper.connectWideRouter();
    }

    public boolean stopSelf(Class<? extends LocalRouterConnectService> clazz) {
        if (mWideRouterHelper == null) return true;
        return mWideRouterHelper.stopSelf(clazz);
    }

    public RouterResponse route(RouterRequest routerRequest) {
        RouterResponse route = null;
        try {
            route = route(mApplication, routerRequest);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }
        return route;
    }

    public void stopWideRouter() {
        if (mWideRouterHelper != null) {
            mWideRouterHelper.stopWideRouter();
        }
    }

    /**
     * 异步执行指定动作
     */
    private class LocalTask implements Callable<String> {
        private RouterResponse mResponse;
        private HashMap<String, String> mRequestData;
        private Context mContext;
        private IAction mAction;
        private Object mObject;

        LocalTask(RouterResponse routerResponse, HashMap<String, String> requestData, Object
                object, Context context, IAction maAction) {
            this.mContext = context;
            this.mResponse = routerResponse;
            this.mRequestData = requestData;
            this.mAction = maAction;
            this.mObject = object;
        }

        @Override
        public String call() throws Exception {
            MaActionResult result = mObject == null ? mAction.invoke(mContext, mRequestData)
                    : mAction.invoke(mContext, mRequestData, mObject);
            mResponse.mObject = result.getObject();
            timeLog("Local async end");
            return result.toString();
        }
    }


}
