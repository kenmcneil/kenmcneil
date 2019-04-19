package com.ferguson.cs.product.task.stylyze;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;

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