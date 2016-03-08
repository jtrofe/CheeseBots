package com.jtrofe.cheesebots.physics.objects

import android.graphics.*
import com.jtrofe.cheesebots.GameApp
import com.jtrofe.cheesebots.SpriteHandler
import com.jtrofe.cheesebots.physics.Vec

/**
 * Created by MAIN on 2/9/16.
 */
public class Cheese(position:Vec,
                    val mStartRadius:Double,
                    val mStartAmount:Double = 100.0,
                    val mCheeseType:Int = 0):GameObject(position){

    private var mAmountLeft:Double = mStartAmount

    private var mRadius:Double = mStartRadius

    public fun GetAmountLeft():Double{
        return mAmountLeft
    }

    public fun GetRadius():Double{
        return mRadius
    }

    init{
        Type = GameObject.TYPE_CHEESE
    }

    public fun Eat(amount:Double){
        mAmountLeft -= amount
        if(mAmountLeft < 0) mAmountLeft = 0.0

        mRadius = (mAmountLeft / mStartAmount) * mStartRadius;
    }

    override fun Draw(canvas:Canvas){
        if(GameApp.CurrentGame == null) return

        if(GameApp.CurrentGame.SpritesLoaded) {

            // Get the bitmap for the cheese
            var original = GameApp.CurrentGame.SpriteSheets[SpriteHandler.SHEET_CHEESE]

            // Create a mask bitmap
            //TODO replace getWidth and getHeight to cheese-specific values
            val mask = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888)

            // Get the current radius of cheese relating to the size of the source bitmap and draw the circle
            val p = (mAmountLeft / mStartAmount)
            val bitmapRadius: Float = (original.getWidth().toFloat() * p.toFloat()) / 2
            val center: Float = original.getWidth().toFloat() / 2f
            Canvas(mask).drawCircle(center, center, bitmapRadius, Paint())

            // Create a bitmap that will contain the complete cheese image
            val result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888)

            // Draw the original cheese bitmap, then mask it
            val tempCanvas = Canvas(result)
            tempCanvas.drawBitmap(original, 0f, 0f, null)

            val paint = Paint()
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
            tempCanvas.drawBitmap(mask, 0f, 0f, paint)
            paint.setXfermode(null)


            //TODO replace 300 with cheese-specific dimensions
            val sr = mStartRadius.toInt()
            val src = Rect(0, 0, 300, 300)
            val dst = Rect(mPosition.xi - sr, mPosition.yi - sr,
                    mPosition.xi + sr, mPosition.yi + sr)


            canvas.drawBitmap(result, src, dst, null)
        }else{
            val p = Paint()
            p.setColor(Color.YELLOW)

            val r = mRadius.toFloat()

            canvas.drawCircle(mPosition.xf, mPosition.yf, r, p)
        }
    }
}