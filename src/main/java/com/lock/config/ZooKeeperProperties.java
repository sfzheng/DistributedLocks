/**
 * FileName: ZooKeeperProperties
 * Author:   songfz
 * Date:     2020/5/28 16:38
 * Description: zk系统配置
 */
package com.lock.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br> 
 * 〈zk系统配置〉
 *
 * @author songfz
 * @create 2020/5/28
 * @since 1.0.0
 */
@Configuration
@RefreshScope
@Data
public class ZooKeeperProperties {

    @Value("${spring.zookeeper.host}")
    private String host;
    @Value("${spring.zookeeper.port}")
    private Integer port;
    @Value("${spring.zookeeper.session_timeout_ms}")
    private Integer sessionTimeoutMs;
    @Value("${spring.zookeeper.connection_timeout_ms}")
    private Integer connectionTimeoutMs;
}
