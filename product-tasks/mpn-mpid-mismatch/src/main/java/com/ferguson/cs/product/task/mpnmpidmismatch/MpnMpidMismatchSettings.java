package com.ferguson.cs.product.task.mpnmpidmismatch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@ConfigurationProperties("mpn-mpid-mismatch")
public class MpnMpidMismatchSettings {
	private String reportOutputFolder;
	private String missingReportFilenamePrefix;
	private String mismatchReportFilenamePrefix;
	private String[] reportEmailList;
}
