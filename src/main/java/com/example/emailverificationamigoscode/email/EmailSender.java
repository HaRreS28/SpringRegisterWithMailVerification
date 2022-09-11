package com.example.emailverificationamigoscode.email;

public interface EmailSender {
    void send(String to, String email);
//    You dont need this, but is nice cause you can have for instance discord implementations what is really nice!
}
