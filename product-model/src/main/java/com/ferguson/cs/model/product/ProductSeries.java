package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Version;

import com.ferguson.cs.model.Auditable;
import com.ferguson.cs.model.asset.ImageResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeries implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String description;
	private String tagline;
	private ImageResource imageSplash;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;

	@Version
	private Integer version;


}
