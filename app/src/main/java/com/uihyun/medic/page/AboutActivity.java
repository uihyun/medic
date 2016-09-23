package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.uihyun.medic.R;

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class AboutActivity extends Activity {

    public static final int ABOUT_INFO = 0;
    public static final int ABOUT_FEEDBACK = 1;
    public static final int ABOUT_VERSION = 2;

    private TextView aboutText;
    private TextView feedbackText;
    private TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutText = (TextView) findViewById(R.id.info_text);
        feedbackText = (TextView) findViewById(R.id.feedback_text);
        versionText = (TextView) findViewById(R.id.version_text);

        aboutText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("about", ABOUT_INFO);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        feedbackText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("about", ABOUT_FEEDBACK);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        versionText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("about", ABOUT_VERSION);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
