package com.spinytech.musicdemo;

import android.content.Context;
import android.content.Intent;

import com.spinytech.macore.action.MaAction;
import com.spinytech.macore.action.MaActionResult;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.tools.Logger;

import java.util.HashMap;

/**
 * Created by wanglei on 2016/12/28.
 */

public class ShutdownAction extends MaAction {

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return true;
    }

    @Override
    public MaActionResult invoke(Context context, HashMap<String, String> requestData) {
        MaActionResult result = new MaActionResult.Builder()
                .code(MaActionResult.CODE_SUCCESS)
                .msg("success")
                .data("")
                .object(null)
                .build();
        context.getApplicationContext().stopService(new Intent(context, MusicService.class));

        boolean stopslef = LocalRouter.getInstance().stopSelf(MusicRouterConnectService.class);
        Logger.e("stopslef", "" + stopslef);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
        return result;
    }
}
