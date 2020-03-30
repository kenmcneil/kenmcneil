package com.ferguson.cs.product.stream.participation.engine.model;

import java.util.Date;

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
