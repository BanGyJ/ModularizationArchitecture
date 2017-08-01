package com.spinytech.macore.base;

/**
 * @author gs
 * @version v1.0.0
 * @title ${TIT}
 * @descp ${DES}
 * @date 2017/4/7
 * @company 中鸿互联企业服务股份有限公司
 */
public interface InitializeRouter {

    /**
     * 注册远程服务
     */
    void initializeAllProcessRouter();

    /**
     * 注册分进程初始化
     */
    void initializeLogic();

    /**
     * 确认是否开启多进程
     *
     * @return
     */
    boolean needMultipleProcess();

}
