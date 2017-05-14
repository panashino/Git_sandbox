package com.lifeistech.android.a0508_memoapp;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by toe_shino on 2017/05/09.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
