package com.harvey.test;

import com.harvey.server.service.HelloService;
import com.harvey.server.service.HelloServiceImpl;

import java.lang.reflect.Proxy;

/**
 * @author Harvey Suen
 */
public class MainTest {
    
    public static void main(String[] args) {
        test01();
    }
    
    private static void test01() {
        HelloService helloService = new HelloServiceImpl();
        
        HelloService helloServiceProxy = (HelloService) Proxy.newProxyInstance(
            HelloService.class.getClassLoader(),
            new Class[] {HelloService.class},
            (proxy, method, args) -> {
                System.out.println("Before method call");
                Object result = method.invoke(helloService, args);
                System.out.println("After method call");
                return result;
            }
        );
        
        System.out.println(helloServiceProxy.sayHello("harvey"));
    }
}
