package team8.codepath.sightseeingapp.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public final class Utilities {


    public static String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    public static String decodeEmail(String email) {
        return email.replace(",", ".");
    }

    public static String getFirstName(String userName){
        String firstName = "";
        String[] userFirstLastName = userName.split(" ");

        if(userFirstLastName.length > 0)
            firstName = userFirstLastName[0];
        else
            firstName = userName;

        return firstName;
    }

    public static Bitmap writeTextOnDrawable(Context context, int drawableId, String text) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 10));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        if (textRect.width() >= (canvas.getWidth() - 4))
            paint.setTextSize(convertToPixels(context, 7));

        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }

    public static float convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }
}
