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

        val r = mRadius.toInt()
        val rf = mRadius.toFloat()
        val d = r * 2

        val p = (mAmountLeft / mStartAmount)
        val offset = (150 - p).toInt()

        var original = GameApp.CurrentGame.SpriteSheets[SpriteHandler.SHEET_CHEESE]
        val mask = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888)

        val center:Float = original.getWidth().toFloat() / 2f
        val bitmapRadius:Float = (original.getWidth().toFloat() * p.toFloat()) / 2
        val pt = Paint()
        pt.setColor(Color.WHITE)
        Canvas(mask).drawCircle(center, center, bitmapRadius, pt)

        val result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888)

        val tempCanvas = Canvas(result)
        val paint = Paint()
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
        tempCanvas.drawBitmap(original, 0f, 0f, null)
        tempCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.setXfermode(null)


        if(GameApp.CurrentGame == null) return

        val sr = mStartRadius.toInt()
        val src = Rect(0, 0, 300, 300)
        val dst = Rect(mPosition.xi - sr, mPosition.yi - sr,
                mPosition.xi + sr, mPosition.yi + sr)


        //canvas.drawBitmap(GameApp.CurrentGame.SpriteSheets[SpriteHandler.SHEET_CHEESE], src, dst, null)

        canvas.drawBitmap(result, src, dst, null)



        //canvas.drawCircle(mPosition.x.toFloat(), mPosition.y.toFloat(), mRadius.toFloat(), paint)
    }
}