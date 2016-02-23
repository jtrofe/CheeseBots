package com.jtrofe.cheesebots

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

import com.jtrofe.cheesebots.physics.Vec


public class MainActivity : Activity() {

    var botsOnScreen: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val button = findViewById(R.id.button_play) as Button

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startGame()
            }
        })

    }

    private fun startGame() {
        val intent = Intent(this, javaClass<GameActivity>())

        startActivity(intent)
    }
}
