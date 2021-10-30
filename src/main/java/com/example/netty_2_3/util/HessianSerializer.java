package com.example.netty_2_3.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * ClassName: HessianSerializer
 * <p>
 * Description: Hessian序列化工具
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/29/2021 21:27
 * <p>
 * History:
 */
public class HessianSerializer {

    /* 不要使用静态方法:因为静态方法属于类,和类在内存中一起分配,无法被JVM垃圾回收及进行内存回收 */
    @SneakyThrows
    public <T> byte[] serialize(T obj) {
        @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
        @Cleanup HessianOutput ho = new HessianOutput(os);
        ho.writeObject(obj);
        ho.flush();
        return os.toByteArray();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        @Cleanup ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        @Cleanup HessianInput hi = new HessianInput(is);
        return (T) hi.readObject(clazz);
    }
}
