package com.jtrofe.cheesebots.inventory;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jtrofe.cheesebots.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 3/8/16
 */
public class GaugeView extends LinearLayout{

    private static int POINT_COUNT = 3;

    private List<ImageView> mBoxes;

    public GaugeView(Context context){
        this(context, null, 0);
    }

    public GaugeView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttrs){
        super(context, attrs, defStyleAttrs);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        this.setOrientation(HORIZONTAL);

        mBoxes = new ArrayList<>();

        for(int i=0;i<POINT_COUNT;i++){
            ImageView v = (ImageView) inflater.inflate(R.layout.inventory_check_box, this, false);
            this.addView(v);

            mBoxes.add(v);

        }
    }

    public void SetGauge(int value){
        for(int i=0;i<POINT_COUNT;i++){
            ImageView v = mBoxes.get(i);

            v.setBackgroundResource(R.drawable.inventory_value_unchecked);
        }

        for(int i=0;i<=value;i++){
            mBoxes.get(i).setBackgroundResource(R.drawable.inventory_value_checked);
        }
    }
}
