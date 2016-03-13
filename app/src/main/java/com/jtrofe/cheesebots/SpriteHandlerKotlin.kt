package com.jtrofe.cheesebots

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.jtrofe.cheesebots.physics.VecKotlin
import com.jtrofe.cheesebots.physics.objects.BotKotlin
import java.util.ArrayList

/**
 * Created by MAIN on 2/17/16.
 */
public class SpriteHandlerKotlin(){

    companion object{
        public val SHEET_SMALL_BOT:Int = 0
        public val SHEET_MEDIUM_BOT:Int = 1
        public val SHEET_LARGE_BOT:Int = 2
        public val SHEET_GIANT_BOT:Int = 3
        public val SHEET_FLAIL:Int = 4
        public val SHEET_CHEESE:Int = 5


        public val WALK_FRAMES:ArrayList<IntArray> = arrayListOf(
                intArray(1, 2, 3, 2),
                intArray(1, 2),
                intArray(1, 2, 3, 2),
                intArray(1, 2, 3, 2)
            )

        public val EAT_FRAMES:ArrayList<IntArray> = arrayListOf(
                intArray(0, 4, 5, 4),
                intArray(0, 3, 4, 3),
                intArray(4, 5, 6, 7, 8),
                intArray(4, 5, 6, 7, 8, 9)
            )


        public fun GetSpriteSheets(context:Context):ArrayList<Bitmap>{
            val resources = context.getResources()

            val sheetList = ArrayList<Bitmap>()

            var sheet = BitmapFactory.decodeResource(resources, R.raw.bot_frames_small)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 600, 180, false))

            sheet = BitmapFactory.decodeResource(resources, R.raw.bot_frames_medium)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 425, 300, false))

            sheet = BitmapFactory.decodeResource(resources, R.raw.bot_frames_large)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 1116, 660, false))

            sheet = BitmapFactory.decodeResource(resources, R.raw.bot_frames_giant)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 3200, 975, false))

            sheet = BitmapFactory.decodeResource(resources, R.raw.flail_frames)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 300, 100, false))

            sheet = BitmapFactory.decodeResource(resources, R.raw.cheese_frames)
            sheetList.add(Bitmap.createScaledBitmap(sheet, 300, 300, false))


            return sheetList
        }
    }
}