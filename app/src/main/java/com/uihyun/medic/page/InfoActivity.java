package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
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
        infoText.setMovementMethod(LinkMovementMethod.getInstance());

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
                try {
                    infoText.setText(getString(R.string.about_version) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                infoText.setText("");
                break;
        }
    }
}
