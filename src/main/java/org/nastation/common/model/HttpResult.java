package org.nastation.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Data
@ApiModel("Http request result class")
public class HttpResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("request flag")
    private Boolean flag;

    @ApiModelProperty("request error message")
    private String message;

    @ApiModelProperty("response data")
    private Object data;

    private HttpResult() {
        super();
        reset();
    }

    public static HttpResult me() {
        return new HttpResult();
    }

    public HttpResult asTrue() {
        this.flag = true;
        return this;
    }

    public HttpResult asFalse() {
        this.flag = false;
        return this;
    }

    public HttpResult msg(String message) {
        this.message = message;
        return this;
    }

    public HttpResult noMsg() {
        this.message = "";
        return this;
    }

    public HttpResult data(Object data) {
        this.data = data;
        return this;
    }

    public HttpResult noData() {
        this.data = "";
        return this;
    }

    private void reset() {
        this.flag = false;
        this.message = "";
        this.data = "";
    }

    public HttpResult setFlag(Boolean flag) {
        this.flag = flag;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}