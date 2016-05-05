package com.yosta.goshare.models;

/**
 * Created by dinhhieu on 3/26/16.
 */
public class Contact extends Resource  {

    private String phoneNumber;

    public Contact(User owner, String phoneNumber, String caption) {
        super(owner, caption);
        this.phoneNumber = phoneNumber;
        this.type = ResourceType.CONTACT;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
