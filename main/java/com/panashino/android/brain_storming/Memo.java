package com.panashino.android.brain_storming;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by toe_shino on 2017/05/18.
 */

public class Memo extends RealmObject {

    @PrimaryKey
    private long TextId;

    private String text;
    private String title;
    private String tag1;
    private String tag2;

    private String imageUri;

    private int likeNum = 0;

    //Standard getters & setters generated
    //変数（フィールドの値）の設定後、変数.methodでメソッドが使用可能

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTextId() {
        return TextId;
    }

    public void setTextId(long TextId) {
        this.TextId = TextId;
    }

    public int getNum() {
        return likeNum;
    }

    public void setNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getImage(){
        return imageUri;
    }

    public void setImage(String image){
        this.imageUri = image;

    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }


}

//初期値｛TextId｝をsetTextIdクラスのフィールドであるthis.TextIdに代入している。