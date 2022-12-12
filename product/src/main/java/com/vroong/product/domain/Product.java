package com.vroong.product.domain;

import com.vroong.shared.Money;
import java.time.Instant;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table
@Getter
@ToString
public class Product {

  @Id
  @Column(name = "product_id")
  private Long ProductId;

  @Column(name = "product_name")
  private String name;

  @Column(name = "description")
  private String description;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "price"))
  private Money price;

  @Column(name = "inventory")
  private Integer inventory;

  @Column(name = "supplier")
  private String supplier;

  @Embedded
  private Size size;

  @Column(name = "store_location")
  private String location;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  public Product(String name, String description, Money price,
      Integer inventory, String supplier, Size size, String location) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.inventory = inventory;
    this.supplier = supplier;
    this.size = size;
    this.location = location;
  }
}
