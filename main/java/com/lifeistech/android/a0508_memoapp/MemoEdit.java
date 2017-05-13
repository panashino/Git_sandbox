package com.lifeistech.android.a0508_memoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemoEdit extends AppCompatActivity implements View.OnClickListener {

    private Realm mRealm;
    private EditText editText;
    private EditText editTitle;
    private long Memo_id;

    View text_view;
    View title_view;

    //Activity起動時の初期設定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        //ソフトウェアキーボードを開く
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //アクションバーを開く
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //EditのIDづけ
        editText = (EditText) text_view.findViewById(R.id.edit_text);
        editTitle = (EditText) title_view.findViewById(R.id.edit_title);

        //保存ボタンと廃棄ボタンの設定
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        //初期データの設定
        initData();
    }

    //onClick時の挙動をボタンによって場合分け
    @Override
    public void onClick(View v){

        int id = v.getId();
        if (id == R.id.save){

            insert();
        }
        finish();
    }

    //Realm results型:クエリ検索を行い、「where」から「findAll」で表示する、「sort」で並び替え可能
    //Memoクラスから検索、idでソートする。（降順）
    public long nextMemoId(){
        RealmResults<Memo> results = mRealm.where(Memo.class).findAll().sort("TextId", Sort.DESCENDING);
        if (results.size() > 0){
            //降順だからfirstなのかな？
            return results.first().getTextId() + 1;
        }
        return 0;
    }

    //初期データの設定
    private void initData(){

        //Realmの利用開始、インスタンスの取得
        mRealm = Realm.getDefaultInstance();

        //getIntentはインスタンスの作製。インテント.メソッドの形
        //memo_idはキー名、キー名を元にしてStringの値を受け取る。
        String memo_id = getIntent().getStringExtra("memo_id");

        //string が null or 0-lengthならば、trueを返す。
        if(TextUtils.isEmpty(memo_id)){

            //データがない場合、新しいidを取得する。
            Memo_id = nextMemoId();
        }else{

            //データがある場合は更新
            Memo_id = Long.parseLong(memo_id);
            editText.setText(mRealm.where(Memo.class).equalTo("id", Memo_id).findFirst().getText());
            editText.setSelection(editText.getText().length());
            editTitle.setText(mRealm.where(Memo.class).equalTo("id", Memo_id).findFirst().getTitle());
            editTitle.setSelection(editTitle.getText().length());
        }
    }

    //Memoデータの追加
    private void insert() {
        //書き込みはトランザクション内部で行わなければいけない！
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm mRealm) {

                Memo memo = new Memo();
                memo.setTextId(Memo_id);
                memo.setText(editText.getText().toString());
                memo.setTitle(editTitle.getText().toString());

                mRealm.insertOrUpdate(memo);
                mRealm.copyToRealmOrUpdate(memo);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        mRealm.close();
    }
}
