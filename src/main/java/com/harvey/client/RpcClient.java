package com.harvey.client;

import com.harvey.client.handler.RpcResponseMessageHandler;
import com.harvey.common.model.RpcRequestMessage;
import com.harvey.common.protocol.MessageCodecSharable;
import com.harvey.common.protocol.ProtocolFrameDecoder;
import com.harvey.common.utils.SequenceIdGenerator;
import com.harvey.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @author Harvey Suen
 */
@Slf4j
public class RpcClient {
    private static Channel channel;
    
    private static final Object lock = new Object();
    
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }
    
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageCodec);
                ch.pipeline().addLast(rpcResponseMessageHandler);
            }
        });
        
        try {
            // 这里可以是同步堵塞等待连接
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            
            // 这里不可以是同步堵塞等待关闭, 因为是初始化 Channel 的操作, 别人还没拿到 Channel 呢, 就无法执行 channel.close()
            channel.closeFuture().addListener(future -> {
                if (future.isSuccess()) {
                    group.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
    
    private static <T> T getProxyService(Class<?> cls) {
        return (T) Proxy.newProxyInstance(
            cls.getClassLoader(),
            new Class[]{cls},
            (proxy, method, args) -> {
                int sequenceId = SequenceIdGenerator.getNextId();
                
                RpcRequestMessage reqMsg = new RpcRequestMessage(
                    sequenceId,
                    cls.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
                );
                
                getChannel().writeAndFlush(reqMsg);
                
                Promise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
                RpcResponseMessageHandler.promiseMap.put(sequenceId, promise);
                
                promise.await();
                if (promise.isSuccess()) {
                    return promise.getNow();
                } else {
                    throw new RuntimeException(promise.cause());
                }
            }
        );
    }
    
    public static void main(String[] args) {
        HelloService helloService = getProxyService(HelloService.class);
        System.out.println(helloService.sayHello("harvey"));
        System.out.println(helloService.sayHello("bruce"));
        System.out.println(helloService.sayHello("jack"));
    }
}
