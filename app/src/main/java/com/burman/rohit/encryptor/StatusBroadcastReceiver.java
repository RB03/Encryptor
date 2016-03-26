package com.burman.rohit.encryptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.burman.rohit.encryptor.utils.ModeHandler;

/**
 * Created by Rohit on 3/7/2016.
 */
public class StatusBroadcastReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION ="BROADCAST";
    public static final String EXTENDED_DATA_STATUS = "STATUS";
    private  Encryptor encryptor= new Encryptor();
    private static boolean failure;
    private static int failure_count;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle =intent.getExtras();
        String status= bundle.getString("STATUS");
        String mode= bundle.getString("MODE");
        String name= bundle.getString("NAME");
        int max=bundle.getInt("MAX");
        int progress= bundle.getInt("PROGRESS");
        failure_count=bundle.getInt("FAILURE_COUNT");


        Log.e("RECEIVER"," "+status+" "+name+" "+progress);

        switch (status) {
            case "COMPLETE":
                RVAdapter rvAdapterEnc = ((TabFragment) ModeHandler.getTab(ModeHandler.ENCRYPTOR))
                        .getRVAdapter();
                RVAdapter rvAdapterDec = ((TabFragment) ModeHandler.getTab(ModeHandler.DECRYPTOR))
                        .getRVAdapter();
                encryptor.dismissProgressBar();
                rvAdapterDec.updateDataSet();
                rvAdapterEnc.updateDataSet();

                if (failure_count>0) {
                    String msg = (mode.equals(ModeHandler.ENCRYPTOR)) ? (max - failure_count + " Files " +
                            "Encrypted." + " "
                            + failure_count + " FAILED!") : (max - failure_count + " Files " +
                            "Decrypted." + " "
                            + failure_count + " FAILED!");
                    encryptor.makeSnack(msg);
                    failure_count = 0;
                } else {
                    String msg = (mode.equals(ModeHandler.ENCRYPTOR)) ? max + " Files Encrypted" : max + " " +
                            "Files Decrypted";
                    encryptor.makeSnack(msg);
                }
                break;

            case "UPDATE":
                String msg = (mode.equals(ModeHandler.ENCRYPTOR)) ? "Encrypting " + name : "Decrypting " + name;
                encryptor.updateProgressBar(max, msg, progress, context);
                break;
        }
    }
}
