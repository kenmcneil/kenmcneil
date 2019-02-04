package com.ferguson.cs.product.task.stylyze;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("stylyze")
public class StylyzeSettings {
    private String localFilePath;

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
}