package com.spinytech.macore.implement;

import com.spinytech.macore.router.WideRouter;
import com.spinytech.macore.tools.PriorityLogicUtils;

/**
 * 初始化广域路由
 * Created by wanglei on 2016/11/25.
 */
public final class WideRouterApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        initRouter();
    }

    protected void initRouter() {
        WideRouter.getInstance(mApplication);
        PriorityLogicUtils.getInstance().initializeAllProcessRouter();
    }
}
