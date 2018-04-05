package project.aha.parent_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import project.aha.Constants;
import project.aha.R;

public class WhatAutism extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_autism);
        Constants.showLogo(this);

        WebView wv = (WebView)findViewById(R.id.what_is_autism_webview);
        wv.setWebViewClient(new AppWebViewClients(this));
        wv.loadUrl(Constants.WHAT_IS_AUTISM_URL);
    }
}
