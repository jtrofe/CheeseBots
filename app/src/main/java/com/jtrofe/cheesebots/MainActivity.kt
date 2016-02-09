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
import com.jtrofe.cheesebots.physics.PhysicsView
import com.jtrofe.cheesebots.physics.Vec


public class MainActivity : Activity() {

    var v:PhysicsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        v = PhysicsView(this)
        setContentView(v)

    }

    override fun onResume() {
        super.onResume()
        v?.Resume()

        // Hide the system UI
        //removeNavigation()
    }

    override fun onPause() {
        super.onPause()
        v?.Pause()
    }
}
