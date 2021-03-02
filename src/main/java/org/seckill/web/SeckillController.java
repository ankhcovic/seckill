package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.Date;
import java.util.List;

/**
 * 接收参数，根据参数进行跳转控制
 * @author gaoyouxin
 * @date 2021/3/2 - 13:37
 */
@Controller
@RequestMapping("/seckill")// url:模块/资源/{}/细分
public class SeckillController {
    // 日志对象
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(name = "list", method = RequestMethod.GET)
    public String list(Model model){
        //list.jsp+mode=ModelAndView
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";// /WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // ajax json
    // 返回一个json数据，数据中封装了我们商品的秒杀地址
    @RequestMapping(value = "/{seckillId}/exposer",
                    method = RequestMethod.POST,
                    produces = {"application/json; charset=UTF-8"}) // 告诉浏览器content/type
    @ResponseBody // 此注解是告诉spring框架将返回类型封装为json
    public SeckillResult<Exposer> exposer(Long seckillId){
        SeckillResult<Exposer> result;
        try {
            // true则返回exposer
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e){
            // false则记录错误信息
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    // 执行秒杀
    // 封装用户是否秒杀成功的信息
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   // 如果required为true这个参数为必须的，程序会报错
                                                   @CookieValue(value = "killphone", required = false) Long phone,
                                                   @PathVariable("md5")String md5) {

        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            result = new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e1) {
            // 记录RepeatKillException错误信息
            logger.error(e1.getMessage(), e1);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            result = new SeckillResult<SeckillExecution>(false, execution);
        } catch (SeckillCloseException e2) {
            // 记录SeckillCloseException错误信息
            logger.error(e2.getMessage(), e2);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            result = new SeckillResult<SeckillExecution>(false, execution);
        } catch (Exception e) {
            // 记录Exception错误信息
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            result = new SeckillResult<SeckillExecution>(false, execution);
        }
        return result;
    }

    // 获取系统时间
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }
}
