package com.spinytech.macore.router;

import com.spinytech.macore.service.LocalRouterConnectService;

/**
 * Created by wanglei on 2016/11/30.
 */

public class ConnectServiceWrapper {
    public Class<? extends LocalRouterConnectService> targetClass = null;

    public ConnectServiceWrapper( Class<? extends LocalRouterConnectService> logicClass) {
        this.targetClass = logicClass;
    }
}
