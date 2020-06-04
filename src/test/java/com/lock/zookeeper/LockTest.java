/**
 * FileName: LockTest
 * Author:   songfz
 * Date:     2020/5/29 16:08
 * Description: zk锁测试
 */
package com.lock.zookeeper;

import com.lock.biz.TicketController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 〈一句话功能简述〉<br>
 * 〈zk锁测试〉
 *
 * @author songfz
 * @create 2020/5/29
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(value = SpringRunner.class)
public class LockTest {

    @Autowired
    private TicketController ticketController;

    @Test
    public void buyTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicReference <Integer> buy = new AtomicReference <>(-1);
        StopWatch stopWatch = new StopWatch("买票");
        stopWatch.start();
        for (int i = 1; i < 2000; i++) {
            int finalI = i;
            executor.execute(() -> {
                 buy.set(ticketController.buy("客户" + finalI));

            });

            if(buy.get() == 0){
                break;
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

}
