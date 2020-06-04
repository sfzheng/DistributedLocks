/**
 * FileName: SpringBeanUtil
 * Author:   songfz
 * Date:     2020/5/29 15:46
 * Description: 获取bean
 */
package com.lock.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈获取bean〉
 *
 * @author songfz
 * @create 2020/5/29
 * @since 1.0.0
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SpringBeanUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(String beanName){
        if(applicationContext.containsBean(beanName)){

            return  (T)applicationContext.getBean(beanName);
        }
        return null;
    }

    public static <T> T getBean(Class<T> claz){
        return applicationContext.getBean(claz);
    }

    public static <T>Map<String,T> getBeansByType(Class<T> claz){
        return  applicationContext.getBeansOfType(claz);
    }
}
