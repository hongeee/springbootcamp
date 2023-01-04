package com.vroong.order.adapter.in.rest;

import com.vroong.order.domain.Order;
import com.vroong.order.domain.OrderItem;
import com.vroong.order.domain.OrderList;
import com.vroong.order.domain.OrderStatus;
import com.vroong.order.domain.Orderer;
import com.vroong.order.domain.Page;
import com.vroong.order.domain.Receiver;
import com.vroong.order.rest.OrderDto;
import com.vroong.order.rest.OrderLineDto;
import com.vroong.order.rest.OrderLineItemDto;
import com.vroong.order.rest.OrderListDto;
import com.vroong.order.rest.OrderProductDto;
import com.vroong.order.rest.OrderStateDto;
import com.vroong.order.rest.PageDto;
import com.vroong.order.rest.UserInfoDto;
import com.vroong.product.rest.Product;
import com.vroong.shared.Money;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Fixture {

  public static Product aOrderItem3Product() {
    final OrderItem orderItem = aOrderItem3();
    return new Product()
        .productId(orderItem.getProductId())
        .name(orderItem.getProductName())
        .price(orderItem.getProductPrice().getValue());
  }

  public static OrderListDto aOrderListDto() {
    return new OrderListDto()
        .addDataItem(aOrderDto())
        .page(new PageDto()
            .number(1)
            .size(10)
            .totalElements(1L)
            .totalPages(1)
        );
  }

  public static OrderDto aOrderDto() {
    return new OrderDto()
        .orderId(1L)
        .orderer(aUserInfoDto())
        .receiver(aUserInfoDto())
        .orderState(OrderStateDto.ORDER_PLACED)
        .orderLine(aOrderLineDto())
        .deliveryFee(BigDecimal.valueOf(3500))
        .totalPrice(BigDecimal.valueOf(43500));
  }

  public static UserInfoDto aUserInfoDto() {
    return new UserInfoDto()
        .name("소농민")
        .phoneNumber("010-1234-5678")
        .address("영국 런던 대저택");
  }

  public static OrderLineDto aOrderLineDto() {
    return new OrderLineDto()
        .addDataItem(aOrderItemDto1())
        .addDataItem(aOrderItemDto2());
  }

  public static OrderLineItemDto aOrderItemDto1() {
    return new OrderLineItemDto()
        .product(new OrderProductDto()
            .productId(1L)
            .name("축구공")
            .price(BigDecimal.valueOf(30000))
        )
        .quantity(1);
  }

  public static OrderLineItemDto aOrderItemDto2() {
    return new OrderLineItemDto()
        .product(new OrderProductDto()
            .productId(1L)
            .name("호미")
            .price(BigDecimal.valueOf(5000))
        )
        .quantity(2);
  }

  public static Order aOrder() {
    return new Order(
        1L,
        OrderStatus.ORDER_PLACED,
        new Money(43500),
        new Money(3500),
        aOrderItemList1(),
        aOrderer(),
        aReceiver()
    );
  }

  public static List<OrderItem> aOrderItemList1() {
    List<OrderItem> orderItemList = new ArrayList<>();
    orderItemList.add(aOrderItem1());
    orderItemList.add(aOrderItem2());

    return orderItemList;
  }

  public static List<OrderItem> aOrderItemList2() {
    List<OrderItem> orderItemList = new ArrayList<>();
    orderItemList.add(aOrderItem3());

    return orderItemList;
  }

  public static List<OrderItem> aOrderItemList3() {
    List<OrderItem> orderItemList = new ArrayList<>();
    orderItemList.add(aOrderItem4());

    return orderItemList;
  }

  public static OrderItem aOrderItem1() {
    return new OrderItem(1L, null, 1L, "축구공", new Money(30000), 1);
  }

  public static OrderItem aOrderItem2() {
    return new OrderItem(2L, null, 2L, "호미", new Money(5000), 2);
  }

  public static OrderItem aOrderItem3() {
    return new OrderItem(1L, null, 1L, "축구공", new Money(10000), 1);
  }

  public static OrderItem aOrderItem4() {
    return new OrderItem(2L, null, 2L, "호미", new Money(3333), 3);
  }

  public static Receiver aReceiver() {
    return Receiver.of("소농민", "010-1234-5678", "영국 런던 대저택");
  }

  public static Orderer aOrderer() {
    return Orderer.of("소농민", "010-1234-5678", "영국 런던 대저택");
  }

  public static OrderList aOrderList() {
    return new OrderList(
        List.of(aOrder()),
        Page.SOLE
    );
  }
}
