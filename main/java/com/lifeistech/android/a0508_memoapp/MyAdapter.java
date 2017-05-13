package com.lifeistech.android.a0508_memoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringDef;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

/**
 * Created by toe_shino on 2017/05/09.
 */

public class MyAdapter extends RealmBaseAdapter<Memo> implements ListAdapter {

    //privateにするとエラー
    private Context mContext;
    private DeleteListener mListener;
    private LayoutInflater LayoutInflater = null;
    private OrderedRealmCollection<Memo> mOrderedRealmCollection;

    //コンストラクタ設定。superは継承元のコンストラクタ呼び出し
    public MyAdapter(Context context, OrderedRealmCollection<Memo> realmResults ){
        super(realmResults);
        this.mContext = context;
        this.mOrderedRealmCollection = realmResults;
        Realm.getDefaultInstance();
    }

    private static class MyViewHolder{
        TextView MemoTitle;
        ImageView delete;
        //TextView LikeCount;
        //ImageView LikeButton;
    }

    //positionを返すことになっているけど、Memoモデルのidをオーバーライドしている。
    @Override
    public long getItemId(int position){
        return getItem(position).getTextId();
    }


    @Override
    public int getCount(){

        int  size = mOrderedRealmCollection.size();

        return size;

    }


    public void setCallback(DeleteListener callback){
        mListener = callback;
    }

    public interface DeleteListener{
        void delete(long memoId);
    }

    //Viewを取得する。
    // getTagとは？
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        MyViewHolder myViewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
            //インスタンスの作製
            myViewHolder = new MyViewHolder();
            myViewHolder.MemoTitle = (TextView) convertView.findViewById(R.id.item_title);

            ImageView delete = (ImageView) convertView.findViewById(R.id.imageDelete);
            delete.setTag(getItemId(position));
            delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mListener.delete((long) v.getTag());
                }
            });
            myViewHolder.delete = delete;

            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
            myViewHolder.delete.setTag(getItemId(position));
        }
        myViewHolder.MemoTitle.setText(getItem(position).getTitle());
        return convertView;

    }

}
