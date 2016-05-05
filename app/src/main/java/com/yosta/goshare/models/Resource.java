package com.yosta.goshare.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dinhhieu on 3/26/16.
 */
public abstract class Resource implements Serializable {

    protected String caption;
    protected Date dateCreation;
    protected Date dateStart;
    protected Date dateExpired;
    protected ResourceType type;
    protected User owner;
    protected ArrayList<Tag> tagList;
    protected MyLocation location;
    protected String note;
    protected String localFilePath;
    protected String directLink;

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDirectLink() {
        return directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }


    public Resource() {

    }

    public Resource(User owner) {
        this.owner = owner;
    }

    public Resource(User owner, String caption) {
        this.owner = owner;
        this.dateCreation = new Date();
        tagList = new ArrayList<>();
        this.caption = caption;
    }

    public void addTag(Tag tag) {
        tagList.add(tag);
    }

    public Boolean isExpired() {
        if (dateExpired == null) {
            return false;
        }
        if (dateExpired.compareTo(new Date()) > 0) {
            return false;
        }
        return true;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getUsetName() {
        return owner.getUsername();
    }
}