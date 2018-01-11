package com.daniel.requestservices.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 10/01/18.
 */

public class WebService {
    private static int DEFAULT_TIME = 40000;

    public interface RequestListener {
        void onSuccess(String response);
        void onError();
    }

    public static void sendRequestedUrl(final Context context,
                                        final String urlRequested,
                                        final String method,
                                        final Map<String, String> params,
                                        final RequestListener requestListener) {

        final String methodRequested;
        if (method.equals("p")) {
            methodRequested = "POST";
        } else {
            methodRequested = "GET";
        }

        StringRequest sendRequestedUrlAction = null;
        if (method.equals("g")) {
            String url = String.format("%s", urlRequested);
            sendRequestedUrlAction = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!isExpectedJson(response)) {
                                requestListener.onError();
                            }

                            requestListener.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError();
                }
            });

            assert sendRequestedUrlAction != null;
            sendRequestedUrlAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIME,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(sendRequestedUrlAction);
        } else {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {

                        URL url = new URL(urlRequested);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(methodRequested);
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoInput(true);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                        if (method.equals("p")) {
                            JSONObject jsonParam = new JSONObject(params);
                            os.writeBytes(jsonParam.toString());
                        }

                        os.flush();
                        os.close();

                        Log.d("STATUS", String.valueOf(conn.getResponseCode()));
                        Log.d("MSG" , conn.getResponseMessage());

                        conn.disconnect();
                        if (conn.getResponseCode() == 200) {

                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String output;
                            while ((output = br.readLine()) != null) {
                                sb.append(output);
                            }
                            Log.d("SERVER RESPONSE ----- ", sb.toString());

                            requestListener.onSuccess(sb.toString());
                        } else {
                            requestListener.onSuccess(String.valueOf(conn.getResponseCode()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };


            task.execute();
        }
    }








    private static boolean isExpectedJson(String response){
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String code = jsonResponse.getString("code");
            if (null == code) {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }
}
