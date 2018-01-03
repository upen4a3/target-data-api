package com.example.targetdataapi.entity;
import org.springframework.stereotype.Component;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Component
@Table(keyspace = "target", name = "products")
public class ProductInfo {

	@PartitionKey
	@Column(name = "product_id")
	private int productId;

	@Column(name = "product_price")
	private float productPrice;

	@Column(name = "product_currency_type")
	private String productCurrencyType;

	public String getProductCurrencyType() {
		return productCurrencyType;
	}

	public void setProductCurrencyType(String productCurrencyType) {
		this.productCurrencyType = productCurrencyType;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

}
