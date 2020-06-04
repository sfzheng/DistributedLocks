/**
 * FileName: ApplicaitonBean
 * Author:   songfz
 * Date:     2020/5/28 16:05
 * Description: 手动注入bean
 */
package com.lock.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.lock.zookeeper.ServerWatcher;
import org.apache.curator.framework.CuratorFramework;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * 〈手动注入bean〉
 *
 * @author songfz
 * @create 2020/5/28
 * @since 1.0.0
 */
@Configuration
public class ApplicationBean {

    private RedisProperties redisProperties;

    private ZooKeeperProperties zooKeeperProperties;

    @Value("${spring.cloud.consul.host}")
    private String consulHost;

    @Value("${spring.cloud.consul.port}")
    private Integer consulPort;
    public ApplicationBean(RedisProperties redisProperties, ZooKeeperProperties zooKeeperProperties) {
        this.redisProperties = redisProperties;
        this.zooKeeperProperties = zooKeeperProperties;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setDatabase(0);
        return Redisson.create(config);
    }


    @Bean
    public CuratorFramework curatorFramework() {
        ServerWatcher serverWatcher = new ServerWatcher(zooKeeperProperties.getHost()+ ":" + zooKeeperProperties.getPort(), zooKeeperProperties.getSessionTimeoutMs(), zooKeeperProperties.getConnectionTimeoutMs());
        return serverWatcher.getCuratorFramework();
    }

    @Bean
    public ConsulClient consulClient(){
        return  new ConsulClient(consulHost,consulPort);
    }
}
