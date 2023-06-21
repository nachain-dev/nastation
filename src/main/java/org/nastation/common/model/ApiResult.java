package org.nastation.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Data
@ApiModel("Http request result class")
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("request flag")
    private Boolean flag;

    @ApiModelProperty("request error message")
    private String message;

    @ApiModelProperty("response data")
    private T data;

    private ApiResult() {
        super();
        reset();
    }

    public static ApiResult me() {
        return new ApiResult();
    }

    public ApiResult asTrue() {
        this.flag = true;
        return this;
    }

    public ApiResult asFalse() {
        this.flag = false;
        return this;
    }

    public ApiResult msg(String message) {
        this.message = message;
        return this;
    }

    public ApiResult noMsg() {
        this.message = "";
        return this;
    }

    public ApiResult data(T data) {
        this.data = data;
        return this;
    }

    public ApiResult noData() {
        this.data = null;
        return this;
    }

    private void reset() {
        this.flag = false;
        this.message = "";
        this.data = null;
    }

    public ApiResult setFlag(Boolean flag) {
        this.flag = flag;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}