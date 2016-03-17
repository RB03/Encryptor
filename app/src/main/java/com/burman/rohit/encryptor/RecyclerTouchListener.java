package com.burman.rohit.encryptor;

/**
 * Created by Rohit on 3/15/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class RecyclerTouchListener implements RecyclerView.OnItemTouchListener, RecyclerView.OnLongClickListener {


    GestureDetector gestureDetector;
    private OnItemClickListener mListener;

    @Override
    public boolean onLongClick(View v) {
        mListener.onItemLongClick(v);
        return false;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view);
    }


    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        this.mListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mListener != null && gestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
        }
        return true;
    }


    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }


    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


}