package com.harvey.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Harvey Suen
 */
public class CommonConfig {
    static Properties properties;
    
    static {
        try (InputStream in = CommonConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static int getSerializerType() {
        String type = properties.getProperty("serializer.type");
        
        if ("java".equals(type)) {
            return 1;
        } else if ("json".equals(type)) {
            return 2;
        } else if ("hessian".equals(type)) {
            return 3;
        }else {
            return 1;
        }
    }
}
