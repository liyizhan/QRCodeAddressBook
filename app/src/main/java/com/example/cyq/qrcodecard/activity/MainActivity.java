package com.example.cyq.qrcodecard.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cyq.qrcodecard.R;
import com.example.cyq.qrcodecard.adapter.ContactAdapter;
import com.example.cyq.qrcodecard.dao.ContactDao;
import com.example.cyq.qrcodecard.pojo.Contact;
import com.example.cyq.qrcodecard.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private EditText mSearchEditText;
    private ImageView mSearchBackImageView;
    private ImageView mSearchImageView;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddFAB;
    private FloatingActionButton mTopFAB;
    private TextView mNavName;
    private TextView mNavContact;
    private CircleImageView mNavHead;

    private List<Contact> mContacts;
    private ContactAdapter mAdapter;
    private List<Contact> mOldContacts;

    private Contact mMyContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView(){
        mToolbar=(Toolbar)findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolbar);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.main_drawer_layout);
        View nav= LayoutInflater.from(this).inflate(R.layout.nav_header,mDrawerLayout,false);
        mNavigationView=(NavigationView)findViewById(R.id.main_nav_view);
        mNavigationView.addHeaderView(nav);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.main_setting);
        }
        mNavigationView.setCheckedItem(R.id.nav_person_detail);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_person_detail:
                        Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                        intent.putExtra(DetailActivity.CONTACT_ID,mMyContact.getId());
                        mDrawerLayout.closeDrawer(Gravity.START);
                        startActivity(intent);
                        break;
                    case R.id.nav_data_io:
                        Intent intent1=new Intent(MainActivity.this,IOActivity.class);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        startActivity(intent1);
                        break;
                    default:break;
                }
                return true;
            }
        });
        mSearchEditText=(EditText)findViewById(R.id.main_search_edit_text);
        mSearchBackImageView=(ImageView)findViewById(R.id.main_search_back);
        mSearchBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBack();
            }
        });
        mSearchImageView=(ImageView)findViewById(R.id.main_search_button);
        mSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        mRecyclerView=(RecyclerView)findViewById(R.id.main_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ContactAdapter(mContacts);
        mRecyclerView.setAdapter(mAdapter);
        mTopFAB=(FloatingActionButton)findViewById(R.id.main_top);
        mAddFAB=(FloatingActionButton)findViewById(R.id.main_add);
        mTopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
        mAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.PAGE_FROM_SITE_ID,DetailActivity.NEW_PAGE);
                startActivity(intent);
            }
        });
        mNavContact=(TextView)nav.findViewById(R.id.nav_contact);
        mNavName=(TextView)nav.findViewById(R.id.nav_person_name);
        mNavHead=(CircleImageView)nav.findViewById(R.id.nav_head_portrait);
        updateMySelf();
    }

    private void updateMySelf(){
        if(!StringUtils.isEmpty(mMyContact.getPhone())){
            mNavContact.setText(mMyContact.getPhone());
        }else if(!StringUtils.isEmpty(mMyContact.getEmail())){
            mNavContact.setText(mMyContact.getEmail());
        }else if(!StringUtils.isEmpty(mMyContact.getQq())){
            mNavContact.setText(mMyContact.getQq());
        }else if(!StringUtils.isEmpty(mMyContact.getWeChat())){
            mNavContact.setText(mMyContact.getWeChat());
        }else{
            mNavContact.setText("暂无联系方式");
        }
        if(!StringUtils.isEmpty(mMyContact.getName())){
            mNavName.setText(mMyContact.getName());
        }else{
            mNavName.setText("暂无姓名");
        }
        if(!StringUtils.isEmpty(mMyContact.getImgPath())){
            Uri uri=Uri.fromFile(new File(mMyContact.getImgPath()));
            Glide.with(this).load(uri).into(mNavHead);
        }else{
            Glide.with(this).load(R.drawable.nav_head_portrait).into(mNavHead);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContacts.clear();
        mOldContacts.clear();
        List<Contact> result=ContactDao.getContacts();
        for(int i=1;i<result.size();i++){
            Contact contact=result.get(i);
            mContacts.add(contact);
        }
        mAdapter.notifyDataSetChanged();
        mMyContact=result.get(0);
        updateMySelf();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:break;
        }
        return true;
    }

    private void initData(){
        mOldContacts=new ArrayList<>();
        //mContacts= Test.getRandomContact();     //测试用
        mContacts= ContactDao.getContacts();
        if(mContacts.size()==0){    //数据库里没有自己的数据，现在新建一个
            Contact contact=new Contact();
            ContactDao.addContact(contact);
            mMyContact=contact;
        }else if(mContacts.size()>=1){
            mMyContact=mContacts.get(0);
            mContacts.remove(0);
        }
    }

    private void search(){
        String keyWord=mSearchEditText.getText().toString();
        if(keyWord.trim().equals("")){
            searchBack();
        }
        List<Contact> result=new ArrayList<>();
        for(Contact contact:mContacts){     //对Contact中的逐个字段进行匹配
            if(contact.getName()!=null&&contact.getName().contains(keyWord)){
                result.add(contact);
            }else if(contact.getPhone()!=null&&contact.getPhone().contains(keyWord)){
                result.add(contact);
            }else if(contact.getEmail()!=null&&contact.getEmail().contains(keyWord)){
                result.add(contact);
            }else if(contact.getQq()!=null&&contact.getQq().contains(keyWord)){
                result.add(contact);
            }else if(contact.getWeChat()!=null&&contact.getWeChat().contains(keyWord)){
                result.add(contact);
            }else if(contact.getAddress()!=null&&contact.getAddress().contains(keyWord)){
                result.add(contact);
            }
        }
        for(Contact contact:mContacts){
            mOldContacts.add(contact);
        }
        mContacts.clear();
        for(Contact contact:result){
            mContacts.add(contact);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void searchBack(){
        if(!mOldContacts.isEmpty()){
            mContacts.clear();
            for(Contact contact:mOldContacts){
                mContacts.add(contact);
            }
            mOldContacts.clear();
            mAdapter.notifyDataSetChanged();
        }
    }
}
