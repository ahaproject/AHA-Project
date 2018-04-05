package project.aha.parent_panel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import project.aha.R;

public class AppWebViewClients extends WebViewClient {
    private ProgressDialog progressBar;

    public AppWebViewClients(Activity activity) {
        progressBar = new ProgressDialog(activity);
        progressBar.setMessage(activity.getString(R.string.please_wait));
        progressBar.setCancelable(true);
        progressBar.show();
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.hide();
    }
}
