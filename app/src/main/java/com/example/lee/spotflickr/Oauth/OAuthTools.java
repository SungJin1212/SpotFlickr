package com.example.lee.spotflickr.Oauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lee.spotflickr.retrofit.APIClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OAuthTools {
    public static final String REQUEST_TOKEN_REST_URL = "https://www.flickr.com/services/oauth/request_token";
    public static final String ACCESS_TOKEN_REST_URL = "https://www.flickr.com/services/oauth/access_token";
    public static final String REST_CONSUMER_KEY = "1b90c8442bc5d832d42f5a56f7ac6466";
    public static final String REST_CONSUMER_SECRET = "33155d41c8b1080d";
    public static final String REST_SIGN_METHOD = "HMAC-SHA1";
    public static final String OAUTH_VERSION = "1.0";
    public static final String OAUTH_CALLBACK = "oauth:/";
    public static String oauth_request_token = null;
    public static String oauth_request_token_secret = null;
    public static String oauth_access_token = null;
    public static String oauth_access_token_secret = null;
    public static String oauth_verifier = null;
    public static Context oAuthContext;
    private static boolean oAuth_lock=false;

    private static String oauthEncode(String input) {
        Map<String, String> oathEncodeMap = new HashMap<>();
        oathEncodeMap.put("\\*", "%2A");
        oathEncodeMap.put("\\+", "%20");
        oathEncodeMap.put("%7E", "~");
        String encoded = "";
        try {
            encoded = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : oathEncodeMap.entrySet()) {
            encoded = encoded.replaceAll(entry.getKey(), entry.getValue());
        }
        return encoded;
    }

    private static String getSignature(String key, String data)
    {
        final String HMAC_ALGORITHM = "HmacSHA1";
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), HMAC_ALGORITHM);
        Mac macInstance = null;
        try {
            macInstance = Mac.getInstance(HMAC_ALGORITHM);
            macInstance.init(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] signedBytes = macInstance.doFinal(data.getBytes());
        return android.util.Base64.encodeToString(signedBytes, android.util.Base64.DEFAULT).trim();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static String requestTokenString(
            String oauth_callback,
            String oauth_nonce,
            String oauth_timestamp) {
        String unencBaseString3 = "oauth_callback="+oauth_callback + "&" +
                "oauth_consumer_key="+REST_CONSUMER_KEY + "&" +
                "oauth_nonce="+oauth_nonce + "&" +
                "oauth_signature_method="+REST_SIGN_METHOD + "&" +
                "oauth_timestamp="+oauth_timestamp + "&" +
                "oauth_version="+OAUTH_VERSION;
        return "GET&"+oauthEncode(REQUEST_TOKEN_REST_URL)+"&"+oauthEncode(unencBaseString3);
    }
    // https://www.flickr.com/services/oauth/request_token?oauth_nonce=89601180&oauth_timestamp=1305583298&oauth_consumer_key=2f006fbb54d3272c931201aa677b4b04&oauth_signature_method=HMAC-SHA1&oauth_version=1.0&oauth_signature=WAIJ%2FOvy6UlCyNNLAYLL0ofUFcE%3D&oauth_callback=http%3A%2F%2Fwww.example.com
    private static String makeOAuthRequestURL() {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonce = "flickr_oauth" + String.valueOf(System.currentTimeMillis());
        String signatureContentString = requestTokenString(oauthEncode(OAUTH_CALLBACK),nonce,timestamp);
        String signature=getSignature(REST_CONSUMER_SECRET+"&", signatureContentString);
        String requestString =
                REQUEST_TOKEN_REST_URL + "?"+
                        "oauth_nonce="+nonce + "&" +
                        "oauth_timestamp="+timestamp+ "&" +
                        "oauth_consumer_key="+REST_CONSUMER_KEY + "&" +
                        "oauth_signature_method="+REST_SIGN_METHOD + "&" +
                        "oauth_version="+OAUTH_VERSION + "&" +
                        "oauth_signature="+oauthEncode(signature) + "&" +
                        "oauth_callback="+oauthEncode(OAUTH_CALLBACK);
        return requestString;
    }
    // return example : oauth_callback_confirmed=true&oauth_token=72157678285574577-3b153bf6c7c0318c&oauth_token_secret=6d1bc62e8e8c5d9f
    private static void getOauthToken(Context c, String url) {
        if(oAuth_lock) {
            Toast.makeText(c, "oauth request already sent, please wait...", Toast.LENGTH_LONG).show();
            Log.d("Debug","HJ Debug:oAuthAlreadyLocked");
            return;
        } else {
            oAuth_lock = true;
            Log.d("Debug","HJ Debug:oAuthLocked");
        }
        retrofit2.Call<String> stringCall = APIClient.getInstance().getService2().getStringResponse(url);
        Log.d("Debug","HJ Debug:getOauthToken:"+url);
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Debug","HJ Debug:getOauthToken:onResponse");
                if (response.isSuccessful()) {
                    String responseString = response.body();
                    String targetbase = "fullname";
                    int isAccessToken = responseString.indexOf(targetbase);
                    String target1 = "oauth_token";
                    String target2 = "oauth_token_secret";
                    int target_num1 = responseString.indexOf(target1);
                    int target_num2 = responseString.indexOf(target2);
                    Log.d("Debug","HJ Debug:getOauthToken:"+responseString);
                    if(isAccessToken==-1) {
                        oauth_request_token =  responseString.substring(target_num1+target1.length()+1,(responseString.substring(target_num1).indexOf("&")+target_num1));
                        oauth_request_token_secret = responseString.substring(target_num2+target2.length()+1,responseString.length());
                        reqOauthVerifier();
                    } else {
                        oauth_access_token =  responseString.substring(target_num1+target1.length()+1,(responseString.substring(target_num1).indexOf("&")+target_num1));
                        oauth_access_token_secret = responseString.substring(target_num2+target2.length()+1,(responseString.substring(target_num2).indexOf("&")+target_num2));
                        Log.d("Debug","HJ Debug:getOauthToken:All setting finishied");
                        oAuth_lock = false;
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("디버그","oauth token fail");
                Log.d("Debug","HJ Debug:oauth token fail");
                oAuth_lock = false;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void reqOauthVerifier() {
        Log.d("Debug","HJ Debug:reqOauthVerifier");
        String userAuthUrl = "https://www.flickr.com/services/oauth/authorize?oauth_token="+oauth_request_token;
        Intent intent = new Intent(OAuthTools.oAuthContext, WebAuthActivity.class);
        Bundle extras = new Bundle();
        extras.putString("Url",userAuthUrl);
        intent.putExtras(extras);
        OAuthTools.oAuthContext.startActivity(intent);
    }
    public static void setUserAuthToken(String token, String verifier) {
        oauth_request_token = token;
        oauth_verifier = verifier;
    }
    public static void reqAccessToken() {
        oAuth_lock = false;
        getOauthToken(oAuthContext, makeOAuthAccessURL());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static String accessTokenString(
            String oauth_nonce,
            String oauth_timestamp) {
        String unencBaseString3 =
                "oauth_consumer_key="+REST_CONSUMER_KEY+"&"+
                "oauth_nonce="+oauth_nonce+"&"+ "oauth_signature_method="+REST_SIGN_METHOD+"&"+
                "oauth_timestamp="+oauth_timestamp+"&"+ "oauth_token="+oauth_request_token +"&"+
                "oauth_verifier="+oauth_verifier+"&"+
                "oauth_version="+OAUTH_VERSION;
        return "GET&"+oauthEncode(ACCESS_TOKEN_REST_URL)+"&"+oauthEncode(unencBaseString3);
    }
    private static String makeOAuthAccessURL() {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonce = "flickr_oauth" + String.valueOf(System.currentTimeMillis());
        String signatureContentString = accessTokenString(nonce,timestamp);
        Log.d("Debug", "HJ Debug:signatureContentString:"+signatureContentString);
        String signature=getSignature(REST_CONSUMER_SECRET+"&"+oauth_request_token_secret, signatureContentString);
        String requestString =
                ACCESS_TOKEN_REST_URL + "?"+
                        "oauth_consumer_key="+REST_CONSUMER_KEY+"&"+
                        "oauth_nonce="+nonce+"&"+ "oauth_signature_method="+REST_SIGN_METHOD+"&"+
                        "oauth_timestamp="+timestamp+"&"+ "oauth_token="+oauth_request_token +"&"+
                        "oauth_verifier="+oauth_verifier+"&"+
                        "oauth_version="+OAUTH_VERSION+"&"+
                        "oauth_signature="+oauthEncode(signature);
        Log.d("Debug", "HJ Debug:makeOAuthAccessURL:"+requestString);
        return requestString;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void getInstance(Context c) {
        oAuthContext = c;
        if(oauth_request_token == null || oauth_request_token_secret == null) {
            Log.d("Debug", "HJ Debug:getInstance:tokenNotCreated");
            getOauthToken(c, makeOAuthRequestURL());
        } else if(oauth_verifier == null) {
            Log.d("Debug", "HJ Debug:getInstance:tokenNotVerified");
            reqOauthVerifier();
        } else if(oauth_access_token == null || oauth_access_token_secret == null) {
            Log.d("Debug", "HJ Debug:getInstance:accessTokenNotCreated");
            getOauthToken(c, makeOAuthAccessURL());
        } else {
            Log.d("Debug", "HJ Debug:getInstance:tokenAlreadyVerified");
        }
    }
    public static void clearInstance() {
        Log.d("Debug", "HJ Debug:clearInstance:removeToken");
        oauth_request_token = null;
        oauth_request_token_secret = null;
        oauth_verifier = null;
        oauth_access_token = null;
        oauth_access_token_secret = null;
    }


}
