package com.example.cyq.qrcodecard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.cyq.qrcodecard.R;
import com.example.cyq.qrcodecard.pojo.Contact;
import com.example.cyq.qrcodecard.util.QRCodeUtil;
import com.google.gson.Gson;


public class ShareActivity extends AppCompatActivity {
    private Contact mContact;
    private ImageView mQRCode;

    public static final String QR_CODE="qr_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mQRCode=(ImageView)findViewById(R.id.qrcode);
        Intent intent=getIntent();
        mContact=(Contact)intent.getSerializableExtra(QR_CODE);
        Gson gson=new Gson();
        String content=gson.toJson(mContact,Contact.class);
        Bitmap bitmap= QRCodeUtil.createBitmap(content);
        mQRCode.setImageBitmap(bitmap);
    }

}
