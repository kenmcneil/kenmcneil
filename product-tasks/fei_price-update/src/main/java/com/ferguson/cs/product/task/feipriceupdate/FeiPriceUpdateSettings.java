package com.ferguson.cs.product.task.feipriceupdate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@ConfigurationProperties("fei-price-update")
@Component
public class FeiPriceUpdateSettings {
	
	private String temporaryFilePath;
	private String inputFilePath;
	private String tempTableName;
	
}
