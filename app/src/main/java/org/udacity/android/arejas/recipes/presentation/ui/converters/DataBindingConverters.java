package org.udacity.android.arejas.recipes.presentation.ui.converters;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.udacity.android.arejas.recipes.utils.Utils;

import java.text.NumberFormat;
import java.util.List;

/*
* Class with static functions used by DataBinding library for setting UI according to parameters passed.
 */
public class DataBindingConverters {

    @BindingAdapter({"imageUrl", "errorResource", "loadingResource"})
    public static void loadImage(ImageView view, String url, Drawable errorResource, Drawable loadingResource) {
        // If not null, get poster image URI and load it with Picasso library
        if ((url != null) &&
                (!url.isEmpty()) &&
                (URLUtil.isNetworkUrl(url) || URLUtil.isFileUrl(url)) &&
                ((url.endsWith(".png")) || (url.endsWith(".jpg")) ||
                        (url.endsWith(".tif")) || (url.endsWith(".gif")))) {
            Glide.with(view.getContext())
                    .load(url)
                    .error(errorResource)
                    .placeholder(loadingResource)
                    .into(view);
        } else {
            Glide.with(view.getContext())
                    .load(errorResource)
                    .placeholder(loadingResource)
                    .into(view);
        }
    }

    @BindingAdapter({"imageResource"})
    public static void loadImage(ImageView view, Drawable imageResource) {
        Glide.with(view.getContext())
                .load(imageResource)
                .into(view);
    }

    @BindingAdapter({"animatedLoadingResource"})
    public static void loadAnimatedLoadingResource(ImageView view, Drawable animatedLoadingResource) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (animatedLoadingResource instanceof AnimatedVectorDrawable) {
                    AnimatedVectorDrawable animationResource = (AnimatedVectorDrawable) animatedLoadingResource;
                    view.setImageDrawable(animatedLoadingResource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        animationResource.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                animationResource.start();
                            }
                        });
                    }
                    animationResource.start();
                } else if (animatedLoadingResource instanceof AnimatedVectorDrawableCompat) {
                    AnimatedVectorDrawableCompat animationResource = (AnimatedVectorDrawableCompat) animatedLoadingResource;
                    view.setImageDrawable(animatedLoadingResource);
                    animationResource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            animationResource.start();
                        }
                    });
                    animationResource.start();
                } else {
                    view.setImageDrawable(animatedLoadingResource);
                }
            } else {
                if (animatedLoadingResource instanceof AnimatedVectorDrawableCompat) {
                    AnimatedVectorDrawableCompat animationResource = (AnimatedVectorDrawableCompat) animatedLoadingResource;
                    view.setImageDrawable(animatedLoadingResource);
                    animationResource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            animationResource.start();
                        }
                    });
                    animationResource.start();
                } else {
                    view.setImageDrawable(animatedLoadingResource);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"textUnderline"})
    public static void loadUnderlineText(TextView view, String dataText) {
        if (dataText != null) {
            view.setText(Utils.fromHtml("<u>" + dataText + "</u>"));
        }
    }

    @BindingAdapter({"textList"})
    public static void loadTextList(TextView view, List<String> dataTextList) {
        String textToWrite = "";
        if ((dataTextList != null) && (!dataTextList.isEmpty())) {
            for (String data : dataTextList) {
                //noinspection StringConcatenationInLoop
                textToWrite += "- " + data + ".\n";
            }
            view.setText(textToWrite);
        }
    }

    @BindingAdapter({"dataText", "textIntro", "noData"})
    public static void loadFancyText(TextView view, String dataText, String textIntro, String noData) {
        if (dataText != null) {
            view.setText(Utils.fromHtml(String.format(textIntro, dataText)));
        } else {
            view.setText(Utils.fromHtml(String.format(textIntro, noData)));
        }
    }

    @BindingAdapter({"dataInt", "textIntro", "noData"})
    public static void loadFancyInteger(TextView view, Integer dataInt, String textIntro, String noData) {
        if (dataInt != null) {
            String dataString = NumberFormat.getNumberInstance().format(dataInt);
            view.setText(Utils.fromHtml(String.format(textIntro,
                    (dataString != null) ? dataString : noData)));
        } else {
            view.setText(Utils.fromHtml(String.format(textIntro, noData)));
        }
    }

    @BindingAdapter({"dataList", "textIntro", "noData"})
    public static void loadFancyDataList(TextView view, List<String> dataList, String textIntro, String noData) {
        // If not null, compose the list and set it.
        if ((dataList != null) && (dataList.size() != 0)) {
            String dataString = TextUtils.join(", ", dataList);
            view.setText(Utils.fromHtml(String.format(textIntro, dataString)));
        } else {
            view.setText(Utils.fromHtml(String.format(textIntro, noData)));
        }
    }

}
