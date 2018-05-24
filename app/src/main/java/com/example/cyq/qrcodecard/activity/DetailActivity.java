package com.example.cyq.qrcodecard.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cyq.qrcodecard.R;
import com.example.cyq.qrcodecard.dao.ContactDao;
import com.example.cyq.qrcodecard.pojo.Contact;
import com.example.cyq.qrcodecard.util.Checker;
import com.example.cyq.qrcodecard.util.ScreenSizeUtils;
import com.example.cyq.qrcodecard.util.StringUtils;
import com.example.cyq.qrcodecard.util.UUIDUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by as on 2018/5/17.
 */

public class DetailActivity extends AppCompatActivity  {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final String CONTACT_ID="contact_id";
    public static final String PAGE_FROM_SITE_ID="page_from_site_id";

    public static final int MY_PAGE=0;
    public static final int EXIST_PAGE=1;
    public static final int NEW_PAGE=3;

    private int mPageFromSiteId;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mHeaderBgImageView;

    private FloatingActionButton mDialFAB;
    private FloatingActionButton mShareFAB;
    private FloatingActionButton mImportFAB;
    private FloatingActionButton mDeleteFAB;

    private CircleImageView mHeadPortraitImageView;
    private EditText mNameEditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;
    private EditText mQQEditText;
    private EditText mWeChatEditText;
    private EditText mAddressEditText;

    private CircleImageView mSubmitImageView;
    private Uri mImageUri;

    private int mContactId;
    private Contact mContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        handleIntent();
    }

    private void initView(){
        mToolbar=(Toolbar)findViewById(R.id.detail_header_title);
        mCollapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.detail_collapsing_toolbar);
        mHeaderBgImageView=(ImageView)findViewById(R.id.detail_header_img);
        setSupportActionBar(mToolbar);
        mDialFAB=(FloatingActionButton)findViewById(R.id.detail_dial);
        mShareFAB=(FloatingActionButton)findViewById(R.id.detail_share);
        mImportFAB=(FloatingActionButton)findViewById(R.id.detail_input);
        mDeleteFAB=(FloatingActionButton)findViewById(R.id.detail_delete);

        mHeadPortraitImageView=(CircleImageView)findViewById(R.id.detail_head_portrait);
        mHeadPortraitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        mNameEditText=(EditText)findViewById(R.id.detail_name);
        mPhoneEditText=(EditText)findViewById(R.id.detail_phone);
        mEmailEditText=(EditText)findViewById(R.id.detail_emial);
        mQQEditText=(EditText)findViewById(R.id.detail_qq);
        mWeChatEditText=(EditText)findViewById(R.id.detail_wx);
        mAddressEditText=(EditText)findViewById(R.id.detail_address);

        mSubmitImageView=(CircleImageView)findViewById(R.id.detail_submit);
        mSubmitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void handleIntent(){
        Intent intent=getIntent();
        mPageFromSiteId=intent.getIntExtra(PAGE_FROM_SITE_ID,0);

        switch (mPageFromSiteId){
            case MY_PAGE:
                mCollapsingToolbarLayout.setTitle(getString(R.string.detail_person_info));
                Glide.with(this).load(R.drawable.nav_header_bg).into(mHeaderBgImageView);
                mContactId=intent.getIntExtra(CONTACT_ID,0);
                if(mContactId==0){
                    finish();
                }
                mContact=initData(mContactId);
                updateView(mContact);
                mDeleteFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beforeDelete(0);
                    }
                });
                break;
            case EXIST_PAGE:
                mContactId=intent.getIntExtra(CONTACT_ID,0);
                if(mContactId==0){
                    finish();
                }
                mContact=initData(mContactId);
                mCollapsingToolbarLayout.setTitle(mContact.getName());
                Glide.with(this).load(randomHeaderBgId()).into(mHeaderBgImageView);
                updateView(mContact);
                mDeleteFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beforeDelete(1);
                    }
                });
                break;
            case NEW_PAGE:
                mCollapsingToolbarLayout.setTitle("新联系人");
                Glide.with(this).load(randomHeaderBgId()).into(mHeaderBgImageView);
                mDeleteFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beforeDelete(2);
                    }
                });
                mContact=new Contact();
                break;
        }
        mDialFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dial();
            }
        });
        mShareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailActivity.this,ShareActivity.class);
                intent.putExtra(ShareActivity.QR_CODE,mContact);
                startActivity(intent);
            }
        });
        mImportFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(DetailActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class)
                        .initiateScan();
            }
        });
    }

    private void openDialog(){
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_bottom, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.9f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog.show();
        Button takePhoto=(Button)view.findViewById(R.id.dialog_take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage=new File(getExternalCacheDir(), UUIDUtils.shortUUID()+".jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    mImageUri= FileProvider.getUriForFile(DetailActivity.this,
                            "com.example.cyq.qrcodecard.fileprovider",outputImage);
                }else{
                    mImageUri=Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
                dialog.dismiss();
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        Button album=(Button)view.findViewById(R.id.dialog_album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DetailActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    dialog.dismiss();
                }else{
                    openAlbum();
                    dialog.dismiss();
                }
            }
        });
        Button cancel=(Button)view.findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    private void dial(){
        if(!StringUtils.isEmpty(mContact.getPhone())){
            Intent intent=new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+mContact.getPhone()));
            startActivity(intent);
        }else {
            Toast.makeText(DetailActivity.this,"暂无保存电话号码，无法拨号",Toast.LENGTH_SHORT).show();
        }
    }

    private void save(){
        String name=mNameEditText.getText().toString();
        String phone=mPhoneEditText.getText().toString();
        String email=mEmailEditText.getText().toString();
        String qq=mQQEditText.getText().toString();
        String weChat=mWeChatEditText.getText().toString();
        String address=mAddressEditText.getText().toString();

        String error="";
        if(!name.equals("")&&!Checker.NAME.matcher(name).matches()){
            error+="名字应为2到20位的中文、英文或空格字符\n";
        }
        if(!phone.equals("")&&!Checker.PHONE.matcher(phone).matches()){
            error+="电话号码应为国内手机号码或带区号座机号码\n";
        }
        if(!email.equals("")&&!Checker.EMAIL.matcher(email).matches()){
            error+="电子邮箱应为正确的电子邮箱格式\n";
        }
        if(!qq.equals("")&&!Checker.QQ.matcher(qq).matches()){
            error+="QQ号暂时支持12位QQ号\n";
        }
        if(!weChat.equals("")&&!Checker.WE_CHAT.matcher(weChat).matches()){
            error+="微信号应为1到40位的英文、数字、下划线或横杠字符\n";
        }
        if(!address.equals("")&&!Checker.ADDRESS.matcher(address).matches()){
            error+="地址应为1到50位的英文、数字、中文、下划线或横杠字符";
        }
        if(!error.equals("")){
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
            return;
        }

        mContact.setName(name);
        mContact.setPhone(phone);
        mContact.setEmail(email);
        mContact.setQq(qq);
        mContact.setWeChat(weChat);
        mContact.setAddress(address);
        ContactDao.updateContact(mContact);
        Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
        if(mPageFromSiteId==NEW_PAGE){
            DetailActivity.this.finish();
        }
    }

    private void updateView(Contact contact){
        String name=contact.getName();
        String phone=contact.getPhone();
        String email=contact.getEmail();
        String qq=contact.getQq();
        String weChat=contact.getWeChat();
        String address=contact.getAddress();
        String imgPath=contact.getImgPath();
        if(name!=null&&!name.equals("")){
            mNameEditText.setText(name);
        }
        if(phone!=null&&!phone.equals("")){
            mPhoneEditText.setText(phone);
        }
        if(email!=null&&!email.equals("")){
            mEmailEditText.setText(email);
        }
        if(qq!=null&&!qq.equals("")){
            mQQEditText.setText(qq);
        }
        if(weChat!=null&&!weChat.equals("")){
            mWeChatEditText.setText(weChat);
        }
        if(address!=null&&!address.equals("")){
            mAddressEditText.setText(address);
        }
        if(imgPath!=null&&!imgPath.equals("")){
            Bitmap bitmap=BitmapFactory.decodeFile(imgPath);
            mHeadPortraitImageView.setImageBitmap(bitmap);
            //Uri uri=Uri.fromFile(new File(imgPath));
            //Glide.with(this).load(uri).into(mHeadPortraitImageView);
        }
    }

    private Contact initData(int id){
        return ContactDao.getContact(id);
    }

    private int randomHeaderBgId(){
        Random random=new Random();
        int headerBgId=random.nextInt(3)+1;
        int picId=0;
        switch (headerBgId){
            case 1:
                picId=R.drawable.random1;
                break;
            case 2:
                picId=R.drawable.random2;
                break;
            case 3:
                picId=R.drawable.random3;
                break;
            default:break;
        }
        return picId;
    }

    private void beforeDelete(final int id){
        String message="";
        switch (id){
            case 0:message="您确定要清空您的个人信息吗?";break;
            case 1:message="您确定要删除本条记录吗?";break;
            case 2:message="您确定要放弃编辑本条记录吗?";break;
            default:return;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告")//设置对话框的标题
                .setMessage(message)//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (id){
                            case 0:empty();break;
                            case 2:DetailActivity.this.finish();break;
                            case 1:ContactDao.deleteContact(mContact);DetailActivity.this.finish();break;
                            default:return;
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void empty(){
        mContact.setImgPath("");
        mHeadPortraitImageView.setImageResource(R.drawable.nav_head_portrait);
        mContact.setAddress("");
        mAddressEditText.setText("");
        mContact.setName("");
        mNameEditText.setText("");
        mContact.setPhone("");
        mPhoneEditText.setText("");
        mContact.setWeChat("");
        mWeChatEditText.setText("");
        mContact.setQq("");
        mQQEditText.setText("");
        mContact.setEmail("");
        mEmailEditText.setText("");
        ContactDao.updateContact(mContact);
        Toast.makeText(this,"清空成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK) {
                    Glide.with(DetailActivity.this).load(mImageUri).into(mHeadPortraitImageView);
                    mContact.setImgPath(mImageUri.getPath());
                    ContactDao.updateContact(mContact);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case IntentIntegrator.REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
                    if(intentResult != null) {
                        if(intentResult.getContents() == null) {
                            Toast.makeText(this,"扫描的内容为空",Toast.LENGTH_LONG).show();
                        } else {
                            String scanResult = intentResult.getContents();
                            Gson gson=new Gson();
                            try {
                                Contact contact=gson.fromJson(scanResult,Contact.class);
                                mNameEditText.setText(contact.getName());
                                mPhoneEditText.setText(contact.getPhone());
                                mEmailEditText.setText(contact.getEmail());
                                mQQEditText.setText(contact.getQq());
                                mWeChatEditText.setText(contact.getWeChat());
                                mAddressEditText.setText(contact.getAddress());
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                Toast.makeText(this,"无法加载扫描内容",Toast.LENGTH_LONG).show();
                            }


                        }
                    } else {
                        super.onActivityResult(requestCode,resultCode,data);
                    }
                }
            default:break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(DetailActivity.this,"拒绝权限将无法使用该功能",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.media.downloads.documents".
                    equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            mHeadPortraitImageView.setImageBitmap(bitmap);
            mContact.setImgPath(imagePath);
            ContactDao.updateContact(mContact);
        }else {
            Toast.makeText(DetailActivity.this,"无法加载头像",Toast.LENGTH_SHORT).show();
        }
    }
}
