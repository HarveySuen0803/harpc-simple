package com.harvey.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Harvey Suen
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RpcRequestMessage extends Message {
    private String interfaceName;
    
    private String methodName;
    
    private Class<?> returnType;
    
    private Class<?>[] parameterTypes;
    
    private Object[] parameterValue;
    
    public RpcRequestMessage(int sequenceId, String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
    
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
