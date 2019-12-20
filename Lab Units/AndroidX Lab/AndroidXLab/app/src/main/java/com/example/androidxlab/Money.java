package com.example.androidxlab;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Bidi;

public class Money implements Serializable {

    private BigDecimal amount;

    public Money(BigDecimal a) {
        amount = a;

    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal a){
        amount = a;
    }

    public String toString(){
        return "$" + amount;
    }
}
