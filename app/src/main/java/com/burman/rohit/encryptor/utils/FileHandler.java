package com.burman.rohit.encryptor.utils;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rohit on 2/8/2016.
 */
public class FileHandler {

    private final static String HOME_NAME="Encryptor";
    private final static String HOME_DIRECTORY= Environment.getExternalStorageDirectory()+File
            .separator+HOME_NAME;
    private final static String ENCRYPTED_DIR=HOME_DIRECTORY+File.separator+"Encrypted";
    private final static String DECRYPTED_DIR=HOME_DIRECTORY+File.separator+"Decrypted";
    private static final boolean DEBUG = true;
    private static final String TAG = "FileHandler";

    private static String mMODE;


    public static FileHandler getInstance(String MODE) {

        mMODE= MODE;
        return new FileHandler();
    }

    public static void initialize(){
        File enc_dir = new File(ENCRYPTED_DIR);
            Log.e("Addr :", enc_dir.getAbsolutePath());
        File dec_dir = new File(DECRYPTED_DIR);
            Log.e("Addr :", dec_dir.getAbsolutePath());
        File directory = new File(HOME_DIRECTORY + File.separator);
            Log.e("Addr :", directory.getAbsolutePath());

        if(!directory.exists()) {
            boolean result= directory.mkdirs();
                Log.e("Result",String.valueOf(result));
            result= enc_dir.mkdirs();
                Log.e("Result",String.valueOf(result));
            result= dec_dir.mkdirs();
                Log.e("Result",String.valueOf(result));
        }
    }

    public ArrayList<File> getFiles(String directory){
        ArrayList<File> files= new ArrayList<>();
        File parent= new File(directory);
        File[] f =parent.listFiles();
        for (File file: f){
            if(!file.isDirectory()){
                files.add(file);
            }
        }
    return files;
    }

    public File getFile(String directory, String filename){
        return new File(directory+File.separator+filename);
    }

    public static String getDirectory(String MODE) {
        if(MODE.compareTo(ModeHandler.ENCRYPTOR)==0) return ENCRYPTED_DIR;
        else return DECRYPTED_DIR;
    }

    public void openFile(Context context, String filename, String directory) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        @SuppressWarnings("ConstantConditions")
        String mimeType="text/plain";
                try {
                   mimeType = myMime.getMimeTypeFromExtension
                            (fileExt(directory + File.separator + filename).substring(1));
                }catch (Exception e){
                        e.printStackTrace();
                }
        newIntent.setDataAndType(Uri.fromFile(getFile(directory, filename)), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    public static void openFile(Context context, Uri uri, String mime){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri , mime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }



    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ( DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Log.d(TAG,"type: "+type);

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + File.separator + split[1];
                    }

                    // TODO handle non-primary volumes
                }

                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }

                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }

            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getHomeDirectory() {
        return HOME_DIRECTORY;
    }

    public static String getEncryptedDir() {
        return ENCRYPTED_DIR;
    }

    public static String getDecryptedDir() {
        return DECRYPTED_DIR;
    }

    public static boolean isValidFile(String path) {
        File file= new File(path);
        boolean exists= file.isFile();
        Log.e("isValidFile: ", String.valueOf(exists));
        return exists;

    }
}
