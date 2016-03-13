package com.jtrofe.cheesebots

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText



public class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val playButton = findViewById(R.id.button_play) as Button
        val inventoryButton = findViewById(R.id.button_inventory) as Button

        playButton.setOnClickListener { startGame() }
        inventoryButton.setOnClickListener { openInventory() }

    }

    private fun openInventory(){
        val intent = Intent(this, javaClass<InventoryActivity>())

        startActivity(intent)
    }

    private fun startGame() {
        val intent = Intent(this, javaClass<GameActivity>())

        startActivity(intent)
    }
}
