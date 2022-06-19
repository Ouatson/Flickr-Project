package com.example.flickr;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ImageOnClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "ImageOnClickListener";

    interface ClickListener {
        void ImageClickListener(View view, int position);
        void ImageLongClickListener(View view, int position);
    }
    public final ClickListener monlistener;
    private final GestureDetectorCompat monDetecteur;

    public ImageOnClickListener(Context context, final RecyclerView recyclerView, ClickListener listener) {
        this.monlistener = listener;
        this.monDetecteur = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View vueEnfant = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (vueEnfant != null && monlistener != null) {
                    Log.d(TAG, "onSingleTapUp: appelle listener: listener.onItemClick");
                    monlistener.ImageClickListener(vueEnfant, recyclerView.getChildAdapterPosition(vueEnfant));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
//                super.onLongPress(e);
                Log.d(TAG, "onLongPress: starts");
                View vueEnfant = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(vueEnfant != null){
                    Log.d(TAG, "onLongPress: appelle listener: listener.onItemLongClick");
                    monlistener.ImageLongClickListener(vueEnfant,recyclerView.getChildAdapterPosition(vueEnfant));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (monDetecteur != null) {
            return monDetecteur.onTouchEvent(e);
        } else {
            return false;
        }
    }
}
