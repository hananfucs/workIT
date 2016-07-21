package com.hf.workit.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;

import org.w3c.dom.Text;

/**
 * Created by hanan on 07/07/16.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.about_activity);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        LinearLayout versionLayout = (LinearLayout)findViewById(R.id.version_layout);

        TextView title = new TextView(versionLayout.getContext());
        title.setText(getResources().getString(R.string.version));

        TextView versionText = new TextView(versionLayout.getContext());
        versionText.setText(getResources().getString(R.string.last_workout));

        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        title.setLayoutParams(lparams);
        versionText.setLayoutParams(lparams);
        versionText.setText(version);

        if(Constatnts.HEB) {
            versionLayout.addView(title, 0);
            versionLayout.addView(versionText, 0);
        } else {
            versionLayout.addView(versionText, 0);
            versionLayout.addView(title, 0);
        }


//        TextView versionText = (TextView)findViewById(R.id.version_text);
//        versionText.setText(version);
    }

    public void contactHanan(View v) {
        String[] address = {"hananfu@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void googleCredit(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://design.google.com/icons/index.html"));
        startActivity(browserIntent);
    }

    public void chartsCredit(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lecho/hellocharts-android"));
        startActivity(browserIntent);
    }

    public void pasteCredit(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paste.ee/"));
        startActivity(browserIntent);
    }
}
