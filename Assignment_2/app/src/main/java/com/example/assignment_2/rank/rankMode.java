package com.example.assignment_2.rank;

public class rankMode {
    private String username;
    private int steps;
    private String avatar;
    private Integer rank;

    // number
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer number) {
        this.rank= rank;
    }

    // username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    // age
    public int getSteps(){
            return steps;
    }
    public void setSteps(int steps){
        this.steps = steps;
    }

    // avatar
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
