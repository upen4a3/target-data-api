package com.example.targetdataapi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.example.targetdataapi.dao.impl.ProductDAOInfoImpl;
import com.example.targetdataapi.entity.ProductInfo;
import com.example.targetdataapi.exceptions.ProductNameNotFoundException;
import com.example.targetdataapi.pojo.Products;
import com.example.targetdataapi.service.impl.ProductServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TargetDataApiApplicationTests {

	// @Autowired
	// private RestTemplate template;

	@Autowired
	private ProductServiceImpl productServiceImpl;

	@Autowired
	private ProductDAOInfoImpl productDaoImpl;

	/***********
	 * Functional Testing
	 * 
	 * @throws ProductNameNotFoundException
	 ************/

	@Test
	public void serviceImplemenation_getMethod() {
		Products abc;
		try {
			abc = productServiceImpl.returnMainResponse("13860428");
			assertEquals(abc.getId(), Integer.valueOf(13860428));
			assertEquals(abc.getCurrent_price().getCurrency_code(), "YUN");
			assertEquals(abc.getName(), "The Big Lebowski (Blu-ray)");
			assertEquals(231, abc.getCurrent_price().getPrice(), 232);
		} catch (ProductNameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void serviceImplemenation_getMethod_CustomerNotFund() {
		Products abc = null;
		try {
			abc = productServiceImpl.returnMainResponse("336");

			assertEquals(abc.getId(), Integer.valueOf(0));
		} catch (ProductNameNotFoundException e) {
			// TODO Auto-generated catch block
		}

	}

	@Test
	public void daoIplementation_getMethod() {
		ProductInfo abc = productDaoImpl.getProductInfo(13860428);
		assertEquals(abc.getProductId(), 13860428);
		assertEquals(abc.getProductCurrencyType(), "YUN");
		assertEquals(231, abc.getProductPrice(), 232);

	}

	@Test
	public void daoImplementation_getMethod_CustomerNotFund() {
		ProductInfo abc = productDaoImpl.getProductInfo(336236);
		assertEquals(abc, null);

	}

	/***
	 * Integartion Tetsing
	 ***/

	@Test
	public void testGetProduct_HappyPath() {

		RestTemplate template = new RestTemplate();
		String jsonString = template.getForObject("http://localhost:8182/products/13860428", String.class);
		Gson gson = new GsonBuilder().create();
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		assertThat(jsonObject.get("id").getAsInt(), equalTo(13860428));
		assertThat(jsonObject.get("name").getAsString(), equalTo("The Big Lebowski (Blu-ray)"));

		assertThat(jsonObject.get("current_price").getAsJsonObject().get("price").getAsFloat(), equalTo(3356f));

		assertThat(jsonObject.get("current_price").getAsJsonObject().get("currency_code").getAsString(),
				equalTo("YUN"));

	}

	@Test
	public void testGetProduct_HappyPath_MediaType() {

		RestTemplate template = new RestTemplate();
		HttpHeaders httpHeaders = template.headForHeaders("http://localhost:8182/products/13860428");
		assertTrue(httpHeaders.getContentType().includes(MediaType.APPLICATION_JSON));

	}

	@Test
	public void testGetProduct_HappyPath_StatusCode() {

		HttpUriRequest request = new HttpGet("http://localhost:8182/products/13860428");

		// When
		HttpResponse httpResponse;
		try {
			httpResponse = HttpClientBuilder.create().build().execute(request);

			// Then
			assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(200));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetProduct_HappyPath_SecondCustomer() {

		RestTemplate template = new RestTemplate();
		String jsonString = template.getForObject("http://localhost:8182/products/15643793", String.class);
		Gson gson = new GsonBuilder().create();
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		assertThat(jsonObject.get("id").getAsInt(), equalTo(15643793));
		assertThat(jsonObject.get("name").getAsString(), equalTo("The Big Lebowski (Blu-ray)"));

		assertThat(jsonObject.get("current_price").getAsJsonObject().get("price").getAsFloat(), equalTo(231f));

		assertThat(jsonObject.get("current_price").getAsJsonObject().get("currency_code").getAsString(),
				equalTo("YUN"));

	}

	@Test
	public void testGetProduct_CustomerNotFound() {

		HttpUriRequest request = new HttpGet("http://localhost:8182/products/336");

		// When
		HttpResponse httpResponse;
		try {
			httpResponse = HttpClientBuilder.create().build().execute(request);
			// Then
			assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(404));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetProduct_BadRequest() throws ClientProtocolException, IOException {

		HttpUriRequest request = new HttpGet("http://localhost:8182/products/abcd");

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(400));
	}

}
