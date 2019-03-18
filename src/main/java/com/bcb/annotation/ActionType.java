/*
 * Project Name: base
 * File Name: ActionType.java
 * Class Name: ActionType
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
package com.bcb.annotation;

/**
* Class Name: ActionType
* Description：操作类型
* @author qiaozhenhan
* Date Sep 18, 2017
*/
public enum ActionType {

	ADD("1", "增加"),
    UPDATE("2", "更新"),
    DELETE("3", "删除"),
    BATCHADD("4", "批量增加"),
    BATCHDELETE("5", "批量删除"),
    QUERY("6", "查询"),
    OTHERRESULT("7", "其它");


    private String code;
    private String text;

    ActionType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
