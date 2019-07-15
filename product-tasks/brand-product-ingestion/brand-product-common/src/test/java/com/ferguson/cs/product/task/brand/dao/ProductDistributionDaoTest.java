package com.ferguson.cs.product.task.brand.dao;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.task.brand.ProductDistributionCommonAutoConfiguration;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.JsonReference;
import com.ferguson.cs.product.task.brand.model.JsonType;
import com.ferguson.cs.product.task.brand.model.SystemSource;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.spring.LazyInitBeanFactoryPostProcessor;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes={ProductDistributionCommonAutoConfiguration.class, ProductDistributionDaoTest.DataAccessTestConfiguration.class})
@TestPropertySource(locations = {"classpath:application-unit.yml"})
@Transactional
public class ProductDistributionDaoTest extends BaseTest {

	@Autowired
	private ProductDistributionDao productDistributionDao;
	private ObjectMapper objectMapper = new ObjectMapper();

	private int systemSourceId = 0;

	@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
	protected static class DataAccessTestConfiguration {
		// This post processor wraps all beans in the context with lazy-loading proxy, this can dramatically improve the startup time of
		// individual tests, as only the beans that are references will be initialized.
		@Bean
		static public BeanFactoryPostProcessor lazyBeanPostProcessor() {
			return new LazyInitBeanFactoryPostProcessor();
		}
	}

	@Before
	public void testInsertSystemSource() throws Exception {
		SystemSource systemSource = new SystemSource();
		systemSource.setSourceName("UnitTest");
		systemSource.setActiveProductsFetched(1201);
		systemSource.setObsoleteProductsFetched(5000);
		productDistributionDao.upsertSystemSource(systemSource);
		assertNotNull("The id cannot be null", systemSource.getId());
		systemSourceId = systemSource.getId();

	}

	@Test
	public void testInsertProducts() throws Exception {
		List<BrandProduct> products = new ArrayList<>();

		BrandProduct product = new BrandProduct();
		product.setProductName("TestProduct");
		product.setManufacturer("GE");
		product.setSystemSourceId(systemSourceId);
		product.setCategoryName("Electric Cooking");
		product.setColor("Stainless Steel");
		product.setBrandName("GE Cafe Series");
		product.setProductId("1119");
		product.setDateAvailable(new Date());
		product.setDateUpdated(new Date());
		product.setIsActive(true);
		List<JsonReference> jsonReferences = new ArrayList<>();
		product.setJsonReferences(jsonReferences);

		// Add Finish Json
		SortedMap<String, Object> deSerializedState = new TreeMap<>();
		deSerializedState.put("Color", "Stainless Steel");
		deSerializedState.put("Appearance", "Stainless Steel");
		deSerializedState.put("AppearanceImage", "Stainless Steel");
		deSerializedState.put("ImageNumber", "38077");
		deSerializedState.put("DataNumber", "80");
		JsonReference jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.FINISH);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		// Add Brand Json
		deSerializedState = new TreeMap<>();
		deSerializedState.put("BrandCopy", "Updated1119 One look at the GE Café™ kitchen and you’ll feel as if you’ve been transported behind the scenes of your favorite casual dining experience.");
		deSerializedState.put("DataId", "Stainless Steel");
		deSerializedState.put("DisplayName", "GE Cafe Series");
		deSerializedState.put("ImageName", "12_ge_cafeseries_k.gif");
		deSerializedState.put("Name", "GE Cafe Series");
		deSerializedState.put("SubHeading", "Food & Friends");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.BRAND);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);
		products.add(product);

		product = new BrandProduct();
		product.setProductName("TestProduct2");
		product.setManufacturer("GE");
		product.setSystemSourceId(systemSourceId);
		product.setCategoryName("Electric Cooking Test");
		product.setColor("Stainless Steel Test");
		product.setBrandName("GE Cafe Series Test");
		product.setProductId("11129");
		product.setDateAvailable(new Date());
		product.setDateUpdated(new Date());
		product.setIsActive(true);
		jsonReferences = new ArrayList<>();
		product.setJsonReferences(jsonReferences);

		// Add Finish Json
		deSerializedState = new TreeMap<>();
		deSerializedState.put("Color", "Stainless SteelTest");
		deSerializedState.put("Appearance", "Stainless Steel2");
		deSerializedState.put("AppearanceImage", "Stainless Steel");
		deSerializedState.put("ImageNumber", "3807766");
		deSerializedState.put("DataNumber", "80");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.FINISH);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		// Add Brand Json
		deSerializedState = new TreeMap<>();
		deSerializedState.put("BrandCopy", "One look at the GE Café™ kitchen and you’ll feel as if you’ve been transported behind the scenes of your favorite casual dining experience.");
		deSerializedState.put("DataId", "Stainless Steel Test");
		deSerializedState.put("DisplayName", "GE Cafe Series");
		deSerializedState.put("ImageName", "12_ge_cafeseries_k.gif");
		deSerializedState.put("Name", "GE Cafe Series");
		deSerializedState.put("SubHeading", "Food & Friends");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.BRAND);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		products.add(product);

		product = new BrandProduct();
		product.setProductName("TestProduct3");
		product.setManufacturer("GE");
		product.setSystemSourceId(systemSourceId);
		product.setCategoryName("Electric Cooking Test");
		product.setColor("Stainless Steel Test");
		product.setBrandName("GE Cafe Series Test");
		product.setProductId("11129-New");
		product.setDateAvailable(new Date());
		product.setDateUpdated(new Date());
		product.setIsActive(true);
		jsonReferences = new ArrayList<>();
		product.setJsonReferences(jsonReferences);

		// Add Finish Json
		deSerializedState = new TreeMap<>();
		deSerializedState.put("Color", "Stainless SteelTest");
		deSerializedState.put("Appearance", "Stainless Steel2");
		deSerializedState.put("AppearanceImage", "Stainless Steel");
		deSerializedState.put("ImageNumber", "3807766");
		deSerializedState.put("DataNumber", "80");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.FINISH);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		// Add Brand Json
		deSerializedState = new TreeMap<>();
		deSerializedState.put("BrandCopy", "One look at the GE Café™ kitchen and you’ll feel as if you’ve been transported behind the scenes of your favorite casual dining experience.");
		deSerializedState.put("DataId", "Stainless Steel Test");
		deSerializedState.put("DisplayName", "GE Cafe Series");
		deSerializedState.put("ImageName", "12_ge_cafeseries_k.gif");
		deSerializedState.put("Name", "GE Cafe Series");
		deSerializedState.put("SubHeading", "Food & Friends");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.BRAND);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		deSerializedState = new TreeMap<>();
		deSerializedState.put("Searchable", "Yes");
		jsonReference = new JsonReference();
		jsonReference.setJsonType(JsonType.ATTRIBUTE);
		jsonReference.setJsonString(objectMapper.writeValueAsString(deSerializedState));
		jsonReferences.add(jsonReference);

		products.add(product);

		productDistributionDao.upsertProducts(products);
		for (BrandProduct brandProduct : products) {
			assertNotNull("The id cannot be null", brandProduct.getId());
		}

	}

}
