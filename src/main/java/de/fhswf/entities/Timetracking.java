package de.fhswf.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Timetracking extends PanacheEntity {

    public String userId;

    public String fromTime;

    public String toTime;

    public Long project;

    public String description;

    public Date timestamp;

    public String total;
}
