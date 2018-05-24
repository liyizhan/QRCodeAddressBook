package com.example.cyq.qrcodecard.dao;

import android.support.annotation.NonNull;

import com.example.cyq.qrcodecard.pojo.Contact;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by as on 2018/5/22.
 */

public class ContactDao {

    public static boolean addContact(@NonNull Contact contact){
        return contact.save();
    }

    public static boolean updateContact(@NonNull Contact contact){
        return contact.save();
    }

    public static int deleteContact(@NonNull Contact contact){
        if(contact.isSaved()){
            return contact.delete();
        }
        return 0;
    }

    public static List<Contact> getContacts(){
        return DataSupport.order("id asc").find(Contact.class);
    }

    public static Contact getContact(@NonNull Integer id){
        List<Contact> result=DataSupport.where("id = ?", String.valueOf(id)).find(Contact.class);
        if(result.isEmpty()){
            return null;
        }else {
            return result.get(0);
        }
    }

    public static int deleteAll(){
        return DataSupport.deleteAll(Contact.class);
    }

}
