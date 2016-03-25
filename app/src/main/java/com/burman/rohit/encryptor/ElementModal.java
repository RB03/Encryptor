package com.burman.rohit.encryptor;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by Rohit on 3/21/2016.
 */
public class ElementModal {
    private int adapter_position;
    private String path;
    private String mode;
    private CheckBox checkBox;


    public ElementModal(int adapter_position, String path, String mode, View v) {
        this.adapter_position = adapter_position;
        this.path = path;
        this.mode = mode;
        checkBox = (CheckBox) v;
        Log.d("Checked", mode + " " + adapter_position + " " + path);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAdapter_position() {
        return adapter_position;
    }

    public void setAdapter_position(int adapter_position) {
        this.adapter_position = adapter_position;
    }

}
