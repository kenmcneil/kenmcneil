package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;

@Repository
public class ManhattanInboundDaoImpl implements ManhattanInboundDao {

	private ManhattanInboundMapper manhattanInboundMapper;

	@Autowired
	public void setManhattanInboundMapper(ManhattanInboundMapper manhattanInboundMapper) {
		this.manhattanInboundMapper = manhattanInboundMapper;
	}

	@Override
	public List<ManhattanIntakeJob> getManhattanIntakeJobs() {
		return manhattanInboundMapper.getManhattanIntakeJobs();
	}

	@Override
	public void updateManhattanIntakeJobStatus(ManhattanIntakeJob manhattanIntakeJob) {
		manhattanInboundMapper.updateManhattanIntakeJobStatus(manhattanIntakeJob);
	}
}
