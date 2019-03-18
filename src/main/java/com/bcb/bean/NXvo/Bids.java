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
public class Bids {

    private List<Double> amount;
    private List<Integer> level;
    private List<Double> price;
    public void setAmount(List<Double> amount) {
         this.amount = amount;
     }
     public List<Double> getAmount() {
         return amount;
     }

    public void setLevel(List<Integer> level) {
         this.level = level;
     }
     public List<Integer> getLevel() {
         return level;
     }

    public void setPrice(List<Double> price) {
         this.price = price;
     }
     public List<Double> getPrice() {
         return price;
     }

}