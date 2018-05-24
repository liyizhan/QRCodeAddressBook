package com.example.cyq.qrcodecard.util;

import com.example.cyq.qrcodecard.pojo.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by as on 2018/5/17.
 */

public class Test {

    public static List<Contact> getRandomContact(){
        Random random=new Random();
        List<Contact> contacts=new ArrayList<>();
        int size=random.nextInt(100)+1;
        for(int i=0;i<size;i++){
            Contact contact=new Contact();
            contact.setId(i+1);
            if(i%2==0){
                contact.setName(String.valueOf(random.nextInt(100)));
            }
            if(i%3==0){
                contact.setPhone(String.valueOf(random.nextInt(100)));
            }
            if(i%4==0){
                contact.setEmail(String.valueOf(random.nextInt(100)));
            }
            if(i%5==0){
                contact.setQq(String.valueOf(random.nextInt(100)));
            }
            if(i%6==0){
                contact.setWeChat(String.valueOf(random.nextInt(100)));
            }
            contacts.add(contact);
        }
        return contacts;
    }
}
