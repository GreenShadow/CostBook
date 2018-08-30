package com.greenshadow.costbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.greenshadow.costbook.utils.ColorUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;

public class TintAnimateImageView extends ImageView {
    public TintAnimateImageView(Context context) {
        super(context);
    }

    public TintAnimateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintAnimateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Keep
    public void setTint(@ColorInt int tintColor) {
        setImageTintList(ColorUtils.getTintList(tintColor));
    }

    @Keep
    public int getTint() {
        return ColorUtils.getColorFromList(getImageTintList());
    }
}
