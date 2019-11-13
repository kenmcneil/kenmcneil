package com.ferguson.cs.model.channel;

import com.ferguson.cs.utilities.IntMappedEnum;

/**
 * Channel Type used to categorize channels.
 *  <P>
 *  <LI>WEB_STORE		The product information is displayed and sold through one Ferguson's  collection of web properties.</LI>
 *  <LI>MARKETPALCE	The product information is displayed and sold through a third-party marketplace such as Amazon, Home Depot, etc</LI>
 *  <P>
 * @author tyler.vangorder
 *
 */
public enum ChannelType implements IntMappedEnum {
	WEB_STORE(1),
	MARKETPLACE(2);

	private int id;

	private ChannelType(int id) {
		this.id = id;
	}
	@Override
	public int getIntValue() {
		return id;
	}

}
