package com.panashino.android.brain_storming;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import io.realm.Realm;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


/**
 * Created by toe_shino on 2017/05/09.
 */

public class MyAdapter extends RealmBaseAdapter<Memo> implements ListAdapter {

    //privateにするとエラー
    private Context mContext;
    private DeleteListener mDeleteListener;
    private AddListener mAddListener;
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
        TextView LikeCount;
        TextView MemoTag1;
        TextView MemoTag2;
        ImageView delete;
        ImageView add;
        ImageView ItemImage;

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

    public void setDeleteCallback(DeleteListener callback){
        mDeleteListener = callback;
    }

    public void setAddCallback(AddListener callback){mAddListener = callback;}

    public interface DeleteListener{
        void delete(long memoId);
    }

    public interface AddListener{
        void add(long memoId);
    }

    //getViewの引数としているconvertViewはListViewのひとつの項目となるListView
    // convertViewがnullだった場合には、item.xmlからインスタンスをとってきてタグをつける。
    //2回目以降はとってきたときにtagをつけているので、tagでインスタンスを取得することにしている。
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final MyViewHolder myViewHolder;
        myViewHolder = new MyViewHolder();

        if(convertView != null && getItem(position).getNum() > 20) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_max, parent, false);

        } else if (convertView != null && getItem(position).getNum() > 10) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);

        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_min, parent, false);
        }

        //インスタンスの作製
        myViewHolder.MemoTitle = (TextView) convertView.findViewById(R.id.item_title);
        myViewHolder.LikeCount = (TextView) convertView.findViewById(R.id.item_likes);
        myViewHolder.MemoTag1 = (TextView) convertView.findViewById(R.id.tag_1);
        myViewHolder.MemoTag2 = (TextView) convertView.findViewById(R.id.tag_2);

        myViewHolder.ItemImage = (ImageView) convertView.findViewById(R.id.item_image);

        ImageView delete = (ImageView) convertView.findViewById(R.id.imageDelete);
        ImageView add = (ImageView) convertView.findViewById(R.id.imageLike);

        //add設定
        add.setTag(getItemId(position));
        add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mAddListener.add((long) v.getTag());
                }
            });

        //削除設定
        delete.setTag(getItemId(position));
        delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){mDeleteListener.delete((long) v.getTag());
                }
            });

        myViewHolder.delete = delete;
        myViewHolder.add = add;

        myViewHolder.delete.setTag(getItemId(position));
        myViewHolder.add.setTag(getItemId(position));

        myViewHolder.LikeCount.setText(getItem(position).getNum() + "");
        myViewHolder.MemoTitle.setText(getItem(position).getTitle());
        myViewHolder.MemoTag1.setText(getItem(position).getTag1());
        myViewHolder.MemoTag2.setText(getItem(position).getTag2());


        if(getItem(position).getImage() != null){

            Uri uri = Uri.parse(getItem(position).getImage());
            Picasso.with(mContext).load(uri).fit().into(myViewHolder.ItemImage);
        }

        return convertView;

    }

}