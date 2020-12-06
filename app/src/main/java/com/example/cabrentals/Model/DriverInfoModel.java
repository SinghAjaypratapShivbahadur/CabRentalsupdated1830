package com.example.cabrentals.Model;

public class DriverInfoModel {
    private String Firstname,Lastname,Mobile_Number,Email_Id;
    private  double rating;

    public DriverInfoModel() {

    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public void setEmail_Id(String email_Id) {
        Email_Id = email_Id;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
