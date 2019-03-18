/**
  * Copyright 2018 aTool.org 
  */
package com.bcb.bean.NXvo;

/**
 * Auto-generated: 2018-03-14 15:44:21
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class JsonRootBean {

    private String symbolId;
    private int idCur;
    private String msgType;
    private Payload payload;
    private int idPrev;
    private int _id;
    private int version;

    public String getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(String symbolId) {
        this.symbolId = symbolId;
    }

    public int getIdCur() {
        return idCur;
    }

    public void setIdCur(int idCur) {
        this.idCur = idCur;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public int getIdPrev() {
        return idPrev;
    }

    public void setIdPrev(int idPrev) {
        this.idPrev = idPrev;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}