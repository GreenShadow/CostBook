package com.greenshadow.costbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenshadow.costbook.R;

public class IconInputTextLayout extends RelativeLayout {
    private ImageView mIconView;
    private TextInputLayout mTil;

    public IconInputTextLayout(Context context) {
        this(context, null);
    }

    public IconInputTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconInputTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IconInputTextLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutParams lp;

        mIconView = new ImageView(context);
        mIconView.setId(R.id.IconInputTextLayout_icon_id);
        mIconView.setPadding(
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding)
        );
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_START);
        lp.addRule(CENTER_VERTICAL);
        mIconView.setLayoutParams(lp);

        mTil = new TextInputLayout(context);
        mTil.setId(R.id.IconInputTextLayout_til_id);
        mTil.setGravity(Gravity.CENTER);
        mTil.setBackgroundColor(getResources().getColor(R.color.input_field_boxBackgroundColor));
        mTil.setBackground(getResources().getDrawable(R.drawable.bg_input));
//        mTil.setBoxBackgroundColor(getResources().getColor(R.color.input_field_boxBackgroundColor));
        mTil.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
        mTil.setBoxCornerRadii(
                getResources().getDimension(R.dimen.boxCornerRadius),
                getResources().getDimension(R.dimen.boxCornerRadius),
                0,
                0
        );
        TextInputEditText tie = new TextInputEditText(context);
        tie.setInputType(InputType.TYPE_CLASS_TEXT);
        tie.setMaxLines(1);
        tie.setPadding(
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding),
                getResources().getDimensionPixelSize(R.dimen.tiePadding)
        );
        tie.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mTil.addView(tie);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.til_height));
        lp.addRule(END_OF, R.id.IconInputTextLayout_icon_id);
        mTil.setLayoutParams(lp);

        addView(mIconView);
        addView(mTil);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconInputTextLayout);
        Drawable icon = ta.getDrawable(R.styleable.IconInputTextLayout_icon);
        if (icon != null) {
            mIconView.setImageDrawable(icon);
        }
        String hint = ta.getString(R.styleable.IconInputTextLayout_hint);
        if (!TextUtils.isEmpty(hint)) {
            mTil.setHint(hint);
        }
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        View inputFrame = mTil.getChildAt(0);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) inputFrame.getLayoutParams();
        lp.bottomMargin = lp.topMargin;
        inputFrame.setLayoutParams(lp);

        mIconView.setTop(inputFrame.getTop());
    }

    public String getText() {
        return mTil.getEditText().getText().toString();
    }
}
