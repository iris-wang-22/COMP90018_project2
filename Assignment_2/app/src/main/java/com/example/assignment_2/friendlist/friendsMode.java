package com.example.assignment_2.friendlist;

public class friendsMode {
    private String username;
    private String gender;
    private String age;
    private String avatar;
    private Integer numberF;

    // number
    public Integer getNumber() {
        return numberF;
    }
    public void setNumber(Integer number) {
        this.numberF = number;
    }

    // username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // gender
    public String getGender() {
        if (gender==null){
            return "Gender: Secret";
        } else{
            return "Gender: " + gender;
        }
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    // age
    public String getAge(){
        if (age==null){
            return "Age: Secret";
        } else{
            return "Age: "+ age;
        }
    }
    public void setAge(String age){
        this.age = age;
    }

    // avatar
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
