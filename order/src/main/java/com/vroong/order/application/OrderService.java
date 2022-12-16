package com.vroong.order.application;

import com.vroong.order.application.port.in.OrderUsecase;
import com.vroong.order.application.port.out.OrderRepository;
import com.vroong.order.application.port.out.event.OrderEvent;
import com.vroong.order.domain.*;
import com.vroong.order.support.SecurityUtils;
import com.vroong.shared.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUsecase {

  private final OrderRepository orderRepository;
  private final PersistentEventCreator eventCreator;

  @Override
  @Transactional
  public Order createOrder(Orderer orderer, Receiver receiver, List<OrderItem> orderItems) {
    final Money deliveryFee = new Money(3500); // 고정값 사용, 외부값 사용시 port/adapter.out에 구현

    final Order newOrder = Order.placeOrder(orderer, receiver, orderItems, deliveryFee);

    orderRepository.save(newOrder);
    eventCreator.create(OrderStatus.ORDER_PLACED.name(), new OrderEvent(newOrder));

    return newOrder;
  }

  @Override
  @Transactional(readOnly = true)
  public OrderList getOrderList(Integer page, Integer size) {
    final String username = getCurrentUsername();

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
  @Transactional(readOnly = true)
  public Order getOrder(Long orderId) {
    final Order order = orderRepository.getReferenceById(orderId);
    if (!getCurrentUsername().equals(order.getCreatedBy())) {
      throw new IllegalArgumentException("자신의 주문만 조회할 수 있습니다.");
    }

    return order;
  }

  @Override
  @Transactional
  public void updateOrder(Long orderId, Receiver receiver, List<OrderItem> orderItems) {
    final Order order = orderRepository.getReferenceById(orderId);
    if (!getCurrentUsername().equals(order.getCreatedBy())) {
      throw new IllegalArgumentException("자신의 주문만 변경할 수 있습니다.");
    }

    final Money deliveryFee = new Money(3500);

    order.updateOrder(receiver, orderItems, deliveryFee);

    eventCreator.create(OrderStatus.ORDER_UPDATED.name(), new OrderEvent(order));
  }

  @Override
  @Transactional
  public void cancelOrder(Long orderId) {
    final Order order = orderRepository.getReferenceById(orderId);
    if (!getCurrentUsername().equals(order.getCreatedBy())) {
      throw new IllegalArgumentException("자신의 주문만 취소할 수 있습니다.");
    }

    order.cancelOrder();
    orderRepository.save(order);

    eventCreator.create(OrderStatus.ORDER_CANCELED.name(), new OrderEvent(order));
  }

  private String getCurrentUsername() {
    return SecurityUtils.getCurrentUserLogin()
        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다.."));
  }
}
