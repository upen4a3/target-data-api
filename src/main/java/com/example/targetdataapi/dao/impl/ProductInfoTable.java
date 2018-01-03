package com.example.targetdataapi.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.example.targetdataapi.entity.ProductInfo;
import com.example.targetdataapi.utilities.ProductInfoColumns;
import com.google.common.base.Joiner;

@Component
public class ProductInfoTable {
	

	private static final Logger LOG = LoggerFactory.getLogger(ProductInfoTable.class);

	@Autowired
	private Session cassandraSession;

	@Autowired
	private MappingManager mappingManager;

	@Bean
	public Mapper<ProductInfo> productInfoMapper() throws Exception {
		return mappingManager.mapper(ProductInfo.class);
	}

	@Bean
	public MappingManager mappingManager() {
		MappingManager mappingManager = new MappingManager(cassandraSession);
		return mappingManager;
	}

	@Bean
	public PreparedStatement getProductInfoPreparedStatement() 
	{
		LOG.info("Executing the Get Query Against Cassandra");

		return cassandraSession.prepare("SELECT " + buildProductInoList() + " FROM target.products WHERE "
				+ ProductInfoColumns.PRODUCT_ID + "=?");
	}

	private String buildProductInoList() {
		Joiner joiner = Joiner.on(", ").skipNulls();
		// TODO Auto-generated method stub
		return joiner.join(ProductInfoColumns.PRODUCT_ID, ProductInfoColumns.PRODUCT_PRICE,
				ProductInfoColumns.PRODUCT_CURRENCY_TYPE);
	}

/*	@Bean
	private PreparedStatement updateProductPricingPreparedStatement() {
		return cassandraSession.prepare("UPDATE target.products SET" + ProductInfoColumns.PRODUCT_PRICE + "=? WHERE"
				+ ProductInfoColumns.PRODUCT_ID + "=?IF EXISTS");
	}
*/
}
