package com.example.lee.spotflickr.Oauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.lee.spotflickr.R;

public class WebAuthActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String url = extras.getString("Url");

        setContentView(R.layout.activity_web_auth);

        mWebView = (WebView)findViewById(R.id.webAuthView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClientClass());
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Debug","HJ Debug:"+url);

            //http://www.example.com/?oauth_token=72157708739717162-86a7e4b2238c3046&oauth_verifier=afdb3b52a92935c
            if(url.startsWith("oauth:/")) {
                String target1 = "oauth_token";
                String target2 = "oauth_verifier";
                int target_num1 = url.indexOf(target1);
                int target_num2 = url.indexOf(target2);
                //String result; result = str.substring(target_num,(str.substring(target_num).indexOf("원")+target_num));
                String token =  url.substring(target_num1+target1.length()+1,(url.substring(target_num1).indexOf("&")+target_num1));
                String verifier = url.substring(target_num2+target2.length()+1,url.length()-1);
                OAuthTools.setVerifier(token, verifier);
                Log.d("Debug", "HJ Debug:Token Verified::"+verifier);
                finish();
                return true;
            }
            return false;
        }
    }
}