package vn.vntravel.schema.domain.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem extends Bean {

    private Long id;
    @JsonProperty("order_id")
    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
