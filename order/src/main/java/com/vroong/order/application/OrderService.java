package com.vroong.order.application;

import com.vroong.order.application.port.in.OrderUsecase;
import com.vroong.order.application.port.out.OrderRepository;
import com.vroong.order.application.port.out.event.OrderEvent;
import com.vroong.order.domain.Order;
import com.vroong.order.domain.OrderItem;
import com.vroong.order.domain.OrderList;
import com.vroong.order.domain.OrderStatus;
import com.vroong.order.domain.Orderer;
import com.vroong.order.domain.Receiver;
import com.vroong.order.support.SecurityUtils;
import com.vroong.shared.Money;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUsecase {

  private final OrderRepository orderRepository;
  private final PersistentEventCreator eventCreator;

  @Override
  public Order createOrder(Orderer orderer, Receiver receiver, List<OrderItem> orderItems) {
    final Money deliveryFee = new Money(3500); // 고정값 사용, 외부값 사용시 port/adapter.out에 구현

    final Order newOrder = Order.placeOrder(orderer, receiver, orderItems, deliveryFee);

    orderRepository.save(newOrder);
    eventCreator.create(OrderStatus.ORDER_PLACED.name(), new OrderEvent(newOrder));

    return newOrder;
  }

  @Override
  public Order getOrder(Long orderId) {
    return orderRepository.getReferenceById(orderId);
  }

  @Override
  public OrderList getOrderList(Integer page, Integer size) {
    final String username = SecurityUtils.getCurrentUserLogin()
        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    final Page<Order> orderPage = orderRepository.findAllByCreatedBy(username, pageable);

    return OrderList.of(
        orderPage.getContent(),
        orderPage.getSize(),
        orderPage.getTotalElements(),
        orderPage.getTotalPages(),
        orderPage.getNumber()
    );
  }

  @Override
  public void cancelOrder(Long orderId) {
    final Order order = orderRepository.getReferenceById(orderId);
    order.updateStatus(OrderStatus.ORDER_CANCELED);
    orderRepository.save(order);

    eventCreator.create(OrderStatus.ORDER_CANCELED.name(), new OrderEvent(order));
  }
}
