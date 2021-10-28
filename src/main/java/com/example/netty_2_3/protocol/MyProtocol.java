package com.example.netty_2_3.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: MyProtocol
 * <p>
 * Description:自定义通信协议：前4个字节放int，用来描述数据长度（字节）
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/28/2021 21:07
 * <p>
 * History:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyProtocol {

    private int length;

    private byte[] data;
}
