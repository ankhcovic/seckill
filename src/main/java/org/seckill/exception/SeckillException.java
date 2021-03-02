package org.seckill.exception;

/**
 * 秒杀相关的所有业务异常
 * @author gaoyouxin
 * @date 2021/2/28 - 23:11
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
