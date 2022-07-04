package cn.com.hellowood.swagger.common;


import cn.com.hellowood.swagger.utils.JSONUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response bean for format response
 *
 * @author HelloWood
 * @date 2017-07-11 15:33
 */
public class CommonResponse {

    @ApiModelProperty(notes = "状态码")
    private int code;

    @ApiModelProperty(notes = "返回消息")
    private String message;

    @ApiModelProperty(notes = "返回的数据")
    private Object data;

    public int getCode() {
        return code;
    }

    public CommonResponse setCode(ResponseCode responseCode) {
        this.code = responseCode.code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public CommonResponse setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
