package com.tqnee.KamS3r.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tqnee.KamS3r.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Ramzy on 6/23/2017.
 */

/**
 * this class hold the shared configuration in app
 * like check network availability
 * encode image as Base 64
 * changing tab custom font  etc
 */

public class Utils {

    public static void requestFocus(View view, Window window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public static Bitmap decodeAndResizeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 150;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 3;
                height_tmp /= 3;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    public static void TabCustomFontSize(Context context, TabLayout tabLayout, String Font, int FontColor) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTextColor(ContextCompat.getColor(context, FontColor));

                    FonTChange(context, ((TextView) tabViewChild), Font);
                }
            }
        }
    }

    private static void FonTChange(Context con, TextView textView, String Fonts) {
        String fontPath = "fonts/" + Fonts;
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(con.getAssets(), fontPath);
        // Applying font
        textView.setTypeface(tf);
    }

    public static void showSnackBar(Context mContext, View coordinatorLayout, String message, int ColorRes) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Utils.FonTChange(mContext, textView, Fontss.regularFont);
        textView.setTextColor(ContextCompat.getColor(mContext, ColorRes));
        snackbar.show();
    }

    public static void showSnackBarWithLong(Context mContext, View coordinatorLayout, String message, int ColorRes) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Utils.FonTChange(mContext, textView, Fontss.regularFont);
        textView.setTextColor(ContextCompat.getColor(mContext, ColorRes));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        snackbar.show();
    }

    public static void Share(Context context, String subject, String text) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(sharingIntent, "Share With "));
    }

    public static void sendEmail(Context mContext, String email) {
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "your subject");
        intentEmail.putExtra(Intent.EXTRA_TEXT, "message body");
        intentEmail.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(intentEmail, "Choose an email provider :"));
    }

    public static void feedbackButtonListener(final Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Constants.ContactUsEmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.feedback_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.sdk_version) + Build.VERSION.SDK_INT + "\n" + context.getResources().getString(R.string.android_vesion) + Build.VERSION.RELEASE + "\n" + context.getResources().getString(R.string.brand) + Build.BRAND + "\n" + context.getResources().getString(R.string.model) + Build.MODEL + "\n" + context.getResources().getString(R.string.manufacturer) + Build.MANUFACTURER+"\n");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void printLog(String key, String message) {
        Log.e(key, message);
    }
}
