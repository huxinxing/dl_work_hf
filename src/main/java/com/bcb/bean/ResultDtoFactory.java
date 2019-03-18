/*
 * Project Name: dctp
 * File Name: ResultDtoFactory.java
 * Class Name: ResultDtoFactory
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * Licensed under the Hengtiansoft
 *
 * http://www.hengtiansoft.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bcb.bean;


import com.bcb.domain.enums.ReturnCodeEnum;

/**
* Class Name: ResultDtoFactory
* @author qiaozhenhan
* Date Apr 5, 2018
*/
public final class ResultDtoFactory {

    private ResultDtoFactory() {
    };

    public static MyResponseResult toSuccess(String msg) {
        return toSuccess(msg,null);
    }

    public static MyResponseResult toSuccess(String msg,Object data) {
        MyResponseResult dto = new MyResponseResult();
        dto.setCode(ReturnCodeEnum.成功.getValue());
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

    public static  MyResponseResult toInternalFail(String msg) {
        return toInternalFail(msg,null);
    }

    public static  MyResponseResult toInternalFail(String msg, Object data) {
        MyResponseResult dto = new MyResponseResult();
        dto.setCode(ReturnCodeEnum.内部错误.getValue());
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

    public static  MyResponseResult toBusinessFail(String msg) {
        return toBusinessFail(msg,null);
    }

    public static  MyResponseResult toBusinessFail(String msg, Object data) {
        MyResponseResult dto = new MyResponseResult();
        dto.setCode(ReturnCodeEnum.业务错误.getValue());
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

    public static  MyResponseResult toParameterFail(String msg) {
        return toParameterFail(msg,null);
    }

    public static  MyResponseResult toParameterFail(String msg, Object data) {
        MyResponseResult dto = new MyResponseResult();
        dto.setCode(ReturnCodeEnum.参数错误.getValue());
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

}

