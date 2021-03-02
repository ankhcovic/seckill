package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author gaoyouxin
 * @date 2021/2/22 - 22:17
 */
/**
 * 配置spring和junit整合，这样junit在启动时就会加载springIOC容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
    // 注入dao实现类依赖
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1000L;
        long phone = 18867151160L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount=" + insertCount);
    }

    @Test
    /**
     * SuccessKilled{seckillId=1000, userPhone=18867151160, state=0, createTime=Mon Feb 22 22:47:59 CST 2021, seckill=Seckill{seckillId=1000, name='1000元秒杀iphone6', number=99, startTime=Mon Feb 22 22:20:04 CST 2021, endTime=Thu Feb 25 00:00:00 CST 2021, createTime=Mon Feb 22 21:15:03 CST 2021}}
     * Seckill{seckillId=1000, name='1000元秒杀iphone6', number=99, startTime=Mon Feb 22 22:20:04 CST 2021, endTime=Thu Feb 25 00:00:00 CST 2021, createTime=Mon Feb 22 21:15:03 CST 2021}
     */
    public void queryByIdWithSeckill() {
        long id = 1000L;
        long phone = 18867151160L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}