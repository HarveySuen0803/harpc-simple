package com.harvey.server.handler;

import com.harvey.common.model.RpcRequestMessage;
import com.harvey.common.model.RpcResponseMessage;
import com.harvey.server.service.HelloService;
import com.harvey.server.service.ServiceFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.netty.channel.ChannelHandler.Sharable;

@Slf4j
@Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage reqMsg) {
        RpcResponseMessage repMsg = new RpcResponseMessage();
        repMsg.setSequenceId(reqMsg.getSequenceId());
        try {
            HelloService helloService = (HelloService) ServiceFactory.getService(Class.forName(reqMsg.getInterfaceName()));
            Method method = helloService.getClass().getMethod(reqMsg.getMethodName(), reqMsg.getParameterTypes());
            Object result = method.invoke(helloService, reqMsg.getParameterValue());
            repMsg.setReturnValue(result);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            
            // e 的错误信息内容非常多, 如果直接将 e 作为 ExceptionValue 返回给 Client,
            // 会超出 MessageCodec 的 LengthFieldBasedFrameDecoder 限定的最大长度
            repMsg.setExceptionValue(new Exception("RpcRequestMessage Handle Exception, " + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(repMsg);
    }
    
    /**
     * 模拟 channelRead0 的调用, 接收到 RpcRequestMessage 进行处理
     */
    public void test01() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage reqMsg = new RpcRequestMessage(
            1,
            "com.harvey.server.service.HelloService",
            "sayHello",
            String.class,
            new Class[]{String.class},
            new Object[]{"harvey"}
        );
        
        HelloService helloService = (HelloService) ServiceFactory.getService(Class.forName(reqMsg.getInterfaceName()));
        Method method = helloService.getClass().getMethod(reqMsg.getMethodName(), reqMsg.getParameterTypes());
        Object result = method.invoke(helloService, reqMsg.getParameterValue());
        
        System.out.println(result);
    }
}