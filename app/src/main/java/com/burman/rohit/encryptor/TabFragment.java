package com.burman.rohit.encryptor;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burman.rohit.encryptor.utils.FileHandler;
import com.burman.rohit.encryptor.utils.ModeHandler;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/9/2016.
 */

public class TabFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "TAB_FRAGMENT";
    private static TabInteraction tabInteraction;
    private static ContextActionMode cam = new ContextActionMode();
    private static ActionMode mActionMode;
    public RVAdapter mRVAdapter;
    private String MODE;
    private int POSITION;
    private ModeHandler mModeHandler;
    private FileHandler mFileHandler;


    public static TabInteraction getTabInteraction() {
        return tabInteraction;
    }

    public static TabFragment newInstance(int position) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("POSITION", POSITION);
        outState.putString(ARG_PARAM1, MODE);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                MODE = savedInstanceState.getString(ARG_PARAM1);
                POSITION = savedInstanceState.getInt(ARG_PARAM2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Cam is alive: " + ContextActionMode.isAlive);
        if (ContextActionMode.isAlive) {
            mActionMode = getActivity()
                    .startActionMode(ContextActionMode.getActionModeCallback(getContext()));
            CheckList.checkItems();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                POSITION = savedInstanceState.getInt(ARG_PARAM2);
                mModeHandler = new ModeHandler(MODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mFileHandler = new FileHandler();
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_card);
        recyclerView.setHasFixedSize(false);
        mRVAdapter = new RVAdapter(mModeHandler);
        recyclerView.setAdapter(mRVAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mRVAdapter.clickListener(new RVAdapter.ViewHolderClicks() {
            @Override
            public void onItemClicked(View v, final int position) {
                Log.d(TAG, "Item clicked");

                if (v instanceof ImageView) {
                    Log.d(TAG, "creating dialog instance in mode " + MODE);
                    tabInteraction.createPassDialog(mModeHandler, position);
                } else {

                    CardView cardView = (CardView) v;
                    TextView textView;

                    if (MODE.equals(ModeHandler.DECRYPTOR)) {
                        textView = (TextView) cardView.findViewById(R.id.filename_dec);
                        mFileHandler.openFile(getActivity().getBaseContext(),
                                textView.getText().toString(),
                                FileHandler.getDirectory(MODE));
                    } else if (MODE.equals(ModeHandler.ENCRYPTOR)) {
                        Snackbar.make(v, "FILE IS ENCRYPTED", Snackbar.LENGTH_LONG).setAction
                                ("DECRYPT?", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tabInteraction.createPassDialog(mModeHandler, position);
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onItemLongClicked(View v, int adapterPosition) {
                Snackbar.make(v, "Long Clicked " + (++adapterPosition), Snackbar.LENGTH_SHORT).
                        show();
            }

            @Override
            public void itemChecked(View v, int position, boolean isChecked) {
                if (position >= 0) {
                    ElementModal element = new ElementModal(position, mRVAdapter.getFilePath(position), mModeHandler.getMODE(), v);
                    if (isChecked) {
                        if (!ContextActionMode.isAlive) {
                            mActionMode = getActivity()
                                    .startActionMode(ContextActionMode.getActionModeCallback
                                            (getActivity()));
                        }
                        CheckList.addToCheckedList(element);
                    } else {
                        Log.d(TAG, "removing item from checklist");
                        CheckList.removeFromCheckedList(element);
                    }
                }
            }
        });

//
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, final int position) {
//                if (view instanceof ImageView) {
//                    Log.d(TAG, "creating dialog instance in mode " + MODE);
//                    tabInteraction.createPassDialog(mModeHandler, position);
//                } else {
//                    CardView cardView = (CardView) view;
//                    TextView textView;
//
//                    if (MODE.equals(ModeHandler.DECRYPTOR)) {
//                        textView = (TextView) cardView.findViewById(R.id.filename_dec);
//                        mFileHandler.openFile(getActivity().getBaseContext(),
//                                textView.getText().toString(),
//                                FileHandler.getDirectory(MODE));
//                    } else if (MODE.equals(ModeHandler.ENCRYPTOR)) {
//                        Snackbar.make(view, "File is encrypted", Snackbar.LENGTH_LONG).setAction
//                                ("Decrypt?", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        tabInteraction.createPassDialog(mModeHandler, position);
//                                    }
//                                }).show();
//                    }
//                }
//            }
//            @Override
//            public void onItemLongClick(View view) {
//                int pos= recyclerView.getChildAdapterPosition(view);
//                Snackbar.make(view, "Long Press", Snackbar.LENGTH_LONG).show();
//            }
//        }));

        return view;
    }

    public String getMODE() {
        return mModeHandler.getMODE();
    }

    public void setMODE(ModeHandler modeHandler) {
        this.mModeHandler = modeHandler;
        MODE = modeHandler.getMODE();
        ModeHandler.registerTab(this, MODE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TabInteraction) {
            tabInteraction = (TabInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewFileFragmentInteractionListener");
        }
    }

    public RVAdapter getRVAdapter() {
        if(mRVAdapter!=null){
            return mRVAdapter;
        }else return new RVAdapter(mModeHandler);
    }


    public interface TabInteraction {
        void createPassDialog(ModeHandler modeHandler, int position);

        void createPassDialog(String mode, ArrayList<String> paths);

    }
}