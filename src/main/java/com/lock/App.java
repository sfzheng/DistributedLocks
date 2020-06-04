/**
 * FileName: App
 * Author:   songfz
 * Date:     2020/5/28 15:50
 * Description: 启动类
 */
package com.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 〈一句话功能简述〉<br> 
 * 〈启动类〉
 *
 * @author songfz
 * @create 2020/5/28
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
