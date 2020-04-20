package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;

/**
 * This schedule object with a start date and end date is part of a Participation record.
 * The start and end dates indicate when to activate the effects of a Participation, and when
 * to deactivate them.
 */
public class ParticipationItemSchedule {
    private Date from;
    private Date to;

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }
}
