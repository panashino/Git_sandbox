package com.panashino.android.brain_storming;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements MyAdapter.AddListener, MyAdapter.DeleteListener{


    private static final String[] initData = {"sample_1"};
    private static final String[] initText = {"sample_text_1"};
    private static final String[] initImage = {null};
    private static final String[] initTag1 = {"Tag1"};
    private static final String[] initTag2 = {"Tag2"};

    private static final int[] initNum = {0};

    private Realm mRealm;
    private MyAdapter mAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            FloatingActionButton Add = (FloatingActionButton) findViewById(R.id.add_button);
            Add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MemoEdit.class);
                    startActivity(intent);
                }

            });


            mListView = (ListView) findViewById(R.id.listView);

            //Listenerの設置、ListViewのアイテムをクリックした時に発動する。
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //詳細画面開始　"memo_id"は第一引数でKey、Long.toStringは第二引数で渡したいlong型idをint型にしている。
                    Intent intent = new Intent(MainActivity.this, MemoEdit.class);
                    intent.putExtra("memo_id", Long.toString(id));
                    startActivity(intent);

                }
            });

            //初期データ読み込み
            initMemo();
    }

    //初期データの追加、データ数0のときのサンプルデータ追加
    private void initMemo() {

        Realm.init(this);

        //Realmインスタンスの作製
        mRealm = Realm.getDefaultInstance();

        //すべてのデータを取得・永続化処理
        RealmResults<Memo> mMemo = mRealm.where(Memo.class).findAll().sort("TextId");
        if (mMemo.size() == 0) {

            //データがない場合は初期データを追加、永続化
            mRealm.beginTransaction();

            for (int i = 0; i < initData.length; i++) {
                Memo memo = mRealm.createObject(Memo.class, i);
                memo.setTitle(initData[i]);
                memo.setText(initText[i]);
                memo.setNum(initNum[i]);
                memo.setImage(initImage[i]);
                memo.setTag1(initTag1[i]);
                memo.setTag2(initTag2[i]);
            }
            mRealm.commitTransaction();
        }

        //adapter = new Adapter(context, layout_file, object?)という感じ
        mAdapter = new MyAdapter(this, mMemo);
        mListView.setAdapter(mAdapter);
        mListView.setDivider(new ColorDrawable(Color.TRANSPARENT));

        mAdapter.setDeleteCallback(this);
        mAdapter.setAddCallback(this);
    }

    //Memoを削除
    private void deleteMemo(long memoId) {
        final long id = memoId;
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Memo memo = realm.where(Memo.class).equalTo("TextId", id).findFirst();
                memo.deleteFromRealm();
            }
        });
    }

    //Likeを追加
    private void addLike(long memoId) {
        final long id = memoId;
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Memo memo = realm.where(Memo.class).equalTo("TextId", id).findFirst();
                int number = memo.getNum() + 1;
                memo.setNum(number);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ;

        mRealm.close();
    }

    @Override
    public void delete(long memoId) {
        deleteMemo(memoId);
    }

    @Override
    public void add(long memoId) {
        addLike(memoId);
    }

}
