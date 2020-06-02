package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.BasicTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.SaleIdEffectTestLifecycle;

public class ItemizedDiscountScenariosIT extends ParticipationScenarioITBase {
	@Autowired
	protected BasicTestLifecycle basicTestLifecycle;

	@Autowired
	protected SaleIdEffectTestLifecycle saleIdEffectTestLifecycle;

//	@Autowired
//	ItemizedDiscountsTestLifecycle itemizedDiscountsTestLifecycle;
}
