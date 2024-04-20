package com.harvey.client.handler;

import com.harvey.common.model.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.channel.ChannelHandler.*;

@Slf4j
@Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static final Map<Integer, Promise<Object>> promiseMap = new ConcurrentHashMap<>();
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        int sequenceId = msg.getSequenceId();
        Promise<Object> promise = promiseMap.remove(sequenceId);
        if (promise == null) {
            return;
        }
        
        Exception exceptionValue = msg.getExceptionValue();
        if (exceptionValue != null) {
            promise.setFailure(exceptionValue);
            return;
        }
        
        Object returnValue = msg.getReturnValue();
        promise.setSuccess(returnValue);
    }
}
