/**
 * FileName: biz
 * Author:   songfz
 * Date:     2020/5/29 15:58
 * Description: 业务
 */
package com.lock.biz;

import com.lock.consul.ConsulLock;
import com.lock.redis.RedisLock;
import com.lock.redis.RedisLockAop;
import com.lock.zookeeper.DistributedZooKeeperLock;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br>
 * 〈业务场景〉
 *
 * @author songfz
 * @create 2020/5/29
 * @since 1.0.0
 */
@RestController
@Log4j2
public class TicketController {

    private volatile Integer ticketNum = 100;

    private DistributedZooKeeperLock zooKeeperLock;

    private RedisLock redisLock;

    private ConsulLock consulLock;

    public TicketController(DistributedZooKeeperLock zooKeeperLock, RedisLock redisLock, ConsulLock consulLock) {
        this.zooKeeperLock = zooKeeperLock;
        this.redisLock = redisLock;
        this.consulLock = consulLock;
    }

    /**
     * redis分布式锁应用
     * @param peopleName
     * @return
     */
//     @RequestMapping("buy")
//    @RedisLockAop(lockKey = "ticket",waitTime = 10,leaseTime = 100,timeUnit = TimeUnit.MILLISECONDS)
//    public Integer buy(String peopleName) {
//
//            if (ticketNum <= 0) {
//                log.error("票卖完了,{}不要买了",peopleName);
//                return 0;
//            }
//            log.info("顾客{}买到第{}张票！",peopleName,ticketNum);
//            return ticketNum = ticketNum - 1;
//
//    }

    /**
     * zk分布式锁应用
     * @param peopleName
     * @return
     */
//    @RequestMapping("buy")
//    public Integer buy(String peopleName) {
//
//
//        if(zooKeeperLock.tryLock("/locks/ticket",10, TimeUnit.MILLISECONDS)){
//        if (ticketNum <= 0) {
//            log.error("票卖完了");
//            return 0;
//        }
//        log.info("顾客{}买到第{}张票！",peopleName,ticketNum);
//        return ticketNum = ticketNum - 1;
//        }
//
//        return -1;
//    }

    /**
     * consul 分布式锁应用
     *
     * @param peopleName
     * @return
     */
    @RequestMapping("buy")
    public Integer buy(String peopleName) {
        try {
            if (consulLock.tryLock(peopleName, 10)) {
                if (ticketNum <= 0) {
                    log.error("票卖完了");
                    return 0;
                }
                log.info("顾客{}买到第{}张票！", peopleName, ticketNum);
                return ticketNum = ticketNum - 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            consulLock.unlock(peopleName);
        }

        return -1;
    }
}
