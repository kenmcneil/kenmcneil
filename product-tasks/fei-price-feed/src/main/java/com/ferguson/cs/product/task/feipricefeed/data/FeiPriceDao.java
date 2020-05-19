package com.ferguson.cs.product.task.feipricefeed.data;

import java.util.List;

import com.ferguson.cs.product.task.feipricefeed.model.DeprioritizedBrandView;

public interface FeiPriceDao {
	List<DeprioritizedBrandView> getDeprioritizedBrands();
}
