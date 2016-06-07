package com.csatimes.dojma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the url passed from Herald on item click.
 * It uses webView to load the url and also sets a title to the actionbar.
 */
public class OpenWebpage extends AppCompatActivity {
    private static String currentURL;
    private static List<String> urlList;
    private static List<String> titleList;
    private static int POSITION;
    private WebView webView;
    private ProgressBar downloadProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_webpage);
        webView = (WebView) findViewById(R.id.webView);
        downloadProgressBar = (ProgressBar) findViewById(R.id.webPageProgressBar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("CSATimes");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color
                    .colorPrimaryDark)));

        }
        POSITION = 0;
        urlList = new ArrayList<>();
        titleList = new ArrayList<>();

        Intent intent = getIntent();
        if (actionBar != null) actionBar.setTitle(intent.getStringExtra("TITLE"));
        currentURL = intent.getStringExtra("URL");
        urlList.add(currentURL);
        titleList.add(intent.getStringExtra("TITLE"));
        POSITION++;
        /**
         * The following codes are for the webView's settings
         */
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().supportZoom();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        //finally load webview
        webView.loadUrl(intent.getStringExtra("URL"));
        //in case user clicks on links within the webview
        //because of the following code it will be handled inside webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) actionBar.setTitle("Loading...");
                currentURL = url;
                urlList.add(currentURL);
                POSITION++;
                view.loadUrl(urlList.get(POSITION - 1));
                if (actionBar != null) actionBar.setTitle("CSATimes");
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                downloadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                downloadProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Snackbar.make(view, "HTTP error from server! Try again later. If the problem " +
                        "persists pls" +
                        " report", Snackbar.LENGTH_LONG).show();
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                downloadProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (actionBar != null)
                    actionBar.setTitle(title);
                titleList.add(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });


    }

    /**
     * This method(overridden) handles onBack pressing.If webView has history it will go back
     * otherwise it
     * will return to calling class - Herald in this case
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            POSITION--;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(titleList.get(POSITION));
            return;
        }
        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open_in_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlList.get(POSITION - 1)));
            startActivity(Intent.createChooser(intent, "Open in browser"));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_page_menu, menu);
        return true;
    }
}