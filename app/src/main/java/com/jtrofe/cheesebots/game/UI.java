package com.jtrofe.cheesebots.game;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 1/24/16
 */
public class UI{

    List<View> mViews;

    public UI(){
        mViews = new ArrayList<>();
    }

    public void AddView(View v){
        mViews.add(v);
    }

    public View GetView(String tag){
        for(View v:mViews){
            System.out.println(v.getTag());
            if(v.getTag().toString().equals(tag)){
                return v;
            }
        }
        return null;
    }
}
