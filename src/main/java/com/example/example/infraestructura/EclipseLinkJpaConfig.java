package com.example.example.infraestructura;

/**
 * Created by max on 09/06/17.
 */

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


//@ConfigurationProperties(prefix = "spring.jpa.eclipselink")
@Configuration
public class EclipseLinkJpaConfig extends JpaBaseConfiguration {

    private Boolean weaving;

    private String ddlGeneration;

    @Autowired
    public EclipseLinkJpaConfig(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
        return adapter;
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("eclipselink.weaving","false");
        map.put("eclipselink.ddl-generation","create-or-extend-tables");
        map.put("eclipselink.logging.level.sql","FINE");
        return map;
    }

    /*public Boolean getWeaving() {
        return weaving;
    }

    public void setWeaving(Boolean weaving) {
        this.weaving = weaving;
    }

    public String getDdlGeneration() {
        return ddlGeneration;
    }

    public void setDdlGeneration(String ddlGeneration) {
        this.ddlGeneration = ddlGeneration;
    }*/


}
