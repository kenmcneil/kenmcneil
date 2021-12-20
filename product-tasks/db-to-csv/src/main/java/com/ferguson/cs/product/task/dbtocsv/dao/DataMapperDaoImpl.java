package com.ferguson.cs.product.task.dbtocsv.dao;

import java.util.List;


public class DataMapperDaoImpl implements DataMapperDao {

	private final DataMapper dataMapper;

	public DataMapperDaoImpl(DataMapper dataMapper) {
		this.dataMapper = dataMapper;
	}

	// So here you would call out to your mapper to get the results of your SQL statement.
	// Or a simplier approach is to just create a MyBatisCursorItemReader in your job configuration.
	// If you do that you won't need the DataMapperDao or it's implementation (here).
	@Override
	public List<String> getSomeData() {
		//return Arrays.asList("Data1","Data2","Data3");

		return dataMapper.getSomeData();
	}
}