package com.example.trackingmypantry;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnEntra;
    private TextView txtRegistrazione;
    public String accessToken;

    TextInputLayout emailTextInputLayout;
    TextInputLayout passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //nasconde l'action bar dell'applicazione
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_main);
        btnEntra = findViewById(R.id.buttonEntra);
        btnEntra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTextInputLayout = findViewById(R.id.email);
                passwordTextInputLayout = findViewById(R.id.password);
                String emailString = emailTextInputLayout.getEditText().getText().toString();
                String passwordString = passwordTextInputLayout.getEditText().getText().toString();
                if(emailString.matches("") == false
                        && passwordString.matches("") == false) { //se l'utente ha compilato tutti i campi

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://lam21.modron.network/auth/login";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("email", emailString);
                        jsonBody.put("password", passwordString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("LOGIN", "Login effettuato");
                                    try {
                                        accessToken = (String) response.get("accessToken");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(MainActivity.this, SelectionActivity.class);
                                    i.putExtra("EXTRA_ACCESS_TOKEN", accessToken);
                                    i.putExtra("EXTRA_USERID", emailString);
                                    startActivity(i);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("LOGIN", "Login non effettuato: error code " + error.networkResponse.statusCode);
                                    emailTextInputLayout.setError(null);
                                    passwordTextInputLayout.setError(null);
                                    emailTextInputLayout.setError("Campo obbligatorio");
                                    passwordTextInputLayout.setError("Campo obbligatorio");
                                }
                            });
                    requestQueue.add(jsonObjectRequest);
                }
                emailTextInputLayout.setError(null);
                passwordTextInputLayout.setError(null);
                if (emailString.matches("")){
                    Log.e("LOGIN", "Login non effettuato: errore campo email non compilato");
                    emailTextInputLayout.setError("Campo obbligatorio");
                }
                if (passwordString.matches("")){
                    Log.e("LOGIN", "Login non effettuato: errore campo email non compilato");
                    passwordTextInputLayout.setError("Campo obbligatorio");
                }
            }
        });

        txtRegistrazione = findViewById(R.id.textRegistrazione);
        txtRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });
    }
}