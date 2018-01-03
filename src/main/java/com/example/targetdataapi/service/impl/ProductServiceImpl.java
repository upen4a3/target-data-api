package com.example.targetdataapi.service.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.targetdataapi.controller.RetailController;
import com.example.targetdataapi.dao.ProductInfoDAO;
import com.example.targetdataapi.exceptions.NotFoundException;
import com.example.targetdataapi.exceptions.ProductNameNotFoundException;
import com.example.targetdataapi.pojo.Current_Price;
import com.example.targetdataapi.pojo.Products;
import com.example.targetdataapi.service.ProductService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ProductServiceImpl implements ProductService {
	
	

	private static final Logger LOG = LoggerFactory.getLogger(RetailController.class);
	@Autowired
	public Current_Price current_Price;

	@Autowired
	public ProductInfoDAO productInfoDao;

	public String productName(int id) {

		String jsonString = null;
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://redsky.target.com/v2/pdp/tcin";

		try {

			jsonString = restTemplate.getForObject(url + "/{id}", String.class, id);
		} catch (Exception e) {
			if (e.getMessage().contains("Not Found")) {
				return null;
			}
		}

		Gson gson = new GsonBuilder().create();
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		System.out.println("Printer is ======" + (jsonObject.get("product")).getAsJsonObject().get("item")
				.getAsJsonObject().get("product_description"));
		JsonElement jsonElement = jsonObject.get("product");
		String title = jsonElement.getAsJsonObject().get("item").getAsJsonObject().get("product_description")
				.getAsJsonObject().get("title").getAsString();
		System.out.println("Title of the produc is " + title);
		return title;
	}

	public Products returnMainResponse(String Id) throws ProductNameNotFoundException {
		
		Products products = new Products();
		int id = Integer.parseInt(Id);
		
		/** Get the product name from the external API using Rest Templete ***/
		
		String productName = productName(id);

		if (productName == null) {
			
				throw new ProductNameNotFoundException("Product not present in External API");
				
		} else if (productInfoDao.getProductInfo(id) != null && productInfoDao.getProductInfo(id).getProductId() != 0
				&& productName != null) {
			
			
			/**Upon Successful validation set the Cassandra vaues to POJ**/
			
			current_Price.setCurrency_code(productInfoDao.getProductInfo(id).getProductCurrencyType());
			current_Price.setPrice(productInfoDao.getProductInfo(id).getProductPrice());
			products.setId(productInfoDao.getProductInfo(id).getProductId());

			products.setCurrent_price(current_Price);

			products.setName(productName);
		}

		else {
			/***In Case ID not founf in the database throw exception to Controller class**/
			
			LOG.error("ID NotFound In Database");
			throw new NotFoundException("ID Not Found In Database");
		}
		return products;
	}

	@Override
	public String updateProductPricing(Map<String, Object> productPricing) {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> updateProductPricingMap = (Map) productPricing;
		Gson gson = new Gson();
		String jsonString = gson.toJson(updateProductPricingMap);
		ObjectMapper mapper = new ObjectMapper();
		Products updatePricing;
		boolean b = false;
		boolean c = false;

		try {
			updatePricing = mapper.readValue(jsonString, Products.class);

			int id = updatePricing.getId();
			float price = updatePricing.getCurrent_price().getPrice();
			String currencyType = updatePricing.getCurrent_price().getCurrency_code();
			if (productInfoDao.getProductInfo(id) == null || productInfoDao.getProductInfo(id).getProductId() == 0) {
				b = productInfoDao.updateProductPrice(id, price, currencyType);
			} else {
				c = productInfoDao.updateProductPrice(id, price, currencyType);
			}
			if (b == true) {
				return "Inserted Successfully";
			} else if (c == true) {
				return "Updated Successfully";
			}
			else
			{
				return "Update Failed";
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Update Failed";

	}

}
