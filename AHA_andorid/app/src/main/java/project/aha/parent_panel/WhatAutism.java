package project.aha.parent_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        if(Constants.get_current_user_type(this) == Constants.ADMIN_TYPE){
            menu.findItem(R.id.chat_activity).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Constants.handleItemChoosed(this ,super.onOptionsItemSelected(item),item);
    }
}
