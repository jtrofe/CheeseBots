package com.jtrofe.cheesebots.inventory;

import android.graphics.PointF;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.UserData.Storage;
import com.jtrofe.cheesebots.game.UserData.UserFlail;

import java.util.List;

/**
 * Created by MAIN on 3/7/16
 */
public class FlailAdapter extends RecyclerView.Adapter<FlailAdapter.FlailViewHolder> implements View.OnClickListener{

    public static class FlailViewHolder extends RecyclerView.ViewHolder{
        FrameLayout cv;

        RelativeLayout flailFrame;


        ImageView flailImage;
        TextView flailName;

        GaugeView flailMassGauge;
        GaugeView flailRadiusGauge;
        GaugeView flailKGauge;

        public FlailViewHolder(View itemView, FlailAdapter adapter){
            super(itemView);

            cv = (FrameLayout) itemView.findViewById(R.id.cv);

            flailFrame = (RelativeLayout) itemView.findViewById(R.id.flailFrame);

            flailImage = (ImageView) itemView.findViewById(R.id.flailImage);
            flailName = (TextView) itemView.findViewById(R.id.flailName);

            flailMassGauge = (GaugeView) itemView.findViewById(R.id.inventory_flail_mass_gauge);
            flailRadiusGauge = (GaugeView) itemView.findViewById(R.id.inventory_flail_radius_gauge);
            flailKGauge = (GaugeView) itemView.findViewById(R.id.inventory_flail_k_gauge);

            cv.setTag(this);
            cv.setOnClickListener(adapter);
        }
    }

    List<UserFlail> flails;

    public FlailAdapter(List<UserFlail> flails){
        this.flails = flails;
    }

    @Override
    public int getItemCount(){
        return flails.size();
    }

    @Override
    public void onClick(View v){
        FlailViewHolder holder = (FlailViewHolder) v.getTag();
        GameApp.CurrentUser.SetSelectedFlailIndex(holder.getPosition());
        this.notifyDataSetChanged();

        Storage.SaveUser();
    }

    @Override
    public FlailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_flail, viewGroup, false);

        return new FlailViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(FlailViewHolder flailViewHolder, int i) {
        if(i == GameApp.CurrentUser.GetSelectedFlailIndex()){
            flailViewHolder.flailFrame.setBackgroundResource(R.drawable.inventory_card_selected);
        }else{
            flailViewHolder.flailFrame.setBackgroundResource(R.drawable.inventory_card_unselected);
        }
        int graphic = flails.get(i).GetGraphic();
        int massLevel = flails.get(i).GetLevel(UserFlail.TYPE_MASS);
        int radiusLevel = flails.get(i).GetLevel(UserFlail.TYPE_RADIUS);
        int kLevel = flails.get(i).GetLevel(UserFlail.TYPE_K);

        flailViewHolder.flailImage.setImageResource(R.drawable.cheese_frames);

        if(graphic == 0){
            flailViewHolder.flailName.setText(R.string.flail_bowling);
        }

        flailViewHolder.flailMassGauge.SetGauge(massLevel);
        flailViewHolder.flailRadiusGauge.SetGauge(radiusLevel);
        flailViewHolder.flailKGauge.SetGauge(kLevel);
    }
}
