package com.example.appcarservicing;

public class HelperClass1 {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public HelperClass1(String name, String email, String username, String password, String phone, String workshop) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.workshop = workshop;
    }

    String name,email,username,password,phone,workshop;
}
