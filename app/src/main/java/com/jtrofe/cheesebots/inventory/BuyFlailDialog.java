package com.jtrofe.cheesebots.inventory;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jtrofe.cheesebots.InventoryActivity;
import com.jtrofe.cheesebots.R;
import com.jtrofe.cheesebots.game.UserData.UserFlail;

/**
 * Created by MAIN on 4/10/16
 */
public class BuyFlailDialog{

    public static InventoryActivity activity;

    public static Dialog dialog;

    public static UserFlail flail;

    public static void Create(InventoryActivity context){
        activity = context;

        dialog = new Dialog(activity);

        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.inventory_dialog_upgrade_flail, null);

    }
}
