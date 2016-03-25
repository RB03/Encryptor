package com.burman.rohit.encryptor.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.burman.rohit.encryptor.StatusBroadcastReceiver;
import com.burman.rohit.encryptor.algo.Crypto;
import com.burman.rohit.encryptor.utils.FileHandler;
import com.burman.rohit.encryptor.utils.ModeHandler;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Rohit on 3/6/2016.
 */
public class EncryptorAESService extends IntentService {

    private static final String TAG = "ENCRYPTOR_AES_SERVICE";
    private static boolean failure;
    private static int failure_count;
    private String mPassword;
    private ArrayList<String> mFilePaths;
    private String mKeyLen;
    private String mMode;
    private Boolean mDeleteFiles;
    private Crypto crypto;
    private String mAlgorithm;

    public EncryptorAESService() {
        super("EncryptorAESService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(EncryptorAESService.this);

        Bundle bundle=intent.getExtras();
        mFilePaths = (ArrayList<String>) bundle.getSerializable("FILE_PATHS");
        mPassword = bundle.getString("PASSWORD");
        mKeyLen =bundle.getString("KEY_LEN");
        mMode = bundle.getString("MODE");
        mDeleteFiles= bundle.getBoolean("DELETE_SOURCE");
        int count= mFilePaths.size();

         File[] source_files = new File[count];
         File[] dest_files= new File[count];

        buildFiles(mFilePaths, source_files, dest_files);

        Intent broadcast = new Intent();
        broadcast.setAction(StatusBroadcastReceiver.BROADCAST_ACTION);
        broadcast.putExtra("MODE", mMode);
        broadcast.putExtra("MAX",count);

        try {
            crypto = new Crypto(mPassword, Integer.valueOf(mKeyLen));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mMode.equals(ModeHandler.ENCRYPTOR)) {

            for (int file = 0; file<count ; file++) {
             boolean success=false;
                broadcast.putExtra("NAME",source_files[file].getName());
                broadcast.putExtra("PROGRESS",file);
                broadcast.putExtra("STATUS","UPDATE");
                Log.d(TAG, "sending intent");
                lbm.sendBroadcast(broadcast);

                try {
                    if (source_files[file].exists())
                        success = crypto.encrypt(source_files[file], dest_files[file], mDeleteFiles);
                    else count--;
                } catch (Exception e) {
                    failure_count++;
                    dest_files[file].delete();
                    e.printStackTrace();
                }
            }
        } else {
            for (int file = 0; file<count; file++) {
                boolean success=false;
                broadcast.putExtra("NAME",source_files[file].getName());
                broadcast.putExtra("STATUS","UPDATE");
                broadcast.putExtra("PROGRESS", file);
                Log.d(TAG, "sending intent");
                lbm.sendBroadcast(broadcast);
                try {
                    if (source_files[file].exists())
                        success = crypto.decrypt(source_files[file], dest_files[file], mDeleteFiles);
                    else count--;
                } catch (Exception e) {
                    failure_count++;
                    dest_files[file].delete();
                    e.printStackTrace();
                }
            }
        }


        broadcast.putExtra("STATUS","COMPLETE");
        broadcast.putExtra("FAILURE_COUNT", failure_count);
        Log.d(TAG, "sending final intent");
        lbm.sendBroadcast(broadcast);
        failure_count=0;
    }

    private void buildFiles(ArrayList<String> mFilePaths, File[] source_files, File[] dest_files) {
        Iterator iterator= mFilePaths.iterator();

        for(int file=0; iterator.hasNext(); file++){
            source_files[file]=new File((String) iterator.next());
            dest_files[file]= new File(FileHandler.getDirectory(mMode)+File.separator+getName
                    (source_files[file].getName()));

        }
    }

    private String getName(String name) {
        if(mMode.equals(ModeHandler.ENCRYPTOR)){
            return name+".aes";
        }
        else {
            if (FilenameUtils.getExtension(name).equals("aes"))return FilenameUtils.removeExtension(name);
        }
        return name;
    }
}
