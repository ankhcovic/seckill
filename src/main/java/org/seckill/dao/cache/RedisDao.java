package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.mysql.jdbc.log.Slf4JLogger;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author gaoyouxin
 * @date 2021/3/7 - 22:47
 */
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool jedisPool;

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }

    // 通过反射拿到字节码对应的对象有哪些属性和方法
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    // 直接访问redis不用访问db就可以直接拿到seckill
    public Seckill getSeckill(long seckillId){
        // 缓存redis操作逻辑，不应该放在service层
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                // 是一个对象没有实现内部序列化操作
                // 典型的缓存访问逻辑
                // get -> byte[] -> 反序列化 -> Object(Seckill)
                // 采用自定义序列化protostuff google
                // 将秒杀对象序列化为二进制数组传递给redis缓存起来
                // classb必须为pojo
                byte[] bytes = jedis.get(key.getBytes());
                // 如果字节数组不为空，说明我们从缓存中获取到了,可以进行转换
                if (bytes != null){
                    // 空对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    // seckill被反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    // 当发现缓存没有的时候去put一个seckill
    public String putSeckill(Seckill seckill){
        // set -> Object(Seckill) -> 序列化 -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60 * 60; // 小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
