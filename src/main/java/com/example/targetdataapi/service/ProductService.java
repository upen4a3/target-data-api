package com.example.targetdataapi.service;

import java.util.Map;

import com.example.targetdataapi.exceptions.ProductNameNotFoundException;
import com.example.targetdataapi.pojo.Products;

public interface ProductService 
{
	public Products returnMainResponse(String id) throws ProductNameNotFoundException ;
	public String updateProductPricing(Map<String, Object> productPricing);
}
