package com.radioserver.bristolbeat.helpers;

import android.util.Patterns;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class CommonUtils {

    public static void loadImage(ImageView imageView, String imageUrl, int blankResId) {
        if (imageUrl != null && Patterns.WEB_URL.matcher(imageUrl).matches()) {
            loadImage(imageView, imageUrl, blankResId, null);
        } else {
            imageView.setImageResource(blankResId);
        }
    }

    public static void loadImage(ImageView imageView, String imageUrl,
                                 int blankResId, ImageLoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(blankResId)
                .showImageOnFail(blankResId)
                .showImageForEmptyUri(blankResId)
                .showImageOnLoading(blankResId)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .resetViewBeforeLoading(false)
                .build();
        if (listener != null)
            ImageLoader.getInstance().displayImage(imageUrl, imageView, options, listener);
        else
            ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
    }
}
