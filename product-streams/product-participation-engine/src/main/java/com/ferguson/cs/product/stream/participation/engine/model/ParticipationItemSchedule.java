package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This schedule object with a start date and end date is part of a Participation record.
 * The start and end dates indicate when to activate the effects of a Participation, and when
 * to deactivate them.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationItemSchedule {
    private Date from;
    private Date to;
}
