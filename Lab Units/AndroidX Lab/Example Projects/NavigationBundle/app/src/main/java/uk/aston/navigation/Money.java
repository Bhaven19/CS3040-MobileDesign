package uk.aston.navigation;

import java.io.Serializable;
import java.math.BigDecimal;

public class Money implements Serializable {

    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String toString() {
        return "£" + amount;
    }
}
