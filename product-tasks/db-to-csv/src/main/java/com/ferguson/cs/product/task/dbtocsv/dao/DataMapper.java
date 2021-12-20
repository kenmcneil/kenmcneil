package com.ferguson.cs.product.task.dbtocsv.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface DataMapper {

	List<String> getSomeData();
}
