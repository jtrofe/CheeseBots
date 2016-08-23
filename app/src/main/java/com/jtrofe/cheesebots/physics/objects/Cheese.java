package com.jtrofe.cheesebots.physics.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.physics.Vec;

/**
 * Created by MAIN on 3/12/16
 */
public class Cheese extends GameObject{

    private int mGraphicIndex;

    private double mStartRadius;
    private double mRadius;

    private double mStartAmount;
    private double mAmountLeft;

    public double GetAmountLeft(){
        return mAmountLeft;
    }

    public double GetRadius(){
        return mRadius;
    }

    public void SetPosition(Vec position){
        mPosition = position.copy();
    }

    public Cheese(int graphicIndex, double startRadius, double startAmount){
        super(new Vec());

        mGraphicIndex = graphicIndex;
        mStartRadius = startRadius;
        mRadius = mStartRadius;

        mStartAmount = startAmount;
        mAmountLeft = mStartAmount;

        Type = GameObject.TYPE_CHEESE;
    }

    public void Eat(double amount){
        mAmountLeft -= amount;
        if(mAmountLeft < 0) mAmountLeft = 0.0;

        float p = (float) (mAmountLeft / mStartAmount);

        p = (float) Math.sin(p * (Math.PI / 2));

        mRadius = p * mStartRadius;
    }

    @Override
    public void Draw(Canvas canvas){
        if(GameApp.CurrentGame == null) return;

        if(GameApp.CurrentGame.SpritesLoaded){
            // Get the cheese bitmap
            Bitmap original = GameApp.CurrentGame.SpriteSheets.get(SpriteHandler.SHEET_CHEESE);

            // Create a mask bitmap
            //TODO replace getWidth and getHeight to cheese-specific values
            int bitmapSize = 300;
            Bitmap mask = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);

            // Get the current radius of cheese relating to the size of the source bitmap and draw the circle
            float p = (float) (mAmountLeft / mStartAmount);

            p = (float) Math.sin(p * (Math.PI / 2));

            float bitmapRadius = (bitmapSize * p) / 2f;
            float center = bitmapSize / 2f;
            new Canvas(mask).drawCircle(center, center, bitmapRadius, new Paint());

            // Create a bitmap that will contain the complete cheese image
            Bitmap result = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);

            // Draw the original cheese bitmap, then mask it
            int left = mGraphicIndex * bitmapSize;
            Canvas tempCanvas = new Canvas(result);
            Rect tempSrc = new Rect(left, 0, left + bitmapSize, bitmapSize);
            Rect tempDst = new Rect(0, 0, bitmapSize, bitmapSize);
            tempCanvas.drawBitmap(original, tempSrc, tempDst, null);
            //tempCanvas.drawBitmap(original, 0f, 0f, null);


            SpriteHandler.PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            tempCanvas.drawBitmap(mask, 0f, 0f, SpriteHandler.PAINT);
            SpriteHandler.PAINT.setXfermode(null);


            //TODO replace 300 with cheese-specific dimensions
            int sr = (int) mStartRadius;
            Rect src = new Rect(0, 0, bitmapSize, bitmapSize);
            Rect dst = new Rect(mPosition.xi() - sr, mPosition.yi() - sr,
                    mPosition.xi() + sr, mPosition.yi() + sr);


            canvas.drawBitmap(result, src, dst, null);

        }else{
            String colorString;
            switch(mGraphicIndex){
                case 0:
                    colorString = "#ECDFBF";
                    break;
                case 1:
                    colorString = "#FFDD75";
                    break;
                default:
                    colorString = "#ECDFBF";

            }
            SpriteHandler.PAINT.setColor(Color.parseColor(colorString));
            SpriteHandler.PAINT.setStyle(Paint.Style.FILL);

            canvas.drawCircle(mPosition.xf(), mPosition.yf(), (float) mRadius, SpriteHandler.PAINT);
        }
    }
}
