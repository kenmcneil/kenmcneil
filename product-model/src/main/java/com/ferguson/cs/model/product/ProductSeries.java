package com.ferguson.cs.model.product;

import java.io.Serializable;

import com.ferguson.cs.model.asset.ImageResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ProductSeries implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private String tagline;
	private ImageResource imageSplash;

}
