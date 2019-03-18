package com.bcb.bean.req;

public class OperationLogRequestDto {
    private String actionType;
    private String operatorName;
    private String createTimeStart;
    private String createTimeEnd;
    private String parameterContext;
    private String commentDescribe;
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getParameterContext() {
        return parameterContext;
    }

    public void setParameterContext(String parameterContext) {
        this.parameterContext = parameterContext;
    }

    public String getCommentDescribe() {
        return commentDescribe;
    }

    public void setCommentDescribe(String commentDescribe) {
        this.commentDescribe = commentDescribe;
    }
}
