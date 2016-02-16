package com.jtrofe.cheesebots

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.*

import com.jtrofe.cheesebots.R
import com.jtrofe.cheesebots.game.Levels.GameLevel
import com.jtrofe.cheesebots.game.Levels.Level0
import com.jtrofe.cheesebots.physics.PhysicsView
import com.jtrofe.cheesebots.physics.Vec
import java.util.ArrayList


public class MainActivity : Activity() {

    var v:PhysicsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        v = PhysicsView(this)

        setContentView(v)

        val resources = getResources()

        val spriteSheets = ArrayList<Bitmap>()

        var sheet = BitmapFactory.decodeResource(resources, R.raw.bot_frames)
        spriteSheets.add(Bitmap.createScaledBitmap(sheet, 600, 180, false))

        sheet = BitmapFactory.decodeResource(resources, R.raw.flail_frames)
        spriteSheets.add(Bitmap.createScaledBitmap(sheet, 300, 100, false))

        v?.SetSpriteSheet(spriteSheets)

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
