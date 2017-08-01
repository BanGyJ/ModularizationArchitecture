package com.spinytech.macore.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.spinytech.macore.ILocalRouterAIDL;
import com.spinytech.macore.action.MaActionResult;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.router.RouterRequest;
import com.spinytech.macore.router.RouterResponse;
import com.spinytech.macore.tools.Logger;


/**
 * 远程服务连接本地服务
 * Created by wanglei on 2016/11/29.
 */
@SuppressLint("Registered")
public class LocalRouterConnectService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.e("MRCS", "onBind");
        return stub;
    }

    ILocalRouterAIDL.Stub stub = new ILocalRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(String routerRequest) throws RemoteException {
            return LocalRouter.getInstance().
                    answerWiderAsync(RouterRequest
                            .obtain(getApplicationContext())
                            .json(routerRequest));
        }

        @Override
        public String route(String routerRequest) {
            try {
                LocalRouter localRouter = LocalRouter.getInstance();
                RouterRequest routerRequest1 = RouterRequest
                        .obtain(getApplicationContext())
                        .json(routerRequest);
                RouterResponse routerResponse = localRouter.route(LocalRouterConnectService.this,
                        routerRequest1);
                return routerResponse.get();
            } catch (Exception e) {
                Logger.e("LocalRouterConnectService", e);
                return new MaActionResult.Builder().msg(e.getMessage()).build().toString();
            }
        }

        @Override
        public boolean stopWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance()
                    .disconnectWideRouter();
            return true;
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance()
                    .connectWideRouter();
        }
    };
}
