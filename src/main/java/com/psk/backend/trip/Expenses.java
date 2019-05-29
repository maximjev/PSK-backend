package com.psk.backend.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Expenses {
    private Long count;
    private BigDecimal price;
    private boolean isOrdered;

    public void merge(Expenses other) {
        if (other != null) {
            this.price = this.price.add(other.getPrice());
            this.count += other.getCount();
            this.isOrdered = this.isOrdered && other.isOrdered();
        }
    }
}
