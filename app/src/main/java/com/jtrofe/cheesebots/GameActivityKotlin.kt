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
import com.jtrofe.cheesebots.physics.PhysicsViewKotlin
import com.jtrofe.cheesebots.physics.VecKotlin
import java.util.ArrayList
import kotlin.concurrent.thread


public class GameActivityKotlin{//} : Activity() {

    /*var v: PhysicsViewKotlin? = null

    // UI junk
    private var mScoreView:TextView? = null
    private var mMessageView:TextView? = null

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


        setContentView(R.layout.activity_game)

        val pView = PhysicsViewKotlin(this)

        val frame = findViewById(R.id.gameFrame) as FrameLayout

        frame.addView(pView)
        //setContentView(v)

        v = pView

        thread(true, block={
            //val spriteSheets = SpriteHandler.GetSpriteSheets(this)
            val spriteSheets = SpriteHandler.GetSpriteSheets()

            v?.SetSpriteSheet(spriteSheets.toArrayList())
        })

        GameApp.CurrentGame.GameContext = this

        mScoreView = findViewById(R.id.destroyedCounter) as TextView

        mMessageView = findViewById(R.id.messageText) as TextView
        mMessageView?.setText("")

        if(GameApp.CurrentGame.IsComplete()){
            GameApp.CurrentGame.OnComplete()
        }
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
        if (GameApp.CurrentGame != null) {
            if(GameApp.CurrentGame.IsComplete()) {
                GameApp.CurrentGame = null
                finish()
            }
        }
    }

    // UI Junk
    public fun SetScore(score:String){
        runOnUiThread { mScoreView?.setText(score) }
    }

    public fun SetCompleteMessage(msg:String){
        runOnUiThread { mMessageView?.setText(msg) }
    }*/
}
