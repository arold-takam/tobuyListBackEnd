package com.tblGroup.toBuyList.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name= "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="action")
    private String action;

    @Column(name="description")
    private String description;

    @Column(name= "date_action")
    private Date dateAction;

    @ManyToOne()
    @JoinColumn(name="client_id")
    private Client client;

    public History(String action, String description, Date dateAction, Client client) {
        this.action = action;
        this.description = description;
        this.dateAction = dateAction;
        this.client = client;
    }

    public History() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateAction() {
        return dateAction;
    }

    public void setDateAction(Date date_action) {
        this.dateAction = date_action;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
