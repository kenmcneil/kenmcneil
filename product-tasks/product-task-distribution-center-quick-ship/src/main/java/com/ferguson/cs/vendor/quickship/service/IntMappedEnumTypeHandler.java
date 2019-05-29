package com.ferguson.cs.vendor.quickship.service;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.ferguson.cs.utilities.IntMappedEnum;

public class IntMappedEnumTypeHandler<E extends IntMappedEnum> extends BaseTypeHandler<IntMappedEnum> {

	private final Class<E> type;
	private final Map<Integer, E> valueLookup;

	public IntMappedEnumTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null.");
		}
		boolean found = false;
		for (Class<?> inter : type.getInterfaces()) {
			if (inter.equals(IntMappedEnum.class)) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new IllegalArgumentException("Type must implement IntMappedEnum. Found: " + type);
		}
		this.type = type;
		valueLookup = new HashMap<>();
		for (E e : type.getEnumConstants()) {
			valueLookup.put(e.getIntValue(), e);
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		int intValue = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		}
		return intValueToEnum(intValue);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		int intValue = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		}
		return intValueToEnum(intValue);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		int intValue = cs.getInt(columnIndex);
		if (cs.wasNull()) {
			return null;
		}
		return intValueToEnum(intValue);
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int paramIndex,
	                                IntMappedEnum enumValue, JdbcType jdbcType) throws SQLException {
		ps.setInt(paramIndex, enumValue.getIntValue());
	}

	public E intValueToEnum(int intValue) {
		E enumValue = valueLookup.get(intValue);
		if (enumValue != null) {
			return enumValue;
		}
		throw new IllegalArgumentException(intValue + " is not a valid mapped integer value for " + type.getSimpleName());
	}
}
