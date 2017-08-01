package com.spinytech.webdemo;

import com.spinytech.api.RouterApi;
import com.spinytech.macore.implement.BaseApplicationLogic;

/**
 * Created by wanglei on 2017/1/4.
 */

public class WebApplicationLogic extends BaseApplicationLogic {

    @Override
    public void onCreate() {
        super.onCreate();
        registerAction(RouterApi.Action.WEB_WEB, new WebAction());
        //LocalRouter.getInstance(mApplication).registerProvider("web",new WebProvider());
    }
}
