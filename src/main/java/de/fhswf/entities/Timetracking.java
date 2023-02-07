package de.fhswf.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Timetracking extends PanacheEntity {

    public String userId;

    public Date fromTime;

    public Date toTime;

    public String workingpackage;

    public String description;
}
