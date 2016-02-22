package com.jtrofe.cheesebots

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*

import com.jtrofe.cheesebots.R
import com.jtrofe.cheesebots.game.Levels.GameLevel
import com.jtrofe.cheesebots.game.Levels.Level0
import com.jtrofe.cheesebots.physics.PhysicsView
import com.jtrofe.cheesebots.physics.Vec
import java.util.ArrayList


public class MainActivity : Activity() {

    var v:PhysicsView? = null

    /**
     * Hide the navigation buttons so the
     * game can be truly fullscreen
     */
    private fun removeNavigation() {
        val decorView = getWindow().getDecorView()
        var uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        decorView.setSystemUiVisibility(uiOptions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN)


        v = PhysicsView(this)

        setContentView(v)

        val spriteSheets = SpriteHandler.GetSpriteSheets(this)

        v?.SetSpriteSheet(spriteSheets)

    }

    override fun onResume() {
        super.onResume()
        v?.Resume()

        // Hide the system UI
        removeNavigation()
    }

    override fun onPause() {
        super.onPause()
        v?.Pause()
    }

    override fun onBackPressed() {
        /*if (GameApplication.CurrentGame) {
            GameApplication.GameEngine = null
            finish()
        }*/
    }
}
