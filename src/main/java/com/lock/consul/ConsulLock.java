/**
 * FileName: ConsulLock
 * Author:   songfz
 * Date:     2020/6/3 14:26
 * Description: consul实现分布式锁
 */
package com.lock.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;
import com.ecwid.consul.v1.session.model.Session;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈consul实现分布式锁〉
 *
 * @author songfz
 * @create 2020/6/3
 * @since 1.0.0
 */
@Component
public class ConsulLock {

    private ConsulClient consulClient;

    public ConsulLock(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    public boolean tryLock(String lockName, long ttlTime) {


        String sessionId = createSession(lockName, ttlTime);
        if (ttlTime < 10 || ttlTime > 86400) {
            ttlTime = 60;
        }
        PutParams putParams = new PutParams();
        putParams.setAcquireSession(sessionId);
        return consulClient.setKVValue(lockName, sessionId, putParams).getValue();
    }


    public void unlock(String lockName) {
        consulClient.deleteKVValue(lockName);
    }

    private String createSession(String lockName, long ttlTime) {

        NewCheck check = new NewCheck();

        check.setId("check:" + lockName);
        check.setName("check:" + lockName);
        check.setTtl(ttlTime + "s");
        check.setTimeout(10+"s");
        consulClient.agentCheckRegister(check);
        consulClient.agentCheckPass(check.getId());


        NewSession session = new NewSession();
        session.setBehavior(Session.Behavior.RELEASE);
        session.setName("session:" + lockName);
        session.setTtl(ttlTime + "s");
        session.setLockDelay(1);
        List <String> checks = new ArrayList <>();
        checks.add(check.getId());
        session.setChecks(checks);
        return consulClient.sessionCreate(session, null).getValue();
    }

}
