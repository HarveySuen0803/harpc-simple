package com.harvey.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Harvey Suen
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger(1);
    
    public static int getNextId() {
        return id.incrementAndGet();
    }
}
