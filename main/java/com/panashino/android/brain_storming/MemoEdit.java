package com.panashino.android.brain_storming;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by toe_shino on 2017/05/18.
 */

public class MemoEdit extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CHOOSER = 1000;

    private Realm mRealm;
    private EditText editText;
    private EditText editTitle;
    private EditText tag1;
    private EditText tag2;
    private ImageView imageView;
    private long Memo_id;
    private Uri resultUri = null;

    int likeNum = 0;
    String init_image = null;

    //Activity起動時の初期設定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        setTitle("Edit_Text");

        FloatingActionButton Add = (FloatingActionButton)findViewById(R.id.show_gallery);
        Add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showGallery();
            }
        });

        //ソフトウェアキーボードを開く
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //アクションバーを開く
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //EditのIDづけ
        editText = (EditText) findViewById(R.id.edit_text);
        editTitle = (EditText) findViewById(R.id.edit_title);
        tag1 = (EditText)findViewById(R.id.Tag1);
        tag2 = (EditText)findViewById(R.id.Tag2);
        imageView = (ImageView) findViewById(R.id.image);


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

            editText.setText(mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getText());
            editText.setSelection(editText.getText().length());

            editTitle.setText(mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getTitle());
            editTitle.setSelection(editTitle.getText().length());

            tag1.setText(mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getTag1());
            tag1.setSelection(tag1.getText().length());

            tag2.setText(mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getTag2());
            tag2.setSelection(tag2.getText().length());

            likeNum = mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getNum();

            init_image = mRealm.where(Memo.class).equalTo("TextId", Memo_id).findFirst().getImage();
            if(init_image != null){
                Picasso.with(this).load(init_image).into(imageView);
            }
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
                memo.setTag1(tag1.getText().toString());
                memo.setTag2(tag2.getText().toString());


                memo.setNum(likeNum);

                if(resultUri != null){
                    memo.setImage(resultUri.toString());
                }else if(init_image != null){
                    memo.setImage(init_image);
                }

                mRealm.insertOrUpdate(memo);
            }
        });
    }

    private void showGallery(){
        Intent intentGallery;

        //SDKによって引数が異なる？
        if(Build.VERSION.SDK_INT < 19){
            intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentGallery.setType("image/*");
        }else{

            //CATEGORY_OPENABLEで統一識別子がインテントに渡される。actionの動きを明確にするためのカテゴリー設定
            //画像などの開くことのできるデータだけが選択される。
            intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
            intentGallery.setType("image/jpeg");
        }

        Intent intent = Intent.createChooser(intentGallery, "画像の選択");
        //intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});

        //移動先のActivityから値を返してほしいときにつかう。呼び出されるのは移動先Activityがfinishしたとき。
        //onActivityResultが呼び出される。
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    //requestCodeにはREQUEST_CHOOSER（第二引数）が入っている。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        if (requestCode == REQUEST_CHOOSER) {

            if (resultCode != RESULT_OK) {
                //キャンセル時
                return;
            }

            //キャンセルでない場合。画像を取得する。
            resultUri = null;

            if (data != null) {
                resultUri = data.getData();
            }
            if (resultUri == null) {
                return;
            }

            //ギャラリーへスキャンを促す。
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},
                    null
            );

            Picasso.with(this).load(resultUri).into(imageView);

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        mRealm.close();
    }
}