package com.greenshadow.costbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.greenshadow.costbook.CostBookApp;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.utils.Log;

public class IntoActivity extends BaseActivity implements View.OnClickListener {
    private static final int WHAT_DEC = 1;
    private static final int WHAT_SKIP = 0;

    private Button mSkipButton;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DEC:
                    if (isFinishing()) {
                        Log.d(this, "Activity finishing, skip.");
                        return;
                    }

                    int decSec = msg.arg1;
                    if (decSec >= 0) {
                        mSkipButton.setText(getString(R.string.skip_into, decSec--));

                        Message newMsg = obtainMessage();
                        newMsg.copyFrom(msg);
                        newMsg.arg1 = decSec;
                        sendMessageDelayed(newMsg, 1000);
                        break;
                    }
                case WHAT_SKIP:
                    jumpToHome();
                    break;
                default:
                    Log.w(this, "Unknown message : " + msg.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean enable = getSharedPreferences().getBoolean(CostBookApp.PREF_KEY_WELCOME_PAGE,
                CostBookApp.DEFAULT_WELCOME_ENABLE);
        if (!enable) {
            jumpToHome();
            return;
        }

        setContentView(R.layout.activity_into);
        mSkipButton = findViewById(R.id.into_skip);
        mSkipButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int skipDelay = getResources().getInteger(R.integer.into_skip_delay_seconds);
        mSkipButton.setText(getString(R.string.skip_into, skipDelay));
        mSkipButton.postDelayed(() -> {
            Message message = mHandler.obtainMessage();
            message.arg1 = skipDelay;
            message.what = WHAT_DEC;
            message.sendToTarget();
        }, 10);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.into_skip) {
            jumpToHome();
            return;
        }

        Log.w(this, "Unknown view : " + v);
    }

    private void jumpToHome() {
        mHandler.removeMessages(WHAT_DEC);
        startActivity(new Intent(IntoActivity.this, HomeActivity.class));
        finish();
    }
}
