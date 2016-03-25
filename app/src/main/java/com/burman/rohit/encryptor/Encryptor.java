package com.burman.rohit.encryptor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.burman.rohit.encryptor.Service.EncryptorAESService;
import com.burman.rohit.encryptor.utils.FileHandler;
import com.burman.rohit.encryptor.utils.ModeHandler;

import java.util.ArrayList;
import java.util.List;

public class Encryptor extends AppCompatActivity implements NewFileFragment.OnNewFileFragmentInteractionListener, TabFragment.TabInteraction {
    private static final int ENCRYPT_TAB = 0;
    private static final int DECRYPT_TAB = 1;
    private static final String TAG = "MAIN ACTIVITY";

    private static ViewPager viewPager;
    private static FloatingActionButton fab;
    private  static ProgressDialog progressBar;
    private static CoordinatorLayout coordinatorLayout;
    private TabFragment tabFragmentEnc;
    private TabFragment tabFragmentDec;
    private TabFragment tabFragmentEnc1;
    private TabFragment tabFragmentDec2;
    private FragmentManager fragmentManager;
    private StatusBroadcastReceiver statusBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryptor);
        setUpToolBars();
        FileHandler.initialize();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.encryptor_layout);

        progressBar = new ProgressDialog(Encryptor.this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getSupportFragmentManager();
                // Create and show the dialog.
                NewFileFragment newFragment = NewFileFragment.newInstance(ModeHandler.DEFAULT, -1);
                newFragment.show(fragmentManager, "Details");


            }
        });

        IntentFilter filter = new IntentFilter(StatusBroadcastReceiver.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        statusBroadcastReceiver= new StatusBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(statusBroadcastReceiver, filter);

    }


    private void setUpToolBars() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setSubtitle("AES 256");
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        toolBarLayout.setTitle(getTitle());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//          }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_encryptor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.drawable.ic_keyboard_backspace_24dp:
            finish();
                System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager upViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabFragmentEnc=new TabFragment();
        tabFragmentDec= new TabFragment();
//        tabFragmentEnc=TabFragment.newInstance(ENCRYPT_TAB);
//        tabFragmentDec= TabFragment.newInstance(DECRYPT_TAB);
////
        tabFragmentDec.setMODE(new ModeHandler(ModeHandler.DECRYPTOR));
        tabFragmentEnc.setMODE(new ModeHandler(ModeHandler.ENCRYPTOR));

        adapter.addFrag(tabFragmentEnc, "Encrypted Files");
        adapter.addFrag(tabFragmentDec, "Decrypted Files");
        upViewPager.setAdapter(adapter);
    }



    @Override
    public void onDetailsFragmentInteraction(View view) {
    }

    @Override
    public void startEncryptorService(ArrayList<String> paths, String pass, String mode, String key_len, Boolean delete) {
        Log.d(TAG, "starting service");
        Intent intent= new Intent(this, EncryptorAESService.class);
        intent.putExtra("FILE_PATHS", paths);
        intent.putExtra("PASSWORD", pass);
        intent.putExtra("MODE",mode);
        intent.putExtra("KEY_LEN", key_len);
        intent.putExtra("DELETE_SOURCE", delete);
        startService(intent);
    }

    @Override
    public void createPassDialog(ModeHandler modeHandler, int position) {
     DialogFrag df= DialogFrag.newInstance(modeHandler.getMODE(), position);
        df.show(getSupportFragmentManager(), "Password");
    }

    @Override
    public void createPassDialog(String mode, ArrayList<String> paths) {
        DialogFrag df = DialogFrag.newInstance(mode, paths);
        df.show(getSupportFragmentManager(), "Password");
    }

    public void updateProgressBar(int max, String msg, int progress, Context context){
        Log.e(TAG," " +Encryptor.this);
        if(!progressBar.isShowing()) {
            progressBar.setTitle("Please Wait...");
            progressBar.setMax(max);
            progressBar.setProgress(progress);
            progressBar.setMessage(msg);
                Log.d(TAG, "starting progressbar");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.setIndeterminate(false);
            progressBar.show();
        }else {
            progressBar.incrementProgressBy(progress);
            progressBar.setMessage(msg);
        }
        }

    public void dismissProgressBar(){
            progressBar.dismiss();

    }

    public  void makeSnack(String message) {
        Snackbar.make(coordinatorLayout,message,Snackbar.LENGTH_LONG).show();
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
