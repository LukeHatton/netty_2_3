package com.example.netty_2_3.util;

/**
 * ClassName: HessianSerializerSingleton
 * <p>
 * Description:{@link HessianSerializer}单例枚举
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/30/2021 20:13
 * <p>
 * History:
 */
public enum HessianSerializerSingleton {

    INSTANCE;

    private final HessianSerializer hessianSerializer = new HessianSerializer();

    public HessianSerializer getInstance(){
        return hessianSerializer;
    }
}
