-- 秒杀执行存储过程
delimiter $$ --  console ; 转换为 $$
-- 定义存储过程
-- 插入购买明细 + 更新库存
-- 参数 in 输入参数 out输出参数
-- row_count():返回上一条修改类型sql(delete,insert,update)的影响行数
-- row_count():0:未修改数据 >0 表示修改的行数 <0 sql错误/未执行修改sql
create procedure `seckill`.`excute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
        in v_kill_time timestamp, out r_result int)
    begin
        declare insert_count int default 0;
        start transaction;
        insert ignore into success_killed
            (seckill_id, user_phone, creat_time)
            values (v_seckill_id, v_phone, v_kill_time);
        select row_count() into insert_count;-- 辅助判断insert的操作
        if(insert_count = 0) then -- 影响0行，重复秒杀
            rollback;
            set r_result = -1;
        elseif(insert_count < 0) then -- 系统异常
            rollback;
            set r_result = -2;
        else
            update seckill
            set number = number - 1
            where seckill_id = v_seckill_id
                and end_time > v_kill_time
                and start_time < v_kill_time
                and number > 0;
            select row_count() into insert_count;-- 辅助判断update的操作
            if(insert_count = 0) then -- 影响0行，秒杀结束
                rollback;
                set r_result = 0;
            elseif(insert_count < 0) then -- sql执行出错，等待行级锁超时
                rollback;
                set r_result = -2;
            else
                commit;
                set r_result = 1;
            end if;
        end if;
    end;
$$
-- 存储过程定义结束

delimiter ;

set @r_result  = -3;
-- 执行存储过程
call excute_seckill(1003,19858876466, now(), @r_result);
-- 获取结果
select @r_result;

-- 存储过程
-- 1：存储过程优化：事务行级锁持有的时间尽可能短
-- 2：不要过度依赖存储过程
-- 3：银行多用，互联网公司用的比较少
-- 4：简单的逻辑秒杀系统可以用存储过程来优化
-- 5：qps：一个秒杀单6000/qbs