package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.uihyun.medic.R;

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class InfoActivity extends Activity {

    private TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        infoText = (TextView) findViewById(R.id.info_detail);

        Intent intent = getIntent();
        int idxAbout = (int) intent.getSerializableExtra("about");

        switch (idxAbout) {
            case AboutActivity.ABOUT_INFO:
                infoText.setText(R.string.about_info);
                break;
            case AboutActivity.ABOUT_FEEDBACK:
                infoText.setText(R.string.about_feedback);
                break;
            case AboutActivity.ABOUT_VERSION:
                infoText.setText(R.string.about_version);
                break;
            default:
                infoText.setText("");
                break;
        }
    }
}
