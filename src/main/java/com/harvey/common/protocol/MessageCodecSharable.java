package com.harvey.common.protocol;

import com.harvey.common.config.CommonConfig;
import com.harvey.common.model.Message;
import com.harvey.common.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.netty.channel.ChannelHandler.*;

/**
 * @author Harvey Suen
 */
@Slf4j
@Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(new byte[]{'C', 'A', 'F', 'E'});
        out.writeByte(1);
        out.writeByte(CommonConfig.getSerializerType());
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = SerializerFactory.getSerializer(CommonConfig.getSerializerType()).serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> msgList) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Message msg = SerializerFactory.getSerializer(serializerType).deserialize(bytes, Message.getMessageClass(messageType));
        
        log.debug("Decoded msg info: {}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("Decoded msg: {}", msg);
        
        msgList.add(msg);
    }
}
