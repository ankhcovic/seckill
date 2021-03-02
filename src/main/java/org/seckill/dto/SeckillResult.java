package org.seckill.dto;

/**
 * @author gaoyouxin
 * @date 2021/3/2 - 14:00
 */
public class SeckillResult<T> {
    private boolean success;
    private T data;
    private String error;

    // true有数据
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    // error要把错误信息传过去
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
