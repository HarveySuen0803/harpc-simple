package com.harvey.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Harvey Suen
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    
    private Object returnValue;
    
    private Exception exceptionValue;
    
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
