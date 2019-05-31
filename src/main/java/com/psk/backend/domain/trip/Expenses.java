package com.psk.backend.domain.trip;

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
            if (this.price != null && other.getPrice() != null) {
                this.price = this.price.add(other.getPrice());
            } else if (other.getPrice() != null) {
                this.price = other.getPrice();
            }
            if (this.count != null && other.getCount() != null) {
                this.count += other.getCount();
            } else if (other.getCount() != null) {
                this.count = other.getCount();
            }
            this.isOrdered = this.isOrdered && other.isOrdered();
        }
    }
}
