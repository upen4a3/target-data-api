package com.example.targetdataapi.utilities;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Following the rules Datastax has:
 * http://www.datastax.com/dev/blog/4-simple-rules-when-using-the-datastax-
 * drivers-for-cassandra
 * 
 * Added Keyspace name to each query rather than in the Cassandra Session (i.e.
 * "Use at most one Session per keyspace, or use a single Session and explicitly
 * specify the keyspace in your queries")
 */
@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration
{
    @SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CassandraConfig.class);

    @Override
    protected String getKeyspaceName() {
        return "target";
    }
 
    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = 
          new CassandraClusterFactoryBean();
        cluster.setContactPoints("127.0.0.1");
        cluster.setPort(9042);
        return cluster;
    }
 
    @Bean
    public CassandraMappingContext cassandraMapping() 
      throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }
   
    @Bean
    public Session cassandraSession() {
        return Cluster.builder().addContactPoints("127.0.0.1").withPort(9042).build().connect();
    }
}

