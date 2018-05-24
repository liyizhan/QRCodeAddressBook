package com.example.cyq.qrcodecard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cyq.qrcodecard.R;
import com.example.cyq.qrcodecard.activity.DetailActivity;
import com.example.cyq.qrcodecard.pojo.Contact;
import com.example.cyq.qrcodecard.util.StringUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by as on 2018/5/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> mContacts;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout mLinearLayout;
        CircleImageView mHeadPortrait;
        TextView mName;
        TextView mContact;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeadPortrait=(CircleImageView)itemView.findViewById(R.id.recycler_head_portrait);
            mName=(TextView)itemView.findViewById(R.id.recycler_name);
            mContact=(TextView)itemView.findViewById(R.id.recycler_contact);
            mLinearLayout=(LinearLayout)itemView.findViewById(R.id.recycler_item);
        }
    }

    public ContactAdapter(List<Contact> contacts){
        this.mContacts=contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Contact contact=mContacts.get(position);
        String imgPath=contact.getImgPath();
        if(StringUtils.isEmpty(imgPath)){
            Glide.with(mContext).load(R.drawable.nav_head_portrait).into(holder.mHeadPortrait);
        }else{
            /*File imgFile=new File(mContext.getExternalCacheDir(),
                    imgPath);
            Uri imgUri;
            if (Build.VERSION.SDK_INT>=24) {
                imgUri=FileProvider.getUriForFile(mContext,
                        "com.example.cyq.qrcodecard",imgFile);
            } else {
                imgUri=Uri.fromFile(imgFile);
            }
            Glide.with(mContext).load(imgUri).into(holder.mHeadPortrait);*/
            Bitmap bitmap= BitmapFactory.decodeFile(imgPath);
            holder.mHeadPortrait.setImageBitmap(bitmap);
        }
        String name=contact.getName();
        if(StringUtils.isEmpty(name)){
            holder.mName.setText(R.string.nav_name);
        }else{
            holder.mName.setText(name);
        }
        String contactStr="暂无联系方式";
        if(!StringUtils.isEmpty(contact.getPhone())){
            contactStr="电话:"+contact.getPhone();
        }else if(!StringUtils.isEmpty(contact.getEmail())){
            contactStr="电子邮箱:"+contact.getEmail();
        }else if(!StringUtils.isEmpty(contact.getEmail())){
            contactStr="QQ:"+contact.getQq();
        }else if(!StringUtils.isEmpty(contact.getEmail())){
            contactStr="微信:"+contact.getWeChat();
        }
        holder.mContact.setText(contactStr);
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.CONTACT_ID,contact.getId());
                intent.putExtra(DetailActivity.PAGE_FROM_SITE_ID,DetailActivity.EXIST_PAGE);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


}
