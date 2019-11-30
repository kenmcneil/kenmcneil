package com.ferguson.cs.product.task.dy.domain;

public enum Sites {
	VENTING_DIRECT(1),
	VENTING_PIPE(2),
	FAUCET_DIRECT(4),
	HANDLE_SETS(5),
	LIGHTING_DIRECT(15),
	PULLS_DIRECT(33),
	BUILD(82),
	FAUCET(84),
	LIGHTING_SHOWPLACE(85),
	LIVING_DIRECT(88),
	COMPACT_APPLIANCE(89),
	WINE_COOLER_DIRECT(90),
	KEGERATOR(91),
	ICE_MAKER_DIRECT(92),
	ALLERGY_AND_AIR(94);

	private Integer value;

	Sites(Integer value) {
		this.value = value;
	}

	public Integer getSiteId() {
		return this.value;
	}
}
