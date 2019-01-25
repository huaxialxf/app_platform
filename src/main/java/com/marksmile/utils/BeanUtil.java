package com.marksmile.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanUtil {

    private static BeanUtil benaUtil;
    private static ApplicationContext context;

    static {
        benaUtil = new BeanUtil();
        context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
    }

    private BeanUtil() {

    }

    public static BeanUtil getBeanUtil() {
        return benaUtil;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static ApplicationContext getContext() {
        return context;
    }
    public static void main(String[] args) {
		BeanUtil.getContext();
	}
}
