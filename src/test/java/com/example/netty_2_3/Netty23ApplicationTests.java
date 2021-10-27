package com.example.netty_2_3;

import com.example.netty_2_3.client.MyRPCClient;
import com.example.netty_2_3.server.MyRPCServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootTest
class Netty23ApplicationTests {

    private final int port = 6996;

    @Test
    void contextLoads() {
    }

    /**
     * 测试：netty服务端启动
     */
    @Test
    public void testNettyServerStart() {
        MyRPCServer server = new MyRPCServer();
        server.start(port);
    }

    /**
     * 测试：netty客户端启动
     */
    @Test
    public void testNettyClientStart() throws UnknownHostException {
        MyRPCClient client = new MyRPCClient();
        client.start(InetAddress.getLocalHost().getHostAddress(), port);
    }

}
