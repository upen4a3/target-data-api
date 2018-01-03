package com.example.targetdataapi.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.mapping.Mapper;
import com.example.targetdataapi.dao.ProductInfoDAO;
import com.example.targetdataapi.entity.ProductInfo;
import com.example.targetdataapi.utilities.ProductInfoColumns;

@Component
public class ProductDAOInfoImpl implements ProductInfoDAO {
	
	


	private static final Logger LOG = LoggerFactory.getLogger(ProductDAOInfoImpl.class);
	@Autowired
	private Session cassandraSession;

	@Autowired
	private PreparedStatement getProductInfoPreparedStatement;

	@Autowired
	private Mapper<ProductInfo> productInfoMapper;

	//@Autowired
	//private PreparedStatement updateProductPricingPreparedStatement;

	@Override
	public ProductInfo getProductInfo(int id) {
		LOG.info("Enterring getProductInfoMethod----");
		ResultSet result = cassandraSession.execute(getProductInfoPreparedStatement.bind(id));
		return productInfoMapper.map(result).one();
	}

	
	private Update updateProductPriceInfo(int id, float price,String currencyType) 
	{

		LOG.info("Enterring updateProductPriceInfoMethod----");
		Update update = QueryBuilder.update("target","products");
		update.with(QueryBuilder.set(ProductInfoColumns.PRODUCT_PRICE, price));
		update.with(QueryBuilder.set(ProductInfoColumns.PRODUCT_CURRENCY_TYPE, currencyType));
		
		update.where(QueryBuilder.eq(ProductInfoColumns.PRODUCT_ID, id));
		
		
		return update;
		
	}
	@Override
	public boolean updateProductPrice(int id, float price,String currencyType)
	{

		LOG.info("Enterring updateProductPrice----");
		Update updateProductPriceInfo = updateProductPriceInfo(id,price,currencyType);
		ResultSet result = cassandraSession.execute(updateProductPriceInfo);
		return result.wasApplied();
		
	}

	
	

}
