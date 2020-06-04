/**
 * FileName: DistributedZKLock
 * Author:   songfz
 * Date:     2020/5/29 11:01
 * Description: zk分布式锁
 */
package com.lock.zookeeper;

import com.google.common.base.Stopwatch;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br>
 * 〈zk分布式锁〉
 *
 * @author songfz
 * @create 2020/5/29
 * @since 1.0.0
 */
@Log4j2
@Component
public class DistributedZooKeeperLock {

    private CuratorFramework curatorFramework;

    public DistributedZooKeeperLock(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    /**
     * 获取锁
     * @param path
     * @param outTime
     * @param unit
     * @return
     * @throws Exception
     */
    public boolean tryLock(String path, long outTime, TimeUnit unit )  {
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, path);

        try {
          if(lock.acquire(outTime,unit)){

              return true;
          }
      }catch (Exception e){
          log.error(e.getMessage(),e);
      }finally {
          if(lock.isAcquiredInThisProcess()){

              try {
                  lock.release();
              } catch (Exception e) {
                log.error(e.getMessage(),e);
              }
          }
      }
        return false;
    }
}
