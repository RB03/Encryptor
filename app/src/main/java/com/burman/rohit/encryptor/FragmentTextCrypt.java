package com.burman.rohit.encryptor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.burman.rohit.encryptor.utils.ModeHandler;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;


public class FragmentTextCrypt extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText input_box;
    private EditText input_password_box;
    private ImageView collapse_icon;
    private CardView input_card;
    private ImageView copy_icon;
    private TextView output_box;
    private ImageView share_icon;
    private RelativeLayout output_card;
    private static Button encrypt_btn;

    private String mParam1;
    private String mParam2;
    private static boolean collapsed;
    private static int full_height=750;
    private static int initial_height;
    private static int maxlines;
    private static int minlines;
    private TabFragment.TabInteraction mListener;
    private boolean icon_state_expanded;
    private ImageView clear_text;
    private Spinner modeSpin;
    private ImageView done_icon;
    private String selected_mode;

    public FragmentTextCrypt() {}

    public static FragmentTextCrypt newInstance(String param1, String param2) {
        FragmentTextCrypt fragment = new FragmentTextCrypt();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_text_crypt, container, false);

        clear_text = (ImageView) v.findViewById(R.id.clear_text);
        input_box= (EditText) v.findViewById(R.id.input_text);
        input_card= (CardView) v.findViewById(R.id.input_card);
        copy_icon = (ImageView) v.findViewById(R.id.copy);
        share_icon = (ImageView) v.findViewById(R.id.share);
        output_box= (TextView) v.findViewById(R.id.output_box);
        output_card= (RelativeLayout) v.findViewById(R.id.output_card);
        done_icon= (ImageView) v.findViewById(R.id.done_icon);
        input_box= (EditText) v.findViewById(R.id.input_text);
        input_password_box= (EditText) v.findViewById(R.id.input_password);
        output_card.setVisibility(View.INVISIBLE);
        share_icon.setVisibility(View.INVISIBLE);
        copy_icon.setVisibility(View.INVISIBLE);
        output_box.setVisibility(View.INVISIBLE);
        modeSpin= (Spinner) v.findViewById(R.id.spinner_mode);


        ArrayAdapter<CharSequence> mode=ArrayAdapter.createFromResource(getActivity(),R.array.spinner_mode_array_short,
                android.R.layout.simple_spinner_dropdown_item);
        modeSpin.setAdapter(mode);

//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        full_height = (int) (350 * scale + 0.5f);
//        initial_height=input_box.getMinHeight();
//        collapsed= input_box.getHeight() <= input_box.getMinHeight();

        input_box.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                input_box.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });


       setupListeners(v);
        return v;
    }

    private void setupListeners(final View v) {
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


        done_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String pass = String.valueOf(input_password_box.getText());
                String message= String.valueOf(input_box.getText());
                if (!pass.isEmpty() && !message.isEmpty()){
                    switch (selected_mode){
                        case ModeHandler.DECRYPTOR:
                            try {
                                String decr_msg= AESCrypt.decrypt(pass,message);
                                output_box.setText(decr_msg);
                                animateCard(1);

                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            break;
                        case ModeHandler.ENCRYPTOR:
                            try {
                                String encr_msg= AESCrypt.encrypt(pass,message);
                                output_box.setText(encr_msg);
                                animateCard(1);

                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        });

        input_box.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.input_text) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_box.setText(null);
                output_box.setText(null);
                input_password_box.setText(null);
                if (output_card.getVisibility()==View.VISIBLE) animateCard(0);
            }
        });

        input_box.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });
//        collapse_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (collapsed) {
//                    expand(v);
//                } else {
//                    collapse(v);
//                }
//            }
//        });
    }

    private void animateCard(int i) {

        switch (i) {
            case 1:
            int l = output_card.getLeft(), t = output_card.getTop(), r = output_card.getRight(),
                    h = output_card.getHeight();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils
                        .createCircularReveal(output_card, output_card.getWidth() / 2,
                                 output_card.getHeight()/2,
                                0, output_card.getHeight());
                animator.setInterpolator(new LinearInterpolator());
                animator.setStartDelay(200);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        output_card.setVisibility(View.VISIBLE);
                        share_icon.setVisibility(View.VISIBLE);
                        copy_icon.setVisibility(View.VISIBLE);
                        output_box.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animator.removeAllListeners();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                animator.start();
            }else {
                output_card.setVisibility(View.VISIBLE);
                share_icon.setVisibility(View.VISIBLE);
                copy_icon.setVisibility(View.VISIBLE);
                output_box.setVisibility(View.VISIBLE);
            }
                break;
            case 0:
                int l1 = output_card.getLeft(), t1 = output_card.getTop(), r1 = output_card
                        .getRight(),
                        h1 = output_card.getHeight();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    final Animator animator = ViewAnimationUtils
                            .createCircularReveal(output_card, output_card.getWidth()/2, t1 - h1/2,
                                    output_card.getHeight(),0);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            output_card.setVisibility(View.INVISIBLE);
                            share_icon.setVisibility(View.INVISIBLE);
                            copy_icon.setVisibility(View.INVISIBLE);
                            output_box.setVisibility(View.INVISIBLE);
                            animator.removeAllListeners();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    animator.start();
                }else {
                    output_card.setVisibility(View.INVISIBLE);
                    share_icon.setVisibility(View.INVISIBLE);
                    copy_icon.setVisibility(View.INVISIBLE);
                    output_box.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void collapse(View v) {
        collapsed=true;

       ValueAnimator anim = ValueAnimator.ofInt(full_height, initial_height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = input_box.getLayoutParams();
                input_box.setHeight(val);
            }
        });
        anim.start();
        if(icon_state_expanded) {
            collapse_icon.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
            icon_state_expanded=false;
        }

        collapsed=true;
    }


    private void expand(View v) {
        collapsed=false;

        ValueAnimator anim = ValueAnimator.ofInt(input_box.getMeasuredHeightAndState(), full_height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = input_box.getLayoutParams();
                layoutParams.height = val;
                input_box.setHeight(val);
            }
        });
        anim.start();

        if(!icon_state_expanded) {
            collapse_icon.setImageResource(R.drawable.ic_keyboard_arrow_up_24dp);
            icon_state_expanded=true;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TabFragment.TabInteraction) {
            mListener = (TabFragment.TabInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewFileFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
