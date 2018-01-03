package com.example.targetdataapi.dao;

import com.example.targetdataapi.entity.ProductInfo;

public interface ProductInfoDAO 
{
	ProductInfo getProductInfo(int id);

	boolean updateProductPrice(int id, float price, String currencyType); 
}
