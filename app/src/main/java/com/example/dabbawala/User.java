package com.example.dabbawala;

/**
 * Created by inspirin on 10/18/2017.
 */

public class User {
    String name,userid,url,phone,email,vehicle_id,dob,password,token_id,usertype;
public User(){

}
    public User(String name, String userid, String url, String phone, String email, String vehicle_id, String dob, String password,String token_id,String usertype) {
        this.name = name;
        this.userid = userid;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.vehicle_id = vehicle_id;
        this.dob = dob;
        this.password = password;
        this.token_id = token_id;
        this.usertype = usertype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
