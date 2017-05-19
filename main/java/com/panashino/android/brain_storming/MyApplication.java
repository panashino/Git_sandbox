package com.panashino.android.brain_storming;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by toe_shino on 2017/05/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}