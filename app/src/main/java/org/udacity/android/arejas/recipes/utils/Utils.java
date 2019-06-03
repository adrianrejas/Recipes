package org.udacity.android.arejas.recipes.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Toast;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.RecipesApplication;
import org.udacity.android.arejas.recipes.presentation.ui.custom.CustomTypefaceSpan;

/**
 * Class with elements which can be useful in the rest of classes.
 */
public class Utils {

    /* Enumerator for distinguish between a sorting style by most popular movies or a sorting style
    by top rated movies. */
    public enum MovieSortType {
        POPULAR,
        TOP_RATED,
        FAVORITES;

        public static MovieSortType valueOf(int ordinal) {
            switch (ordinal) {
                case 0:
                    return POPULAR;
                case 1:
                    return TOP_RATED;
                default:
                    return FAVORITES;
            }
        }
    }

    private static Toast mToast;

    /**
     * This method shows an error on the screen. use this function instead of calling directly
     * Toast functions in order to cancel app toast still on screen.
     *
     * @param text The text string to be shown.
     *
     */
    private static void showToast(String text) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(RecipesApplication.getApplication().getApplicationContext(),
                text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Function for launching a web page (used for reviews)
     * @param web Web to open
     * @throws ActivityNotFoundException If no activity found to launch the video
     */
    public static void launchWebPage(String web) throws ActivityNotFoundException{
        if (web != null) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(web).buildUpon().build());
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (webIntent.resolveActivity(RecipesApplication.getApplication().getApplicationContext().getPackageManager()) != null) {
                RecipesApplication.getApplication().getApplicationContext().startActivity(webIntent);
            } else {
                throw new ActivityNotFoundException();
            }
        } else {
            showToast(RecipesApplication.getApplication().getApplicationContext().getString(R.string.error_no_web));
        }
    }


    /**
     * Function for calling Html.fromHtml function correctly depending on the Android version
     *
     * @param htmlText text to convert
     * @return Html.fromHtml returned object
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String htmlText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(htmlText);
        }
    }

    public static SpannableString getFontedString (String text, int resourceFont) {
        try {
            Typeface face = ResourcesCompat.getFont(
                    RecipesApplication.getApplication().getApplicationContext(), resourceFont);
            CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(face);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(typefaceSpan, 0, spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } catch (Exception e) {
            return new SpannableString("");
        }
    }
}
