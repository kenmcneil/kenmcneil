package com.ferguson.cs.product.stream.participation.engine.data;

import org.springframework.stereotype.Repository;

@Repository
public class ParticipationItemizedV1DaoImpl implements ParticipationItemizedV1Dao {
	private ParticipationItemizedV1Mapper participationItemizedV1Mapper;

	public ParticipationItemizedV1DaoImpl(ParticipationItemizedV1Mapper participationItemizedV1Mapper) {
		this.participationItemizedV1Mapper = participationItemizedV1Mapper;
	}
}
