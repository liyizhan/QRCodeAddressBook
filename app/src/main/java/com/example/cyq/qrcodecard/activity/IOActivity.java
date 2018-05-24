package com.example.cyq.qrcodecard.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cyq.qrcodecard.R;
import com.example.cyq.qrcodecard.dao.ContactDao;
import com.example.cyq.qrcodecard.pojo.Contact;
import com.example.cyq.qrcodecard.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class IOActivity extends AppCompatActivity {

    private Button mInputButton;
    private Button mOutputButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);
        mInputButton=(Button)findViewById(R.id.io_in);
        mInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in();
            }
        });
        mOutputButton=(Button)findViewById(R.id.io_out);
        mOutputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                out();
            }
        });
    }

    private void in(){
        ContactDao.deleteAll();
        FileInputStream in=null;
        BufferedReader reader=null;
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String line;
            String[] cols;
            while((line=reader.readLine())!=null){
                Contact contact=new Contact();
                cols=line.split(",");
                for(int j=0;j<6;j++){
                    if(cols[j].equals("null")){
                        cols[j]="";
                    }
                }
                contact.setName(cols[0]);
                contact.setPhone(cols[1]);
                contact.setEmail(cols[2]);
                contact.setQq(cols[3]);
                contact.setWeChat(cols[4]);
                contact.setAddress(cols[5]);
                ContactDao.addContact(contact);
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this,"导入失败",Toast.LENGTH_SHORT).show();
        }finally {
            try {
                if(reader!=null){
                    reader.close();
                    Toast.makeText(this,"导入成功",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void out(){
        StringBuilder sb=new StringBuilder();
        List<Contact> contacts= ContactDao.getContacts();
        for(Contact contact:contacts){
            String name=contact.getName();
            String phone=contact.getPhone();
            String email=contact.getEmail();
            String qq=contact.getQq();
            String weChat=contact.getWeChat();
            String address=contact.getAddress();
            if(StringUtils.isEmpty(name)){
                name="null";
            }
            if(StringUtils.isEmpty(phone)){
                phone="null";
            }
            if(StringUtils.isEmpty(email)){
                email="null";
            }
            if(StringUtils.isEmpty(weChat)){
                weChat="null";
            }
            if(StringUtils.isEmpty(qq)){
                qq="null";
            }
            if(StringUtils.isEmpty(address)){
                address="null";
            }
            sb.append(name);
            sb.append(",");
            sb.append(phone);
            sb.append(",");
            sb.append(email);
            sb.append(",");
            sb.append(qq);
            sb.append(",");
            sb.append(weChat);
            sb.append(",");
            sb.append(address);
            sb.append("\r\n");
        }
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("data", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(sb.toString());
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this,"导出失败",Toast.LENGTH_SHORT).show();
        }finally {
            try{
                if(writer!=null){
                    writer.close();
                    Toast.makeText(this,"导出成功",Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
