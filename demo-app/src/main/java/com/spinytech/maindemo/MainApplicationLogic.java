package com.spinytech.maindemo;

import com.spinytech.api.RouterApi;
import com.spinytech.macore.implement.BaseApplicationLogic;

/**
 * Created by wanglei on 2016/11/29.
 */

public class MainApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        registerAction(RouterApi.Action.MAIN_SYNC, new SyncAction());
        registerAction(RouterApi.Action.MAIN_ASYNC, new AsyncAction());
        registerAction(RouterApi.Action.MAIN_ATTACHMENT, new AttachObjectAction());
    }
}
