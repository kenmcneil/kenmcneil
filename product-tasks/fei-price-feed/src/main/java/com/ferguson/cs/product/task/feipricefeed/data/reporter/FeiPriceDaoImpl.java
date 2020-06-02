package com.ferguson.cs.product.task.feipricefeed.data.reporter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.feipricefeed.model.DeprioritizedBrandView;

@Repository
public class FeiPriceDaoImpl implements FeiPriceDao {

	private final FeiPriceMapper feiPriceMapper;

	public FeiPriceDaoImpl(FeiPriceMapper feiPriceMapper) {
		this.feiPriceMapper = feiPriceMapper;
	}

	@Override
	public List<DeprioritizedBrandView> getDeprioritizedBrands() {
		return feiPriceMapper.getDeprioritizedBrands();
	}
}
