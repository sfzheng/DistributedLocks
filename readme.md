# DistributedLocks 分布式锁demo

#说明
- redis锁
   * 原理
     ---
     使用redis SETNX 命令 设置key 过期时间。ttl过期自动删除key值。
   * 优点
    * * 1：redis官方有支持的分布式锁算法，且redis具备天然的原子性事务的特征
   * * 2：redis并发高，响应快需要
   * * 3：支持互斥，容错率高
   * 缺点：
    * * 1：因为原子性事务原则，客户端不可达的情况下，必须到了超时的阈值才能释放锁
    * * 2：需要不停的发送锁请求获取锁信息，增加开销
   
- zookeeper锁
- consul锁