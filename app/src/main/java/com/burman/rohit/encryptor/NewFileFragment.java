package com.burman.rohit.encryptor;


import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.burman.rohit.encryptor.utils.ModeHandler;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

public class NewFileFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "[NewFileFragment]" ;

    private TextInputLayout passwordInputLayout;
    private TextView pathTextView;
    private Button doItButton, pickButton;
    private Spinner modeSpin;
    private Spinner keyLen;
    private CheckBox checkBoxDelete;

    private String file_path;
    private ArrayList<String> file_paths;
    private String mPassword;
    private String mMode, selected_mode, selected_keylen;
    private boolean delete_source = false;

    private int POSITION;
    private int count;

    private OnNewFileFragmentInteractionListener mListener;

    public NewFileFragment() {
        // Required empty public constructor
    }

    public static NewFileFragment newInstance(String MODE, int position) {
        NewFileFragment fragment = new NewFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, MODE);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMode = getArguments().getString(ARG_PARAM1);
            POSITION = getArguments().getInt(ARG_PARAM2);
        }

        if (mMode.equals(ModeHandler.DEFAULT)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoActionBar);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_new_file, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (getArguments() != null) {
            mMode = getArguments().getString(ARG_PARAM1);
            POSITION = getArguments().getInt(ARG_PARAM2);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        file_paths = new ArrayList<>();
        getViews(view);
        setUpListeners(view);
    }

    private void getViews(View view) {

        modeSpin = (Spinner) view.findViewById(R.id.spinner_mode);
        keyLen = (Spinner) view.findViewById(R.id.spinner_enc_key);
        checkBoxDelete = (CheckBox) view.findViewById(R.id.checkbox_delete);
        passwordInputLayout= (TextInputLayout) view.findViewById(R.id.input_layout_password);
        doItButton= (Button) view.findViewById(R.id.ok_button);
        pickButton = (Button) view.findViewById(R.id.pick_button);
        pathTextView = (TextView) view.findViewById(R.id.paths_text);

        ArrayAdapter<CharSequence> mode=ArrayAdapter.createFromResource(getActivity(),R.array.spinner_mode_array,
                android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> klen=ArrayAdapter.createFromResource(getActivity(),R.array
                        .spinner_keylength_array,
                android.R.layout.simple_spinner_dropdown_item);
        modeSpin.setAdapter(mode);
        keyLen.setAdapter(klen);
    }

    private void setUpListeners(View view) {

        switch (mMode) {
            case ModeHandler.DEFAULT:
                modeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected_mode = String.valueOf(parent.getSelectedItem().toString().contains("Encrypt") ? ModeHandler.ENCRYPTOR : ModeHandler.DECRYPTOR);
                        Log.e("spin mode", selected_mode);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selected_mode = String.valueOf(parent.getItemAtPosition(0).toString().contains("Encrypt") ? ModeHandler.ENCRYPTOR : ModeHandler.DECRYPTOR);
                        Log.e("spin mode", selected_mode);
                    }
                });
                keyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected_keylen = String.valueOf(parent.getSelectedItem().toString().contains("128") ? 128 : 256);
                        Log.e("spin key", selected_keylen);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selected_keylen = String.valueOf(parent.getSelectedItem().toString().contains("128") ? 128 : 256);
                        Log.e("spin key", selected_keylen);
                    }
                });

                pickButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentFilePicker();
                    }
                });

                doItButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean valid = validateFields(ModeHandler.DEFAULT);
                        if (valid) {
                            delete_source = checkBoxDelete.isChecked();
                            mListener.startEncryptorService(file_paths, mPassword, selected_mode,
                                    selected_keylen, delete_source);
                            dismiss();
                        }
                    }
                });
//        }else if (mMode.compareTo(ModeHandler.DECRYPTOR)==0 ){
//            doItButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean valid= validateFields(ModeHandler.DECRYPTOR);
//                    if(valid){
//                        Log.e("pw pth rd"," "+mPassword+" "+file_path+" "+mRadio);
//                        dismiss();
//                    }
//                }
//            });
//
//        }else if( mMode.compareTo(ModeHandler.ENCRYPTOR)==0){
//            doItButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean valid= validateFields(ModeHandler.ENCRYPTOR);
//                    if(valid){
//                        Log.e("pw pth rd"," "+mPassword+" "+file_path+" "+mRadio);
//                        dismiss();
//                    }
//                }
//            });
                break;
        }
    }


    private boolean validateFields(String viewType) throws NullPointerException{

        switch (viewType){
            case ModeHandler.DEFAULT:
                String password= String.valueOf(passwordInputLayout.getEditText().getText());
                if (password.length()!=0){
                    passwordInputLayout.setErrorEnabled(false);
                    mPassword=password;
                }else {
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Password is required");
                    return false;
                }
                if (!file_paths.isEmpty()){

                }else {
                    return false;
                }
                return true;

            case ModeHandler.DECRYPTOR :
                String password2= String.valueOf(passwordInputLayout.getEditText().getText());
                if (password2.length()!=0){
                    passwordInputLayout.setErrorEnabled(false);
                    mPassword=password2;
                }else {
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Enter a password");
                    return false;
                }
                return true;

            case ModeHandler.ENCRYPTOR:
                 password2= String.valueOf(passwordInputLayout.getEditText().getText());
                if (password2.length()!=0){
                    passwordInputLayout.setErrorEnabled(false);
                    mPassword=password2;
                }else {
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Enter a password");
                    return false;
                }
                return true;
        }
    return false;
    }


    private void intentFilePicker() {
        Intent i = new Intent(getActivity(), FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == -1) {
                ClipData clip = data.getClipData();
                if(clip!=null) {
                    pathTextView.setText(null);

                    for (int i = 0; i < clip.getItemCount(); i++) {
                        Uri uri = clip.getItemAt(i).getUri();
                        file_paths.add(uri.getPath());
                        pathTextView.append(uri.getPath() +"\n");
                    }
                } else {
                    try {
                        file_path = data.getData().getPath();
                        Log.e("Path: ", "" + file_path);
                        File file = new File(data.getData().getPath());
                        Log.e(TAG, "file exists: " + file.exists());
                        pathTextView.setText(file_path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnNewFileFragmentInteractionListener) {
            mListener = (OnNewFileFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnNewFileFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.dialog_animation;
    }


    public interface OnNewFileFragmentInteractionListener {
        void onDetailsFragmentInteraction(View view);
        void startEncryptorService(ArrayList<String> paths, String pass, String mode,String
                key_len,Boolean delete);
    }


}
