package com.burman.rohit.encryptor.utils;

import android.support.v4.app.Fragment;
import android.util.Log;


import com.burman.rohit.encryptor.TabFragment;

import java.util.HashMap;

/**
 * Created by Rohit on 2/10/2016.
 */
public class ModeHandler {
    public final static String ENCRYPTOR="ENCRYPTOR";
    public final static String DECRYPTOR="DECRYPTOR";
    public final static String DEFAULT="DEFAULT";
    private static final String TAG = "MODE_HANDLER";
    public static HashMap<String, Fragment> tablist =new HashMap<>();

    private String MODE;

    public ModeHandler(String mode) {
        this.MODE=mode;
    }


    public String getMODE() {
        return MODE;
    }

    public static void  registerTab(Fragment fragment, String mode ){
        tablist.put(mode, fragment);
        Log.d(TAG, "registering "+ fragment.getClass().getSimpleName()+" in mode "+ mode);
    }

    public static Fragment getTab(String mode) {
         return tablist.get(mode);
    }
}
