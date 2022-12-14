package com.vroong.delivery.adapter.in.rest;

import com.vroong.delivery.adapter.in.rest.mapper.DeliveryFactory;
import com.vroong.delivery.adapter.in.rest.mapper.DeliveryMapper;
import com.vroong.delivery.application.port.in.DeliveryService;
import com.vroong.delivery.domain.Delivery;
import com.vroong.delivery.rest.DeliveryApiDelegate;
import com.vroong.delivery.rest.DeliveryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryApiDelegateImpl implements DeliveryApiDelegate {

  final DeliveryService service;
  final DeliveryFactory factory;
  final DeliveryMapper mapper;

  @Override
  public ResponseEntity<Void> cancelDelivery(Long deliveryId) {
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<DeliveryDto> createDelivery(DeliveryDto dto) {
    final Delivery delivery = service.createDelivery(factory.createFrom(dto));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(delivery));
  }

  @Override
  public ResponseEntity<DeliveryDto> getDelivery(Long deliveryId) {
    return ResponseEntity.ok(Fixtures.aDeliveryDto());
  }

  @Override
  public ResponseEntity<Void> updateDelivery(Long deliveryId, DeliveryDto deliveryDto) {
    return ResponseEntity.noContent().build();
  }
}
