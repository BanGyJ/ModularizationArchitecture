package com.spinytech.macore.router;

import android.content.Context;
import android.text.TextUtils;

import com.spinytech.macore.tools.Logger;
import com.spinytech.macore.tools.ProcessUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 路由的请求参数bean
 * +对象池
 * +序列化
 * +URL解析
 * Created by wanglei on 2016/12/27.
 */
@SuppressWarnings("deprecation")
public final class RouterRequest {
    private static final String TAG = "RouterRequest";
    //默认进程名称,为当前进程名
    private static volatile String defaultProcess = ProcessUtil.getProcessName();
    //本次来自
    private String from;
    //本次toDo
    private String domain;
    //本次服务名
    private String group;
    //本次动作名
    private volatile String action;
    //本次动作URI
    private String path;
    //本次数据
    private HashMap<String, String> data;
    //本次对象
    private Object object;
    //线程安全的布尔参数,判断是否空闲被占用
    private AtomicBoolean isIdle = new AtomicBoolean(true);
    //线程安全的自增参数
    private static AtomicInteger sIndex = new AtomicInteger(0);
    //默认重置次数
    private static final int RESET_NUM = 1000;
    //默认请求池个数
    private static final int LENGTH = 64;
    //默认请求池
    private static volatile RouterRequest[] table = new RouterRequest[LENGTH];

    static {
        //初始化请求池
        for (int i = 0; i < LENGTH; i++) {
            table[i] = new RouterRequest();
        }
    }

    private RouterRequest() {
        this.from = defaultProcess;
        this.domain = defaultProcess;
        this.group = "";
        this.action = "";
        this.data = new HashMap<>();
    }


    private RouterRequest(Context context) {
        this.domain = getProcess(context);
        this.from = domain;
        this.action = "";
        this.group = action;
        this.data = new HashMap<>();
    }


    public String getFrom() {
        return from;
    }

    public String getDomain() {
        return domain;
    }


    public String getPath() {
        return TextUtils.isEmpty(path) ? "/" + group + "/" + action : path;
    }

    public String getGroup() {
        // String temp = group;
        //group = "";
        return group;
    }

    public String getAction() {
        //String temp = action;
        //action = "";
        return action;
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> temp = new HashMap<>();
        temp.putAll(data);
        data.clear();
        return temp;
    }

    public Object getAndClearObject() {
        Object temp = object;
        object = null;
        return temp;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    private static String getProcess(Context context) {
        if (TextUtils.isEmpty(defaultProcess) || ProcessUtil.UNKNOWN_PROCESS_NAME
                .equals(defaultProcess)) {
            defaultProcess = ProcessUtil.getProcessName(context);
        }
        return defaultProcess;
    }


    /**
     * 把请求参数序列化成字符串
     *
     * @return
     */
    @SuppressWarnings("PMD")
    public String toJson() {
        //Here remove Gson to save about 10ms.
        //String result = new Gson().toJson(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", getFrom());
            jsonObject.put("domain", getDomain());
            jsonObject.put("group", getGroup());
            jsonObject.put("action", getAction());
            jsonObject.put("path", getPath());

            try {
                JSONObject jsonData = new JSONObject();
                for (Map.Entry<String, String> entry : getData().entrySet()) {
                    jsonData.put(entry.getKey(), entry.getValue());
                }
                jsonObject.put("data", jsonData);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                jsonObject.put("data", "{}");
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
        unIdle();
        return jsonObject.toString();
    }


    /**
     * 反序列化成Bean
     *
     * @param requestJsonString
     * @return
     */
    public RouterRequest json(String requestJsonString) {
        //Here remove Gson to save about 10ms.
        //RouterRequest routerRequest = new Gson().fromJson(requestJsonString, RouterRequest.class);
        try {
            JSONObject jsonObject = new JSONObject(requestJsonString);
            this.from = jsonObject.getString("from");
            this.domain = jsonObject.getString("domain");
            this.group = jsonObject.getString("group");
            this.action = jsonObject.getString("action");
            this.path = jsonObject.getString("path");
            try {
                JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
                Iterator it = jsonData.keys();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    String value = (String) jsonData.get(key);
                    this.data.put(key, value);
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                this.data = new HashMap<>();
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
        return this;
    }

    /**
     * 解析Url中的请求参数
     *
     * @param url
     * @return
     */
    @SuppressWarnings("PMD")
    public RouterRequest url(String url) {
        int questIndex = url.indexOf('?');
        String[] urls = url.split("\\?");
        if (urls.length != 1 && urls.length != 2) {
            Logger.e(TAG, "The url is illegal.");
            return this;
        }
        String[] targets = urls[0].split("/");
        if (targets.length == 3) {
            this.domain = targets[0];
            this.group = targets[1];
            this.action = targets[2];
        } else {
            Logger.e(TAG, "The url is illegal.");
            return this;
        }
        //Add params
        if (questIndex != -1) {
            String queryString = urls[1];
            if (queryString != null && queryString.length() > 0) {
                int ampersandIndex, lastAmpersandIndex = 0;
                String subStr, key, value;
                String[] paramPair, values, newValues;
                do {
                    ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                    if (ampersandIndex > 0) {
                        subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                        lastAmpersandIndex = ampersandIndex;
                    } else {
                        subStr = queryString.substring(lastAmpersandIndex);
                    }
                    paramPair = subStr.split("=");
                    key = paramPair[0];
                    value = paramPair.length == 1 ? "" : paramPair[1];
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Logger.e(TAG, e.getMessage());
                    }
                    data.put(key, value);
                } while (ampersandIndex > 0);
            }
        }
        return this;
    }

    public RouterRequest domain(String domain) {
        this.domain = domain;
        return this;
    }


    public RouterRequest group(String group) {
        this.group = group;
        return this;
    }


    public RouterRequest action(String action) {
        this.action = action;
        return this;
    }

    public RouterRequest path(String path) {
        this.path = path;
        return this;
    }


    public RouterRequest data(String key, String data) {
        this.data.put(key, data);
        return this;
    }

    public RouterRequest object(Object object) {
        this.object = object;
        return this;
    }

    /**
     * 获取回收的请求对象
     *
     * @param context
     * @return
     */
    public static RouterRequest obtain(Context context) {
        return obtain(context, 0);
    }

    /**
     * 从回收池中获取对象
     *
     * @param context   上下文
     * @param retryTime 重新获取次数
     * @return 如果存在从回收池中获取, 如果不存在重写创建一个
     */
    private static RouterRequest obtain(Context context, int retryTime) {
        int index = sIndex.getAndIncrement();
        if (index > RESET_NUM) {
            sIndex.compareAndSet(index, 0);
            if (index > RESET_NUM * 2) {
                sIndex.set(0);
            }
        }
        int num = index & (LENGTH - 1);
        RouterRequest target = table[num];
        //判断是否空闲,并标记为非空闲
        if (target.isIdle.compareAndSet(true, false)) {
            target.domain = getProcess(context);
            target.domain = target.from;
            target.action = "";
            target.group = "";
            target.path = "";
            return target;
        }
        //递归查找回收池
        return retryTime < 5 ? obtain(context, ++retryTime) : new RouterRequest(context);
    }

    void unIdle() {
        this.isIdle.compareAndSet(false, true);
    }


    /*添加最终提交，便于封装*/
    public RouterResponse routed() {
        return LocalRouter.getInstance().route(this);
    }

    /*添加最终提交，便于封装*/
    public RouterResponse routed(Context context) {
        return LocalRouter.getInstance().route(context, this);
    }


}
