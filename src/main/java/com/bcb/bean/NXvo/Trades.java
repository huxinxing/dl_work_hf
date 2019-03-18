/**
  * Copyright 2018 aTool.org 
  */
package com.bcb.bean.NXvo;
import java.util.List;

/**
 * Auto-generated: 2018-03-14 15:44:21
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class Trades {

    private List<Double> amount;
    private List<Double> price;
    private List<Integer> time;
    private List<Integer> direction;
    public void setAmount(List<Double> amount) {
         this.amount = amount;
     }
     public List<Double> getAmount() {
         return amount;
     }

    public void setPrice(List<Double> price) {
         this.price = price;
     }
     public List<Double> getPrice() {
         return price;
     }

    public void setTime(List<Integer> time) {
         this.time = time;
     }
     public List<Integer> getTime() {
         return time;
     }

    public void setDirection(List<Integer> direction) {
         this.direction = direction;
     }
     public List<Integer> getDirection() {
         return direction;
     }

}