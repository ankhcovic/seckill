package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author gaoyouxin
 * @date 2021/3/2 - 0:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    // 日志对象
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    // 获取列表
    public void getSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        System.out.println(seckills);
    }

    @Test
    // 获取列表中的一个商品的信息
    public void getById() {
        long secKillId = 1000;
        Seckill seckill = seckillService.getById(secKillId);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long secKillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(secKillId);
        System.out.println(exposer);
    }

    @Test
    public void executeSeckill() {
        long secKillId = 1000;
        long userPhone = 19858876466L;
        String md5 = "bf204e2683e7452aa7db1a50b5713bae";

        // 在该测试方法中添加try catch,将程序允许的异常包起来而不去向上抛给junit
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(secKillId, userPhone, md5);

            System.out.println(seckillExecution);
        } catch (RepeatKillException e){
            e.printStackTrace();
        } catch (SeckillCloseException e1){
            e1.printStackTrace();
        }
    }

    @Test
    // 将方法三和方法四结合起来完整逻辑测试，不然每次都得通过方法三暴露秒杀商品地址后
    public void testSeckillLogic() {
        long secKillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(secKillId);
        if (exposer.isExposed()){
            // 秒杀开启
            System.out.println(exposer);

            long userPhone = 18072953403L;
            String md5 = "bf204e2683e7452aa7db1a50b5713baw";

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(secKillId, userPhone, md5);
                System.out.println(seckillExecution);
            } catch (RepeatKillException e){
                e.printStackTrace();
            } catch (SeckillCloseException e1){
                e1.printStackTrace();
            } catch (SeckillException e2){
                e2.printStackTrace();
            }
        } else {
            // 秒杀未开启
            System.out.println(exposer);
        }
    }
}