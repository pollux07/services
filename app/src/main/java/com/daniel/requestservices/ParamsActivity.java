package com.daniel.requestservices;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParamsActivity extends AppCompatActivity {
    Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
        mCtx = this;

        final ListView listview = findViewById(R.id.params_list);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String response = extras.getString("response");

        try {
            final List<String> paramsList = new ArrayList<>();
            JSONObject jsonResponse = new JSONObject(response);

            Iterator<?> keys = jsonResponse.keys();

            while( keys.hasNext() ) {
                String k = (String)keys.next();
                Log.i("Info", "Key: " + k + ", value: " + jsonResponse.getString(k));
                paramsList.add(k + "=" + jsonResponse.getString(k));

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, paramsList);
            listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
