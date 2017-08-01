package com.spinytech.musicdemo;

import com.spinytech.api.RouterApi;
import com.spinytech.macore.implement.BaseApplicationLogic;

/**
 * Created by wanglei on 2016/11/30.
 */

public class MusicApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        registerAction(RouterApi.Action.MUSIC_PLAY, new PlayAction());
        registerAction(RouterApi.Action.MUSIC_STOP, new StopAction());
        registerAction(RouterApi.Action.MUSIC_SHUTDOWN, new ShutdownAction());
    }
}
