package com.ferguson.cs.product.task.stylyze;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("stylyze")
public class StylyzeSettings {
    private List<StylyzeInputProduct> inputData;

    public List<StylyzeInputProduct> getInputData() {
        return inputData;
    }

    public void setInputData(List<StylyzeInputProduct> inputData) {
        this.inputData = inputData;
    }

}