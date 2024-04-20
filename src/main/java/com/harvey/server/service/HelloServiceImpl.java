package com.harvey.server.service;

/**
 * @author Harvey Suen
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
        return "Hello " + msg;
    }
}