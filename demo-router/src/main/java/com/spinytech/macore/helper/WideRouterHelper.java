package com.spinytech.macore.helper;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.spinytech.macore.IWideRouterAIDL;
import com.spinytech.macore.action.ErrorAction;
import com.spinytech.macore.action.MaActionResult;
import com.spinytech.macore.router.RouterRequest;
import com.spinytech.macore.router.RouterResponse;
import com.spinytech.macore.router.WideRouter;
import com.spinytech.macore.service.LocalRouterConnectService;
import com.spinytech.macore.service.WideRouterConnectService;
import com.spinytech.macore.tools.Logger;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @author gs
 * @version v1.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 17-4-7
 * @company 中鸿互联企业服务股份有限公司
 */
public final class WideRouterHelper {

    private String mProcessName;

    private Application mApplication;
    private IWideRouterAIDL mWideRouterAIDL;
    private static ExecutorService threadPool = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWideRouterAIDL = IWideRouterAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWideRouterAIDL = null;
        }
    };

    public WideRouterHelper() {

    }


    public void init(Application application, String processName) {
        mApplication = application;
        mProcessName = processName;
    }

    /**
     * 开启广域服务
     */
    public void connectWideRouter() {
        connectWideRouter(mProcessName);
    }

    /**
     * 开启广域服务
     */
    public void connectWideRouter(String processName) {
        Intent binderIntent = new Intent(mApplication, WideRouterConnectService.class);
        binderIntent.putExtra("domain", processName);
        mApplication.bindService(binderIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void disconnectWideRouter() {
        if (null == mServiceConnection) {
            return;
        }
        mApplication.unbindService(mServiceConnection);
        mWideRouterAIDL = null;
    }


    public boolean checkWideRouterConnection() {
        return mWideRouterAIDL != null;
    }

    public boolean stopSelf(Class<? extends LocalRouterConnectService> clazz) {
        if (checkWideRouterConnection()) {
            try {
                return mWideRouterAIDL.stopRouter(mProcessName);
            } catch (RemoteException e) {
                Logger.e("WideRouterHelper", e.getMessage());
                return false;
            }
        } else {
            mApplication.stopService(new Intent(mApplication, clazz));
            return true;
        }
    }

    public void stopWideRouter() {
        if (checkWideRouterConnection()) {
            try {
                mWideRouterAIDL.stopRouter(WideRouter.PROCESS_NAME);
            } catch (RemoteException e) {
                Logger.e("WideRouterHelper", e.getMessage());
            }
        } else {
            Logger.e(TAG, "This local router hasn't connected the wide router.");
        }
    }


    public RouterResponse loadWide(@NonNull RouterRequest routerRequest)
            throws RemoteException {
        RouterResponse routerResponse = new RouterResponse();

        String domain = routerRequest.getDomain();
        String routerRequestString = routerRequest.toJson();
        //如果AIDL链接已经建立过
        if (checkWideRouterConnection()) {
            timeLog("Wide async check start");
            //If you don't need wide async check, use "routerResponse.mIsAsync = false;"
            // replace the next line to improve performance.
            //检查是不是异步操作
            routerResponse.mIsAsync = mWideRouterAIDL.checkResponseAsync(domain,
                    routerRequestString);
            //不是异步
            if (!routerResponse.mIsAsync) {
                routerResponse.mResultString = mWideRouterAIDL.route(domain, routerRequestString);
                timeLog("Wide async end");
                return routerResponse;
            }
            //异步操作执行
            // Async result, use the thread pool to execute the task.

            WideTask task = new WideTask(domain, routerRequestString);
            routerResponse.mAsyncResponse = getThreadPool().submit(task);
            timeLog("Wide async check end");
            return routerResponse;
        }

        // Has not connected with the wide router.
        //如果没有建立过远程链接,建立链接并执行操作
        routerResponse.mIsAsync = true;
        ConnectWideTask task = new ConnectWideTask(routerResponse, domain,
                routerRequestString);
        routerResponse.mAsyncResponse = getThreadPool().submit(task);

        return routerResponse;
    }

    private void timeLog(String msg) {
        Logger.d(TAG, "Process:" + mProcessName + "\n" + msg + "+: "
                + System.currentTimeMillis());
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

    /**
     * 远程异步操作
     */
    private final class WideTask implements Callable<String> {

        private String mDomain;
        private String mRequestString;

        private WideTask(String domain, String requestString) {
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            timeLog("Wide async start");
            String result = mWideRouterAIDL.route(mDomain, mRequestString);
            Logger.e(TAG, result);
            timeLog("Wide async end");
            return result;
        }
    }

    /**
     * 创建并链接远程的AIDL服务
     */
    private final class ConnectWideTask implements Callable<String> {
        private RouterResponse mResponse;
        private String mDomain;
        private String mRequestString;

        private ConnectWideTask(RouterResponse routerResponse, String domain,
                                String requestString) {
            this.mResponse = routerResponse;
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            timeLog("Bind wide router start");
            connectWideRouter();
            int time = 0;
            while (true) {
                if (null == mWideRouterAIDL) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Logger.e("WideRouterHelper", e.getMessage());
                    }
                    time++;
                } else {
                    break;
                }
                if (time >= 600) {
                    ErrorAction defaultNotFoundAction = new ErrorAction(true, MaActionResult
                            .CODE_CANNOT_BIND_WIDE, "Bind wide router time out. Can not bind wide"
                            + " "
                            + "router.");
                    MaActionResult result = defaultNotFoundAction.invoke(mApplication, new
                            HashMap<String, String>());
                    mResponse.mResultString = result.toString();
                    return result.toString();
                }
            }

            timeLog("Bind wide router end");
            String result = mWideRouterAIDL.route(mDomain, mRequestString);
            timeLog("Bind wide async end");

            return result;
        }
    }
}
