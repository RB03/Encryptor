package com.burman.rohit.encryptor.utils;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.burman.rohit.encryptor.R;

/**
 * Created by Rohit on 3/15/2016.
 */
public class ViewProvider {
    private ModeHandler mModeHandler;


    public static View getRecyclerCardView(View v, String mode){
        switch (mode){
            case ModeHandler.DECRYPTOR:
                return v.findViewById(R.id.cv_dec);
            case ModeHandler.ENCRYPTOR:
                return v.findViewById(R.id.cv);
        }
        return null;
    }

    public static ImageView getLockIcon(View view, String mode) {

        switch (mode){
            case ModeHandler.DECRYPTOR:
                return (ImageView) view.findViewById(R.id.lock_icon_dec);
            case ModeHandler.ENCRYPTOR:
                return (ImageView) view.findViewById(R.id.unlock_icon);
        }
        return null;
    }

    public static TextView getFilenameTextView(View view, String mode) {

        switch (mode){
            case ModeHandler.DECRYPTOR:
                return (TextView) view.findViewById(R.id.filename_dec);
            case ModeHandler.ENCRYPTOR:
                return (TextView) view.findViewById(R.id.filename);
        }
        return null;
    }
}
