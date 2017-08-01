package com.spinytech.api;

import static com.spinytech.api.RouterApi.Group.MAIN;
import static com.spinytech.api.RouterApi.Group.MUSIC;
import static com.spinytech.api.RouterApi.Group.PIC;
import static com.spinytech.api.RouterApi.Group.WEB;

/**
 * @author gs
 * @version v0.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 2017/8/1
 * @company 中鸿互联企业服务股份有限公司
 **/
public final class RouterApi {

    private RouterApi() {
    }

     static final class Group {
         static final String MAIN = "/main";
         static final String MUSIC = "/music";
         static final String PIC = "/pic";
         static final String WEB = "/web";
    }

    public static final class Action {
        public static final String MAIN_SYNC = MAIN + "/sync";
        public static final String MAIN_ATTACHMENT = MAIN + "/attachment";
        public static final String MAIN_ASYNC = MAIN + "/async";


        public static final String MUSIC_PLAY = MUSIC + "/play";
        public static final String MUSIC_STOP = MUSIC + "/stop";
        public static final String MUSIC_SHUTDOWN = MUSIC + "/shutdown";


        public static final String PIC_PIC = PIC + "/pic";

        public static final String WEB_WEB = WEB + "/web";
    }


}
