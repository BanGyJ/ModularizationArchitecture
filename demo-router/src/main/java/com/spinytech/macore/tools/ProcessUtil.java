package com.spinytech.macore.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by wanglei on 2016/11/25.
 *  * 改:修改获取进程方法
 */
public class ProcessUtil {

    private ProcessUtil() {
    }

    public static final String UNKNOWN_PROCESS_NAME = "unknown_process_name";

    public static int getMyProcessId() {
        return android.os.Process.myPid();
    }

    @SuppressWarnings({"ReturnInsideFinallyBlock", "PMD", "FindBugs"})
    public static String getProcessName(int pid) {
        String processName = UNKNOWN_PROCESS_NAME;
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            Logger.e("PriorityLogicUtils", e.getMessage());
        } finally {
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        }
        return UNKNOWN_PROCESS_NAME;
    }

    @SuppressWarnings({"ReturnInsideFinallyBlock", "PMD", "FindBugs"})
    public static String getProcessName() {
        return getProcessName(getMyProcessId());
    }

    public static String getProcessName(Context context) {
        String processName = getProcessName();
        if (UNKNOWN_PROCESS_NAME.equals(processName)) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context
                    .ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return UNKNOWN_PROCESS_NAME;
            }
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == getMyProcessId()) {
                    return procInfo.processName;
                }
            }
        } else {
            return processName;
        }
        return UNKNOWN_PROCESS_NAME;
    }

}
