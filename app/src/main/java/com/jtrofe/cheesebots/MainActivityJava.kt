package com.jtrofe.cheesebots

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

import com.jtrofe.cheesebots.game.Levels.GameLevel
import com.jtrofe.cheesebots.game.Levels.Level0
import com.jtrofe.cheesebots.physics.Vec


public class MainActivityJava : Activity() {

    var botsOnScreen: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val button = findViewById(R.id.button_play) as Button

        botsOnScreen = findViewById(R.id.text_bots_on_screen) as EditText

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startGame()
            }
        })

    }

    private fun startGame() {
        val level = Level0()

        try {
            level.MaxBotsOnScreen = Integer.parseInt(botsOnScreen?.getText().toString())
        } catch (e: Exception) {
            level.MaxBotsOnScreen = 10
        }


        val intent = Intent(this, javaClass<GameActivity>())
        intent.putExtra("level", level.ToJSON())

        val c = findViewById(R.id.check_use_rope) as CheckBox

        GameApp.UseRope = c.isChecked()

        intent.putExtra("useRope", c.isChecked())

        startActivity(intent)


    }
}
