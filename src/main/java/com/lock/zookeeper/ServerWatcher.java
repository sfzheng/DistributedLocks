/**
 * FileName: ServerWatch
 * Author:   songfz
 * Date:     2020/5/28 16:34
 * Description: zookeeper服务观察者
 */
package com.lock.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br>
 * 〈zookeeper服务观察者 监听节点创建〉
 *
 * @author songfz
 * @create 2020/5/28
 * @since 1.0.0
 */

public class ServerWatcher implements Watcher {


    private static final Logger log = LoggerFactory.getLogger(ServerWatcher.class);
    /**
     * zk地址
     */
    private String zkUrl;

    /**
     * session超时时间
     */
    private Integer sessionTimeoutMs;
    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs;

    private CuratorFramework curatorFramework;

    List <String> locksAll = Collections.synchronizedList(new ArrayList <>());

    public ServerWatcher(String zkUrl, Integer sessionTimeoutMs, Integer connectionTimeoutMs) {
        this.zkUrl = zkUrl;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeoutMs;
        //1000：表示curator链接zk的时候超时时间是多少 3：表示链接zk的最大重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        创建zk 连接
        curatorFramework = CuratorFrameworkFactory.newClient(zkUrl, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        curatorFramework.start();
        try {

            createNode(curatorFramework, "/locks", CreateMode.PERSISTENT, "");
//            log.info("创建根节点：【{}】",paths);
            locksAll = curatorFramework.getChildren().usingWatcher(this).forPath("/locks");
            log.info("获取到所有锁地址：【{}】", locksAll);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void process(WatchedEvent event) {

        try {
            if (event.getState().compareTo(Event.KeeperState.Closed) == 0) {
                return;
            }
            List <String> currentLocks = curatorFramework.getChildren().usingWatcher(this).forPath("/locks");
            List <String> noContains = locksAll.stream().filter(l -> !currentLocks.contains(l)).collect(Collectors.toList());
            noContains.stream().forEach(r -> System.out.println("原节点-" + r + "-消失了！"));
            List <String> newNodes = currentLocks.stream().filter(c -> !locksAll.contains(c)).collect(Collectors.toList());
            newNodes.stream().forEach(n -> System.out.println("新节点-" + n + "-增加了！"));
            locksAll = currentLocks;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }


    /**
     * 创建节点
     *
     * @param client     客户端
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
    public static boolean createNode(CuratorFramework client, String path, CreateMode createMode, String data) {
        try {
            client.create().withMode(createMode).forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }

        return true;
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param client 客户端
     * @param path   路径
     * @return null-节点不存在  | CreateMode.PERSISTENT-是持久化 | CreateMode.EPHEMERAL-临时节点
     */
    public static CreateMode getNodeType(CuratorFramework client, String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            if (stat == null) {
                return null;
            }

            if (stat.getEphemeralOwner() > 0) {
                return CreateMode.EPHEMERAL;
            }

            return CreateMode.PERSISTENT;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
