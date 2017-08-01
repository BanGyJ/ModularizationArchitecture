package com.spinytech.picdemo;

import com.spinytech.api.RouterApi;
import com.spinytech.macore.implement.BaseApplicationLogic;

/**
 * Created by wanglei on 2017/1/4.
 */

public class PicApplicationLogic extends BaseApplicationLogic {

    @Override
    public void onCreate() {
        super.onCreate();
        registerAction(RouterApi.Action.PIC_PIC,new PicAction());
        //LocalRouter.getInstance(mApplication).registerProvider("pic",new PicProvider());
    }
}
