package com.spinytech.macore.router;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.spinytech.macore.ILocalRouterAIDL;
import com.spinytech.macore.action.MaActionResult;
import com.spinytech.macore.service.LocalRouterConnectService;
import com.spinytech.macore.service.WideRouterConnectService;
import com.spinytech.macore.tools.Logger;
import com.spinytech.macore.tools.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 广域路由
 * Created by wanglei on 2016/11/29.
 */
@SuppressWarnings("StaticFieldLeak")
public final class WideRouter {
    private static final String TAG = "WideRouter";
    public static final String PROCESS_NAME = ProcessUtil.getProcessName();
    private static HashMap<String, ConnectServiceWrapper> sLocalRouterClasses;
    private static WideRouter sInstance = null;
    private Application mApplication;
    private HashMap<String, ServiceConnection> mLocalRouterConnectionMap;
    private HashMap<String, ILocalRouterAIDL> mLocalRouterAIDLMap;
    public boolean mIsStopping = false;

    private WideRouter(Application context) {
        mApplication = context;
        String checkProcessName = ProcessUtil.getProcessName(context);
        if (!PROCESS_NAME.equals(checkProcessName)) {
            throw new RuntimeException("You should not initialize the WideRouter in process:"
                    + checkProcessName);
        }
        sLocalRouterClasses = new HashMap<>();
        mLocalRouterConnectionMap = new HashMap<>();
        mLocalRouterAIDLMap = new HashMap<>();
    }

    public static synchronized WideRouter getInstance(@NonNull Application context) {
        if (sInstance == null) {
            sInstance = new WideRouter(context);
        }
        return sInstance;
    }

    /**
     * 注册服务
     *
     * @param processName
     * @param targetClass
     */
    public static void registerLocalRouter(String processName, Class<? extends
            LocalRouterConnectService> targetClass) {
        if (null == sLocalRouterClasses) {
            sLocalRouterClasses = new HashMap<>();
        }
        ConnectServiceWrapper connectServiceWrapper = new ConnectServiceWrapper(targetClass);
        sLocalRouterClasses.put(processName, connectServiceWrapper);
    }

    /**
     * 检查有没有注册服务
     *
     * @param domain
     * @return
     */
    public boolean checkLocalRouterHasRegistered(final String domain) {
        ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
        return null != connectServiceWrapper && null != connectServiceWrapper.targetClass;
    }

    /**
     * 连接AIDL服务
     *
     * @param domain
     * @return
     */
    public boolean connectLocalRouter(final String domain) {
        if (!checkLocalRouterHasRegistered(domain)) return false;
        Class<? extends LocalRouterConnectService> clazz = sLocalRouterClasses.get(domain)
                .targetClass;
        Intent binderIntent = new Intent(mApplication, clazz);
        Bundle bundle = new Bundle();
        binderIntent.putExtras(bundle);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocalRouterAIDL mLocalRouterAIDL = ILocalRouterAIDL.Stub.asInterface(service);
                ILocalRouterAIDL temp = mLocalRouterAIDLMap.get(domain);
                if (null == temp) {
                    mLocalRouterAIDLMap.put(domain, mLocalRouterAIDL);
                    mLocalRouterConnectionMap.put(domain, this);
                    try {
                        mLocalRouterAIDL.connectWideRouter();
                    } catch (RemoteException e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocalRouterAIDLMap.remove(domain);
                mLocalRouterConnectionMap.remove(domain);
            }
        };
        mApplication.bindService(binderIntent, serviceConnection, BIND_AUTO_CREATE);
        return true;
    }

    /**
     * 断开连接
     *
     * @param domain
     * @return
     */
    public boolean disconnectLocalRouter(String domain) {
        if (TextUtils.isEmpty(domain)) {
            return false;
        } else if (PROCESS_NAME.equals(domain)) {
            stopSelf();
            return true;
        } else if (null == mLocalRouterConnectionMap.get(domain)) {
            return false;
        } else {
            ILocalRouterAIDL aidl = mLocalRouterAIDLMap.get(domain);
            if (null != aidl) {
                try {
                    aidl.stopWideRouter();
                } catch (RemoteException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
            mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
            mLocalRouterAIDLMap.remove(domain);
            mLocalRouterConnectionMap.remove(domain);
            return true;
        }
    }

    /**
     * 退出服务
     */
    void stopSelf() {
        mIsStopping = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> locals = new ArrayList<>();
                locals.addAll(mLocalRouterAIDLMap.keySet());
                for (String domain : locals) {
                    ILocalRouterAIDL aidl = mLocalRouterAIDLMap.get(domain);
                    if (null != aidl) {
                        try {
                            aidl.stopWideRouter();
                        } catch (RemoteException e) {
                            Logger.e(TAG, e.getMessage());
                        }
                        mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
                        mLocalRouterAIDLMap.remove(domain);
                        mLocalRouterConnectionMap.remove(domain);
                    }
                }
                try {
                    Thread.sleep(1000);
                    mApplication.stopService(new Intent(mApplication, WideRouterConnectService
                            .class));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.e(TAG, e.getMessage());
                }
                System.exit(0);
            }
        }).start();
    }

    /**
     * 检测是否链接远程
     *
     * @param domain
     * @param routerRequest
     * @return
     */
    public boolean answerLocalAsync(String domain, String routerRequest) {
        ILocalRouterAIDL target = mLocalRouterAIDLMap.get(domain);
        if (target == null) {
            return checkLocalRouterHasRegistered(domain);
        } else {
            try {
                return target.checkResponseAsync(routerRequest);
            } catch (RemoteException e) {
                Logger.e(TAG, e.getMessage());
                return true;
            }
        }
    }

    /**
     * 连接远程
     *
     * @param domain
     * @param routerRequest
     * @return
     */
    @SuppressWarnings("PMD")
    public RouterResponse route(String domain, String routerRequest) {
        Logger.d(TAG, "Process:" + PROCESS_NAME + "\nWide route start: " + System
                .currentTimeMillis());
        RouterResponse routerResponse = new RouterResponse();
        //检查服务是否关闭
        if (mIsStopping) {
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = new MaActionResult.Builder()
                    .code(MaActionResult.CODE_WIDE_STOPPING)
                    .msg("Wide router is stopping.")
                    .build().toString();
            return routerResponse;
        }
        //进程名不能与本地名相同
        if (PROCESS_NAME.equals(domain)) {
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = new MaActionResult.Builder()
                    .code(MaActionResult.CODE_TARGET_IS_WIDE)
                    .msg("Domain can not be " + PROCESS_NAME + ".")
                    .build().toString();
            return routerResponse;
        }
        ILocalRouterAIDL target = mLocalRouterAIDLMap.get(domain);
        if (null == target) {
            if (!connectLocalRouter(domain)) {
                //连接失败
                routerResponse.mIsAsync = false;
                routerResponse.mResultString = new MaActionResult.Builder()
                        .code(MaActionResult.CODE_ROUTER_NOT_REGISTER)
                        .msg("The " + domain + " has not registered.")
                        .build().toString();
                Logger.d(TAG, "Process:" + PROCESS_NAME + "\nLocal not register end: "
                        + System.currentTimeMillis());
                return routerResponse;
            }
            // Wait to bind the target process connect service, timeout is 30s.
            Logger.d(TAG, "Process:" + PROCESS_NAME + "\nBind local router start: "
                    + System.currentTimeMillis());
            int time = 0;
            //遍历查找服务
            while (true) {
                target = mLocalRouterAIDLMap.get(domain);
                if (null == target) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Logger.e(TAG, e.getMessage());
                    }
                    time++;
                } else {
                    Logger.d(TAG, "Process:" + PROCESS_NAME + "\nBind local router end: "
                            + System.currentTimeMillis());
                    break;
                }
                if (time >= 600) {
                    routerResponse.mResultString = new MaActionResult.Builder()
                            .code(MaActionResult.CODE_CANNOT_BIND_LOCAL)
                            .msg("Can not bind " + domain + ", time out.")
                            .build().toString();
                    return routerResponse;
                }
            }
        }
        try {
            Logger.d(TAG, "Process:" + PROCESS_NAME + "\nWide target start: "
                    + System.currentTimeMillis());
            routerResponse.mResultString = target.route(routerRequest);
            Logger.d(TAG, "Process:" + PROCESS_NAME + "\nWide route end: "
                    + System.currentTimeMillis());
        } catch (RemoteException e) {
            Logger.e(TAG, e.getMessage());
            routerResponse.mResultString = new MaActionResult.Builder()
                    .code(MaActionResult.CODE_REMOTE_EXCEPTION)
                    .msg(e.getMessage())
                    .build().toString();
            return routerResponse;
        }
        return routerResponse;
    }

}
