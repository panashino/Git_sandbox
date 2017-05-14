package com.lifeistech.android.a0508_memoapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by toe_shino on 2017/05/08.
 */

public class Memo extends RealmObject {

    @PrimaryKey
    private long TextId;

    private String text;
    private String title;

    private int likeNum = 0;

    //Standard getters & setters generated
    //変数（フィールドの値）の設定後、変数.methodでメソッドが使用可能

    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public  long getTextId(){
        return TextId;
    }
    public void setTextId(long TextId){
        this.TextId = TextId;
    }

    public int getNum(){
        return likeNum;
    }
    public void setNum(int likeNum){
        this.likeNum = likeNum;
    }

    //初期値｛TextId｝をsetTextIdクラスのフィールドであるthis.TextIdに代入している。

}
