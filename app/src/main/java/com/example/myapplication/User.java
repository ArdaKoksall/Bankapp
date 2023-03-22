package com.example.myapplication;

public class User {
    String name, surname, nickname, password, imageloc;
    int wallet;

    public  User(){
    }

    public User(String name, String surname, String nickname, String password, String imageloc, int wallet){
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.password = password;
        this.wallet = wallet;
        this.imageloc = imageloc;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getImageloc(){return imageloc;}

    public int getWallet() {
        return wallet;
    }

}
