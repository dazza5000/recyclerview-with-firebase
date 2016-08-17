package org.emeritus.androidsample;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by daz on 8/17/16.
 */

public class EmeritusApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
