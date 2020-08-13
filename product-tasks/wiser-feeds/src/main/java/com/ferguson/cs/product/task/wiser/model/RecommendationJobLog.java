package com.ferguson.cs.product.task.wiser.model;

import java.util.Date;

public class RecommendationJobLog {
	private Date runDateTime;
	private Boolean successful;
	private RecommendationJobFailureCause recommendationJobFailureCause;

	public Date getRunDateTime() {
		return runDateTime;
	}

	public void setRunDateTime(Date runDateTime) {
		this.runDateTime = runDateTime;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public RecommendationJobFailureCause getRecommendationJobFailureCause() {
		return recommendationJobFailureCause;
	}

	public void setRecommendationJobFailureCause(RecommendationJobFailureCause recommendationJobFailureCause) {
		this.recommendationJobFailureCause = recommendationJobFailureCause;
	}
}
