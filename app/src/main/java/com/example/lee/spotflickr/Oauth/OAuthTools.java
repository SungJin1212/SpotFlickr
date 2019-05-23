package com.example.lee.spotflickr.Oauth;

import android.util.Log;

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
    public static final String REST_CONSUMER_KEY = "1b90c8442bc5d832d42f5a56f7ac6466";
    public static final String REST_CONSUMER_SECRET = "33155d41c8b1080d";
    public static final String REST_SIGN_METHOD = "HMAC-SHA1";
    public static final String OAUTH_VERSION = "1.0";
    public static String oauth_token;
    public static String oauth_token_secret;
    public static String oauth_verifier;

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
    public static String makeOAuthRequestURL() {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonce = "flickr_oauth" + String.valueOf(System.currentTimeMillis());        String signatureContentString = requestTokenString("http%3A%2F%2Fwww.example.com",nonce,timestamp);
        String signature=getSignature(REST_CONSUMER_SECRET+"&", signatureContentString);
        String requestString =
                REQUEST_TOKEN_REST_URL + "?"+
                        "oauth_nonce="+nonce + "&" +
                        "oauth_timestamp="+timestamp+ "&" +
                        "oauth_consumer_key="+REST_CONSUMER_KEY + "&" +
                        "oauth_signature_method="+REST_SIGN_METHOD + "&" +
                        "oauth_version="+OAUTH_VERSION + "&" +
                        "oauth_signature="+oauthEncode(signature) + "&" +
                        "oauth_callback="+"http%3A%2F%2Fwww.example.com";
        return requestString;
    }
    // return example : oauth_callback_confirmed=true&oauth_token=72157678285574577-3b153bf6c7c0318c&oauth_token_secret=6d1bc62e8e8c5d9f

    public static void getOauthToken(String url) {
        retrofit2.Call<String> stringCall = APIClient.getInstance().getService2().getStringResponse(url);
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseString = response.body();
                    String target1 = "oauth_token";
                    String target2 = "oauth_token_secret";

                    int target_num1 = responseString.indexOf(target1);
                    int target_num2 = responseString.indexOf(target2);
                    //String result; result = str.substring(target_num,(str.substring(target_num).indexOf("원")+target_num));
                    oauth_token =  responseString.substring(target_num1+target1.length()+1,(responseString.substring(target_num1).indexOf("&")+target_num1));
                    oauth_token_secret = responseString.substring(target_num2+target2.length()+1,responseString.length()-1);
                    Log.d("디버그",oauth_token +"");
                    Log.d("디버그",oauth_token_secret +"");
                    // todo: do something with the response string
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("디버그","oauth token fail");
            }
        });
    }
    public static void getOauthVerifier(String token) {
        retrofit2.Call<String> stringCall = APIClient.getInstance().getService2().getStringResponse("https://www.flickr.com/services/oauth/authorize?oauth_token=" + token);
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String responseString = response.body();
                String target = "oauth_verifier";
                int target_num = responseString.indexOf(target);
                oauth_verifier = responseString.substring(target_num+target.length()+1,responseString.length()-1);
                Log.d("디버그",oauth_verifier+"");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("디버그","verifier fail");

            }
        });
    }


}
