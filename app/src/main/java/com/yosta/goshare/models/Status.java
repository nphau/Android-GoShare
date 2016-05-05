package com.yosta.goshare.models;
/**
 * Created by dinhhieu on 3/26/16.
 */
public class Status extends Resource {


    public Status(String status, User owner) {
        super(owner, status);
        this.type = ResourceType.STATUS;
    }



}
