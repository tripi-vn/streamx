package vn.vntravel.schema.domain.window;

import vn.vntravel.schema.domain.bean.Order;
import vn.vntravel.schema.domain.bean.OrderItem;

import java.io.Serializable;

public class OrderWindow implements Serializable {
    private Order order;
    private OrderItem orderItem;

    public OrderWindow(OrderItem orderItem, Order order) {
        this.order = order;
        this.orderItem = orderItem;
    }

    public Order getOrder() {
        return order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }
}
