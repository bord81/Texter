package labut.md311.texter.network;


import android.net.Uri;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//OAUth browser flow support and API network calls interceptor
public class OAuthForm implements Interceptor {
    public final static String BASE_URL = "https://api.twitter.com/1.1/";
    public static final String GET_REQUEST_TOKENS = "r_tokens";
    public static final String GET_ACCESS_TOKENS = "a_tokens";
    public static final String AUTHORIZE_REQUEST = "auth_request";
    private final String UNSUPPORTED_REQUEST = "unsupp_request";
    private final String reqType;
    private final String tw_cons_key;
    private final String tw_priv_cons_key;
    private final String callbackUri;
    private final String oauthVerifier;
    private final String oauthToken;
    private final String oauthTokenSecret;
    private final String oauthEndPointUrl;
    private final Map<String, String> paramsMap = new TreeMap<>();
    private final String httpMethod;
    private final String sigMethod;
    private final String oauthVer;

    public OAuthForm(String reqType, String oauthEndPointUrl, String httpMethod, String sigMethod, String oauthVer, String cons_key, String cons_sec_key, String oauthToken, String oauthSecToken, String callbackUri, String oauthVerifier) {
        this.oauthEndPointUrl = oauthEndPointUrl;
        this.httpMethod = httpMethod;
        this.sigMethod = sigMethod;
        this.oauthVer = oauthVer;
        this.tw_cons_key = cons_key;
        this.tw_priv_cons_key = cons_sec_key;
        if (reqType.equals(GET_REQUEST_TOKENS)) {
            this.reqType = reqType;
            this.callbackUri = callbackUri;
            this.oauthVerifier = null;
            this.oauthToken = null;
            this.oauthTokenSecret = null;
        } else if (reqType.equals(GET_ACCESS_TOKENS)) {
            this.reqType = reqType;
            this.callbackUri = null;
            this.oauthVerifier = oauthVerifier;
            this.oauthToken = oauthToken;
            this.oauthTokenSecret = oauthSecToken;
        } else if (reqType.equals(AUTHORIZE_REQUEST)) {
            this.reqType = reqType;
            this.callbackUri = null;
            this.oauthVerifier = null;
            this.oauthToken = oauthToken;
            this.oauthTokenSecret = oauthSecToken;
        } else {
            this.reqType = UNSUPPORTED_REQUEST;
            this.callbackUri = null;
            this.oauthVerifier = null;
            this.oauthToken = null;
            this.oauthTokenSecret = null;
        }
    }

    public Request getHttpRequest() {
        if (reqType.equals(UNSUPPORTED_REQUEST)) {
            return null;
        }
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        String signKey = null;
        if (reqType.equals(GET_REQUEST_TOKENS)) {
            signKey = Uri.encode(tw_priv_cons_key) + "&";
        } else {
            signKey = Uri.encode(tw_priv_cons_key) + "&" + Uri.encode(oauthTokenSecret);
        }

        try {
            mac.init(new SecretKeySpec(signKey.getBytes(), mac.getAlgorithm()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
        SecureRandom random = new SecureRandom();
        byte[] nonce_array = new byte[32];
        random.nextBytes(nonce_array);
        int nonce_array_length = nonce_array.length;
        for (int i = 0; i < nonce_array_length; i++) {
            if (nonce_array[i] < 0) {
                nonce_array[i] = (byte) (nonce_array[i] * -1);
            }
            if (nonce_array[i] < 48) {
                int next = random.nextInt(10);
                nonce_array[i] = (byte) (48 + next);
            } else if (nonce_array[i] < 57 && nonce_array[i] > 48) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (65 + next);
            } else if (nonce_array[i] < 97 && nonce_array[i] > 57) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (97 + next);
            } else if (nonce_array[i] > 122) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (122 - next);
            }
        }
        String nonce = new String(nonce_array);
        StringBuilder paramString = new StringBuilder();
        StringBuilder finParamString = new StringBuilder();
        finParamString.append(httpMethod).append("&");
        finParamString.append(Uri.encode(oauthEndPointUrl));
        finParamString.append("&");
        paramsMap.put(Uri.encode("oauth_consumer_key"), Uri.encode(tw_cons_key));
        paramsMap.put(Uri.encode("oauth_nonce"), Uri.encode(nonce));
        paramsMap.put(Uri.encode("oauth_signature_method"), Uri.encode(sigMethod));
        paramsMap.put(Uri.encode("oauth_version"), Uri.encode(oauthVer));
        if (reqType.equals(GET_REQUEST_TOKENS)) {
            paramsMap.put(Uri.encode("oauth_callback"), Uri.encode(callbackUri));
        } else if (reqType.equals(GET_ACCESS_TOKENS)) {
            paramsMap.put(Uri.encode("oauth_verifier"), Uri.encode(oauthVerifier));
            paramsMap.put(Uri.encode("oauth_token"), Uri.encode(oauthToken));
        } else if (reqType.equals(AUTHORIZE_REQUEST)) {
            paramsMap.put(Uri.encode("oauth_token"), Uri.encode(oauthToken));
        }
        Long secondsEpoch = System.currentTimeMillis() / 1000;
        paramsMap.put(Uri.encode("oauth_timestamp"), Uri.encode(secondsEpoch.toString()));
        for (Map.Entry<String, String> entry : paramsMap.entrySet()
                ) {
            paramString.append(entry.getKey());
            paramString.append("=");
            paramString.append(entry.getValue());
            paramString.append("&");
        }
        paramString.deleteCharAt(paramString.length() - 1);
        finParamString.append(Uri.encode(paramString.toString()));
        byte[] ouath_sig_arr = Base64.encode(mac.doFinal(finParamString.toString().getBytes()), Base64.NO_WRAP);
        paramsMap.put(Uri.encode("oauth_signature"), Uri.encode(new String(ouath_sig_arr)));
        //forming the authorization request header and obtaining the request token
        StringBuilder authHeaderString = new StringBuilder();
        authHeaderString.append("OAuth ");
        if (reqType.equals(GET_ACCESS_TOKENS)) {
            paramsMap.remove("oauth_verifier");
        }
        for (Map.Entry<String, String> entry : paramsMap.entrySet()
                ) {
            authHeaderString.append(entry.getKey());
            authHeaderString.append("=\"");
            authHeaderString.append(entry.getValue());
            authHeaderString.append("\", ");
        }
        String authHeader = authHeaderString.toString().trim();
        authHeader = authHeader.substring(0, authHeader.length() - 1);
        MediaType type = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        Request requestOAUTHToken = null;
        if (reqType.equals(GET_REQUEST_TOKENS)) {
            requestOAUTHToken = new Request.Builder().url(oauthEndPointUrl)
                    .header("User-Agent", "TexterWi")
                    .header("Host", "api.twitter.com")
                    .header("Accept", "*/*")
                    .header("Authorization", authHeader)
                    .method(httpMethod, RequestBody.create(type, ""))
                    .build();
        } else if (reqType.equals(GET_ACCESS_TOKENS)) {
            requestOAUTHToken = new Request.Builder().url(oauthEndPointUrl)
                    .header("User-Agent", "TexterWi")
                    .header("Host", "api.twitter.com")
                    .header("Accept", "*/*")
                    .header("Authorization", authHeader)
                    .post(RequestBody.create(type, Uri.encode("oauth_verifier") + "=" + Uri.encode(oauthVerifier)))
                    .build();
        } else if (reqType.equals(AUTHORIZE_REQUEST)) {
            requestOAUTHToken = new Request.Builder().url(oauthEndPointUrl)
                    .header("User-Agent", "TexterWi")
                    .header("Host", "api.twitter.com")
                    .header("Accept", "*/*")
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method(httpMethod, null)
                    .build();
        }
        return requestOAUTHToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        String signKey = Uri.encode(tw_priv_cons_key) + "&" + Uri.encode(oauthTokenSecret);
        try {
            mac.init(new SecretKeySpec(signKey.getBytes(), mac.getAlgorithm()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
        SecureRandom random = new SecureRandom();
        byte[] nonce_array = new byte[32];
        random.nextBytes(nonce_array);
        int nonce_array_length = nonce_array.length;
        for (int i = 0; i < nonce_array_length; i++) {
            if (nonce_array[i] < 0) {
                nonce_array[i] = (byte) (nonce_array[i] * -1);
            }
            if (nonce_array[i] < 48) {
                int next = random.nextInt(10);
                nonce_array[i] = (byte) (48 + next);
            } else if (nonce_array[i] < 57 && nonce_array[i] > 48) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (65 + next);
            } else if (nonce_array[i] < 97 && nonce_array[i] > 57) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (97 + next);
            } else if (nonce_array[i] > 122) {
                int next = random.nextInt(26);
                nonce_array[i] = (byte) (122 - next);
            }
        }
        String nonce = new String(nonce_array);
        StringBuilder paramString = new StringBuilder();
        StringBuilder finParamString = new StringBuilder();
        finParamString.append(httpMethod).append("&");
        String urlFull = request.url().toString();
        String[] urlParts = urlFull.split(Pattern.quote("?"));
        finParamString.append(Uri.encode(urlParts[0]));
        finParamString.append("&");
        Set<String> q_params = request.url().queryParameterNames();
        for (String s : q_params
                ) {
            paramsMap.put(Uri.encode(s), Uri.encode(request.url().queryParameter(s)));
        }
        paramsMap.put(Uri.encode("oauth_consumer_key"), Uri.encode(tw_cons_key));
        paramsMap.put(Uri.encode("oauth_nonce"), Uri.encode(nonce));
        paramsMap.put(Uri.encode("oauth_signature_method"), Uri.encode(sigMethod));
        paramsMap.put(Uri.encode("oauth_version"), Uri.encode(oauthVer));
        paramsMap.put(Uri.encode("oauth_token"), Uri.encode(oauthToken));
        Long secondsEpoch = System.currentTimeMillis() / 1000;
        paramsMap.put(Uri.encode("oauth_timestamp"), Uri.encode(secondsEpoch.toString()));
        for (Map.Entry<String, String> entry : paramsMap.entrySet()
                ) {
            paramString.append(entry.getKey());
            paramString.append("=");
            paramString.append(entry.getValue());
            paramString.append("&");
        }
        paramString.deleteCharAt(paramString.length() - 1);
        finParamString.append(Uri.encode(paramString.toString()));
        byte[] ouath_sig_arr = Base64.encode(mac.doFinal(finParamString.toString().getBytes()), Base64.NO_WRAP);
        paramsMap.put(Uri.encode("oauth_signature"), Uri.encode(new String(ouath_sig_arr)));
        //forming the authorization request header and obtaining the request token
        StringBuilder authHeaderString = new StringBuilder();
        authHeaderString.append("OAuth ");

        for (Map.Entry<String, String> entry : paramsMap.entrySet()
                ) {
            authHeaderString.append(entry.getKey());
            authHeaderString.append("=\"");
            authHeaderString.append(entry.getValue());
            authHeaderString.append("\", ");
        }
        String authHeader = authHeaderString.toString().trim();
        authHeader = authHeader.substring(0, authHeader.length() - 1);
        MediaType type = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        if (urlParts[0].contains("create") || urlParts[0].contains("destroy") || (urlParts[0].contains("retweet") && !urlParts[0].contains("retweets"))) {
            request = request.newBuilder()
                    .header("User-Agent", "TexterWi")
                    .header("Host", "api.twitter.com")
                    .header("Accept", "*/*")
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method(httpMethod, RequestBody.create(type, ""))
                    .build();
        } else {
            request = request.newBuilder()
                    .header("User-Agent", "TexterWi")
                    .header("Host", "api.twitter.com")
                    .header("Accept", "*/*")
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method(httpMethod, null)
                    .build();
        }
        return chain.proceed(request);
    }
}
