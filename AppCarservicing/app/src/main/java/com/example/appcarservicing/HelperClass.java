package com.example.appcarservicing;

public class HelperClass {
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

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }


    String name,email,username,password,phone,car;


    public HelperClass(String name, String email, String username, String password, String phone, String car) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.car = car;

    }
}
