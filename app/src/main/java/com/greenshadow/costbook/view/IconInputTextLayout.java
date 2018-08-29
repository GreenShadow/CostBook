package com.greenshadow.costbook.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.animation.AnimationUtils;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.utils.ColorUtils;

public class IconInputTextLayout extends RelativeLayout {
    private boolean mIsError = false;
    private boolean mIsExpanded = false;
    private boolean mIsLabelAnimateEnabled = true;

    private String mLabelText;
    private String mNoteText;
    private String mErrorText;

    private float mLabelTranslationY;

    private int mTextColor;
    private int mErrorColor;

    private ImageView mLeadingIcon;
    private RelativeLayout mInputContainer;
    private TextSizeAnimateTextView mLabel;
    private EditText mInput;
    private ImageView mTrailingIcon;
    private TextView mAssistLabel;

    private InputMethodManager mImm;

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
        mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        init(context, attrs);
        registerListeners();
        updateLabels();
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_input, this, true);

        mLeadingIcon = findViewById(R.id.leading_icon);
        mInputContainer = findViewById(R.id.input_container);
        mLabel = findViewById(R.id.label);
        mInput = findViewById(R.id.input);
        mTrailingIcon = findViewById(R.id.trailing_icon);
        mAssistLabel = findViewById(R.id.assist_label);

        mInput.setVisibility(GONE);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconInputTextLayout);

        boolean focusable = ta.getBoolean(R.styleable.IconInputTextLayout_android_focusable, true);
        mInput.setFocusable(focusable);
        mInput.setCursorVisible(focusable);

        Drawable leadingIcon = ta.getDrawable(R.styleable.IconInputTextLayout_leading_icon);
        if (leadingIcon != null) {
            leadingIcon.setTint(ColorUtils.getThemeColor(getContext(), R.attr.colorAccent));
            mLeadingIcon.setVisibility(VISIBLE);
            mLeadingIcon.setImageDrawable(leadingIcon);
        } else {
            mLeadingIcon.setVisibility(GONE);
        }

        Drawable trailingIcon = ta.getDrawable(R.styleable.IconInputTextLayout_trailing_icon);
        if (trailingIcon != null) {
            mTrailingIcon.setVisibility(VISIBLE);
            mTrailingIcon.setImageDrawable(trailingIcon);
        } else {
            mTrailingIcon.setVisibility(GONE);
        }

        mTextColor = ta.getColor(R.styleable.IconInputTextLayout_color_text,
                ColorUtils.getThemeColor(getContext(), android.R.attr.textColorPrimary));
        mInput.setTextColor(mTextColor);

        mErrorColor = ta.getColor(R.styleable.IconInputTextLayout_color_error,
                ColorUtils.getThemeColor(getContext(), R.attr.colorError));

        int inputType = ta.getInt(R.styleable.IconInputTextLayout_android_inputType, EditorInfo.TYPE_NULL);
        mInput.setInputType(inputType);

        mIsLabelAnimateEnabled = ta.getBoolean(R.styleable.IconInputTextLayout_label_animate_enabled, true);

        mLabelText = ta.getString(R.styleable.IconInputTextLayout_hint);
        mNoteText = ta.getString(R.styleable.IconInputTextLayout_note);
        mErrorText = ta.getString(R.styleable.IconInputTextLayout_error);

        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            int containerHeight = mInputContainer.getHeight();
            int labelHeight = findViewById(R.id.label_frame).getHeight();
            float inputHeight = mInput.getTextSize();
            mLabelTranslationY = (containerHeight - labelHeight - inputHeight) / 2 + 3;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            clearFocus();
            post(this::hideIm);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mInput.setVisibility(VISIBLE);
        mInput.requestFocus();
        return mInput.onTouchEvent(event);
    }

    private void registerListeners() {
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasContent = s.length() != 0;
                boolean hasFocus = mInput.hasFocus();

                if (hasContent) {
                    mInput.setVisibility(VISIBLE);
                } else {
                    if (!hasFocus) {
                        mInput.setVisibility(GONE);
                    } else {
                        mInput.setVisibility(VISIBLE);
                    }
                }
                expandCollapseLabel(hasContent || hasFocus, getWindowVisibility() == VISIBLE);
            }
        });

        mInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mInput.setVisibility(VISIBLE);
            } else {
                if (mInput.getText().toString().isEmpty()) {
                    mInput.setVisibility(GONE);
                }
            }
            expandCollapseLabel(hasFocus);

            mInputContainer.setSelected(hasFocus);
        });

        mInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mInput.clearFocus();
                hideIm();
                return true;
            }
            return false;
        });
    }

    private void hideIm() {
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void updateLabels() {
        mLabel.setText(mLabelText);
        mAssistLabel.setText(mNoteText);
    }

    private void expandCollapseLabel(boolean expand) {
        expandCollapseLabel(expand, mIsLabelAnimateEnabled);
    }

    private void expandCollapseLabel(boolean expand, boolean animate) {
        if (expand == mIsExpanded) {
            return;
        }

        if (!TextUtils.isEmpty(getText()) && !expand) {
            return;
        }

        post(() -> {
            mIsExpanded = expand;

            if (animate) {
                ObjectAnimator positionAnimator;
                ObjectAnimator textSizeAnimator;
                ObjectAnimator textColorAnimator;
                if (expand) {
                    positionAnimator = ObjectAnimator.ofFloat(mLabel, "translationY", mLabelTranslationY * -1);
                    textSizeAnimator = ObjectAnimator.ofFloat(mLabel, "CurrentTextSize", 14);
                    textColorAnimator = ObjectAnimator.ofArgb(mLabel, "textColor",
                            ColorUtils.getThemeColor(getContext(), R.attr.colorAccent));
                } else {
                    positionAnimator = ObjectAnimator.ofFloat(mLabel, "translationY", 0);
                    textSizeAnimator = ObjectAnimator.ofFloat(mLabel, "CurrentTextSize",
                            mInput.getTextSize() / getResources().getDisplayMetrics().density);
                    textColorAnimator = ObjectAnimator.ofArgb(mLabel, "textColor",
                            ColorUtils.getThemeColor(getContext(), R.attr.colorLabel));
                }

                AnimatorSet as = new AnimatorSet();
                as.setDuration(300);
                as.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                as.playTogether(positionAnimator, textSizeAnimator, textColorAnimator);
                as.start();
            } else {
                if (expand) {
                    mLabel.setTranslationY(mLabelTranslationY * -1);
                    mLabel.setCurrentTextSize(14);
                    mLabel.setTextColor(ColorUtils.getThemeColor(getContext(), R.attr.colorAccent));
                } else {
                    mLabel.setTranslationY(0);
                    mLabel.setCurrentTextSize(mInput.getTextSize() / getResources().getDisplayMetrics().density);
                    mLabel.setTextColor(ColorUtils.getThemeColor(getContext(), R.attr.colorLabel));
                }
            }
        });
    }

    public void setLabelAnimateEnabled(boolean enabled) {
        mIsLabelAnimateEnabled = enabled;
    }

    public void setError(String errorText) {
        mErrorText = errorText;
        mIsError = !TextUtils.isEmpty(errorText);

        if (mIsError) {
            if (!TextUtils.isEmpty(mErrorText)) {
                mAssistLabel.setVisibility(VISIBLE);
                mAssistLabel.setTextColor(mErrorColor);
                mAssistLabel.setText(mErrorText);
            }
            mInputContainer.setBackgroundResource(R.drawable.line_error);
        } else {
            if (!TextUtils.isEmpty(mNoteText)) {
                mAssistLabel.setVisibility(VISIBLE);
                mAssistLabel.setTextColor(mTextColor);
            }
            mInputContainer.setBackgroundResource(R.drawable.bg_input_container);
        }
    }

    public boolean isError() {
        return mIsError;
    }

    public void setTrailingIconClickListener(View.OnClickListener l) {
        if (mTrailingIcon.getVisibility() == VISIBLE && l != null) {
            mTrailingIcon.setOnClickListener(l);
        }
    }

    public String getText() {
        return mInput.getText().toString();
    }

    public void setText(String text) {
        mInput.setText(text);
    }

    public EditText getEditText() {
        return mInput;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mInput.setEnabled(enabled);
        mInputContainer.setEnabled(enabled);
    }
}
