/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

/**
 *
 * @author wojte
 */
public class InvoiceCalculator {
    private Double price;
    private String priceS;
    
    private int amount;
    private String amountS;
    
    private Double total;
    private String totalS;

    public InvoiceCalculator() {
        price = 0.0;
        amount = 0;
        total = 0.0;
        
       updateStrings(); 
    }
    
    private void updateTotal(){
        total = price * amount; 
        updateStrings();
    }

    private void updateStrings(){
        this.priceS = "" + price;
        this.amountS = "" + amount;
        this.totalS = "" + total;
        
    }


    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
        updateTotal();
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
        updateTotal();
    }


    /**
     * @param total the total to set
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * @return the priceS
     */
    public String getPriceS() {
        return priceS;
    }

    /**
     * @return the amountS
     */
    public String getAmountS() {
        return amountS;
    }

    /**
     * @return the totalS
     */
    public String getTotalS() {
        return totalS;
    }
    
    
    
    
    
}
