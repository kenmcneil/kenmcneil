package com.ferguson.cs.product.task.dy.domain;

public enum Site {
	VENTINGDIRECT(1),
	VENTINGPIPE(2),
	FAUCETDIRECT(4),
	HANDLESETS(5),
	LIGHTINGDIRECT(15),
	PULLSDIRECT(33),
	BUILD(82),
	FAUCET(84),
	LIGHTINGSHOWPLACE(85),
	COMPACTAPPLIANCE(89),
	WINECOOLERDIRECT(90),
	KEGERATOR(91),
	ICEMAKERDIRECT(92),
	ALLERGYANDAIR(94);

	private Integer value;

	Site(Integer value) {
		this.value = value;
	}

	public Integer getSiteId() {
		return this.value;
	}
}
