package com.burman.rohit.encryptor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.burman.rohit.encryptor.utils.ModeHandler;

import java.util.ArrayList;

/**
 * Created by Rohit on 3/7/2016.
 */
public  class DialogFrag extends DialogFragment {
    private static final String TAG = "TAB_FRAGMENT";
    private static String operation_mode;
    private static String tabMode;
    private static RVAdapter adapter;
    private static int itemPosition;

    private String key;
    private Boolean delete;
    private ArrayList <String> paths;

    private TextInputLayout passwordInputLayout;
    private CheckBox checkBoxDelete;

    static DialogFrag newInstance(String modeHandler, int position) {
        DialogFrag f = new DialogFrag();
        itemPosition=position;
        tabMode=modeHandler;
        operation_mode=(tabMode.equals(ModeHandler.ENCRYPTOR))?ModeHandler.DECRYPTOR:ModeHandler
                .ENCRYPTOR;
        Bundle args = new Bundle();
        args.putString("MODE", modeHandler);
        args.putInt("Position", position);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            tabMode=savedInstanceState.getString("MODE");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_password, null);
        builder.setView(v).setTitle("Enter passphrase");

        Spinner keylen= (Spinner) v.findViewById(R.id.spinner_key);
        checkBoxDelete = (CheckBox) v.findViewById(R.id.checkBox);
        passwordInputLayout = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        paths= new ArrayList<>();

        ArrayAdapter<CharSequence> klen=ArrayAdapter.createFromResource(getActivity(), R.array
                        .spinner_keylength_array,
                android.R.layout.simple_spinner_dropdown_item);
        keylen.setAdapter(klen);
        keylen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                key = String.valueOf(parent.getSelectedItem().toString().contains("128")?128:256);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                key = String.valueOf(parent.getSelectedItem().toString().contains("128")?128:256);
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pass = String.valueOf(passwordInputLayout.getEditText().getText());
                if (pass.isEmpty()) {

                } else {
                    delete = checkBoxDelete.isChecked();
                    TabFragment tabFragment= ((TabFragment)ModeHandler.getTab(tabMode));
                        Log.i(TAG,""+ModeHandler.tablist.toString());
                        Log.i(TAG,"getting tab in mode "+tabMode+" "+ModeHandler.getTab(tabMode));
                    adapter= tabFragment.getRVAdapter();
                    paths.add(adapter.getFilePath(itemPosition));
                    ((Encryptor) getActivity()).startEncryptorService(paths, pass, operation_mode, key, delete);
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("MODE", tabMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            tabMode=savedInstanceState.getString("MODE");
        }
    }
}
