package com.daniel.requestservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.requestservices.Utils.Common;
import com.daniel.requestservices.Utils.DBManager;
import com.daniel.requestservices.Utils.VolleySingleton;
import com.daniel.requestservices.Utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Context mCtx;

    Spinner mSpUrlSaved;

    List<String> mUrlsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCtx = this;

        VolleySingleton.getInstance(mCtx);
        final LinearLayout linearParams = findViewById(R.id.linear_et);
        linearParams.setVisibility(View.GONE);
        final String[] selectedMethod = new String[1];
        RadioGroup rgMethodOption = findViewById(R.id.rg_method_opt);
        rgMethodOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rb_post) {
                    selectedMethod[0] = "p";
                    linearParams.setVisibility(View.VISIBLE);
                } else {
                    selectedMethod[0] = "g";
                    linearParams.setVisibility(View.GONE);
                }
            }
        });

        final EditText etUrl = findViewById(R.id.et_url);

        mSpUrlSaved = findViewById(R.id.sp_url);

        ArrayAdapter<String> Adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mUrlsList);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpUrlSaved.setAdapter(Adapter);

        mSpUrlSaved.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String url = parent.getItemAtPosition(position).toString();
                etUrl.setText(url, TextView.BufferType.EDITABLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText etName = findViewById(R.id.request_name);
        final EditText etLastN1 = findViewById(R.id.request_last_name_1);
        final EditText etLastN2 = findViewById(R.id.request_last_name_2);
        final EditText etBirth = findViewById(R.id.request_birth);
        final EditText etGender = findViewById(R.id.request_gender);
        final EditText etCity = findViewById(R.id.request_city);
        final EditText etInd = findViewById(R.id.request_ind);
        final EditText etCurp = findViewById(R.id.request_curp);


        Button btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMethod[0] != null ) {
                    String url = etUrl.getText().toString();
                    Map<String, String> params = new HashMap<>();

                    if (selectedMethod[0].equals("p")) {
                        String name = etName.getText().toString();
                        String lastName1 = etLastN1.getText().toString();
                        String lastName2 = etLastN2.getText().toString();
                        String birth = etBirth.getText().toString();
                        String gender = etGender.getText().toString();
                        String city = etCity.getText().toString();
                        String ind = etInd.getText().toString();
                        String curp = etCurp.getText().toString();

                        if (TextUtils.isEmpty(name)) {
                            name = "";
                        } else if (TextUtils.isEmpty(lastName1)) {
                            lastName1 = "";
                        } else if (TextUtils.isEmpty(lastName2)) {
                            lastName2 = "";
                        } else if (TextUtils.isEmpty(birth)) {
                            birth = "";
                        } else if (TextUtils.isEmpty(gender)) {
                            gender = "";
                        } else if (TextUtils.isEmpty(city)) {
                            city = "";
                        } else if (TextUtils.isEmpty(ind)) {
                            ind = "";
                        } else if (TextUtils.isEmpty(curp)) {
                            curp = "";
                        }

                        params.put("nombres", name);
                        params.put("aPaterno", lastName1);
                        params.put("aMaterno", lastName2);
                        params.put("fechNac", birth);
                        params.put("sexo", gender);
                        params.put("cveEntidadNac", city);
                        params.put("indRENAPO", ind);
                        params.put("curp", curp);
                    }

                    saveInBD(url);
                    urlRequested(url, selectedMethod[0], params);
                } else {
                    Toast.makeText(mCtx, "DEBES SELECCIONAR UN METODO DE ENVIO",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveInBD(String url) {
        DBManager dbManager = new DBManager(mCtx);
        Cursor cursorUrl = dbManager.getAllUrl();

        if (cursorUrl.moveToFirst()) {
            Log.d(Common.DEV_LOG, "BASE DE DATOS NO VACIA");
            do {
                String pathUrl = cursorUrl.getString(1);
                if (!url.equals(pathUrl)) {
                    dbManager.insertUrl(url);
                }
                mUrlsList.add(pathUrl);
            } while (cursorUrl.moveToNext());
        } else {
            Log.d(Common.DEV_LOG, "BASE DE DATOS VACIA");
            dbManager.insertUrl(url);
        }
    }

    private void urlRequested(String url, String selectedMethod, Map<String, String> params) {
        final ProgressDialog pDialog = new ProgressDialog(mCtx);
        pDialog.setMessage("Cargando");
        pDialog.show();
        WebService.sendRequestedUrl(mCtx, url, selectedMethod, params, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.d(Common.DEV_LOG, response);
                    if (response.equals("400") && response.equals("500")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pDialog.dismiss();
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mCtx);
                                alertBuilder.setMessage(getString(R.string.requirement));
                                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                final AlertDialog dialog = alertBuilder.create();
                                dialog.show();
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        JSONObject jsonResponse = new JSONObject(response);
                        Log.d(Common.DEV_LOG, "response del json: " + jsonResponse);

                        final String jsonsStringResponse = jsonResponse.getString("response");

                        String jsonsStringStatus = jsonResponse.getString("status");
                        JSONObject jsonArrayStatus = new JSONObject(jsonsStringStatus);
                        for (int i = 0; i < 1; i++) {
                            int code = jsonArrayStatus.getInt("code");
                            if (code == Common.RESPONSE_OK) {
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intentActivity = new Intent(mCtx, ParamsActivity.class);
                                        intentActivity.putExtra("response", jsonsStringResponse);
                                        startActivity(intentActivity);
                                    }
                                });
                            }
                        }
                    }

                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                pDialog.dismiss();
            }
        });
    }

}
