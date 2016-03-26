package com.burman.rohit.encryptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.burman.rohit.encryptor.utils.FileUtils;
import com.burman.rohit.encryptor.utils.ModeHandler;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Rohit on 3/21/2016.
 */
public class ContextActionMode implements ActionMode.Callback {
    public static boolean isAlive;
    static ContextActionMode cam = new ContextActionMode();
    static ActionMode am;
    private static String current_operating_mode = null;
    private static MenuItem encrypt;
    private static MenuItem decrypt;
    private static CheckList checklist;
    private static int decr;
    private static int encr;
    private static Context context;
    TabFragment.TabInteraction tabInteraction;
    private Menu _menu;



    public static ActionMode.Callback getActionModeCallback(Context context_param) {
        context= context_param;
        return cam;
    }

    public static void kill() {
        am.finish();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = mode.getMenuInflater();
        menuInflater.inflate(R.menu.action_mode, menu);
        mode.setTitle((encr + decr) + " " + "Selected");
        tabInteraction = TabFragment.getTabInteraction();
        isAlive = true;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        am = mode;
        _menu = mode.getMenu();
        encrypt = _menu.findItem(R.id.encrypt_menu);
        decrypt = _menu.findItem(R.id.decrypt_menu);
        isAlive = true;
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        Encryptor encryptor = new Encryptor();
        switch (id) {
            case R.id.decrypt_menu:
                tabInteraction.createPassDialog(ModeHandler.DECRYPTOR, CheckList.getPaths());
                break;
            case R.id.encrypt_menu:
                tabInteraction.createPassDialog(ModeHandler.ENCRYPTOR, CheckList.getPaths());
                break;
            case R.id.delete_menu:
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Confirm Delete?");
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> paths = CheckList.getPaths();
                        FileUtils.deleteFiles(paths);
                        RVAdapter rvAdapterEnc = ((TabFragment) ModeHandler.getTab(ModeHandler.ENCRYPTOR))
                                .getRVAdapter();
                        RVAdapter rvAdapterDec = ((TabFragment) ModeHandler.getTab(ModeHandler.DECRYPTOR))
                                .getRVAdapter();
                        rvAdapterDec.updateDataSet();
                        rvAdapterEnc.updateDataSet();
                    }
                });
                alert.show();

                break;
            case R.id.share_menu:
                ArrayList<String> file_paths_send=CheckList.getPaths();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files");
                intent.setType("*/*");

                ArrayList<Uri> files = new ArrayList<Uri>();

                for(String path : file_paths_send) {
                    File file = new File(path);
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                context.startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        CheckList.clear();
        encr = 0;
        decr = 0;
        current_operating_mode = null;
        isAlive = false;
    }

    public static void itemAdded(String mode) {

        if (mode.equals(ModeHandler.ENCRYPTOR)) encr++;
        else decr++;
        am.setTitle((encr + decr) + " " + "Selected");

//        if (encr==0) setEncryptMenuVisibility(true);
//        else if (decr==0) setDecryptMenuVisibility(true);
//        else if (encr>0 & decr>0){
//            setEncryptMenuVisibility(false);
//            setDecryptMenuVisibility(false);
//        }
    }

    public void setDecryptMenuVisibility(boolean b) {
        decrypt.setVisible(b);
    }

    public void setEncryptMenuVisibility(boolean b) {
        encrypt.setVisible(b);
    }

    public static void itemRemoved(String mode) {
        if (mode.equals(ModeHandler.ENCRYPTOR)) encr--;
        else decr--;
        am.setTitle((encr + decr) + " " + "Selected");
//        if (encr==0) setEncryptMenuVisibility(true);
//        else if (decr==0) setDecryptMenuVisibility(true);
//        else if (encr>0 & decr>0){
//            setEncryptMenuVisibility(false);
//            setDecryptMenuVisibility(false);
//        }
        Log.e("size ", String.valueOf(CheckList.size()) + " " + CheckList.isEmpty());
        if (CheckList.isEmpty()) {
            isAlive = false;
            am.finish();
        }
    }
}
