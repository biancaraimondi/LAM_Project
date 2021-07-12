package com.example.trackingmypantry;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    Button buttonRegistrati;
    TextInputLayout usernameRegistrationTextInputLayout;
    TextInputLayout emailRegistrationTextInputLayout;
    TextInputLayout passwordRegistrationTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //nasconde l'action bar dell'applicazione
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_registration);

        materialToolbar = findViewById(R.id.topAppBar);
        materialToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        buttonRegistrati = findViewById(R.id.buttonRegistrati);
        buttonRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameRegistrationTextInputLayout = findViewById(R.id.usernameRegistration);
                emailRegistrationTextInputLayout = findViewById(R.id.emailRegistration);
                passwordRegistrationTextInputLayout = findViewById(R.id.passwordRegistration);
                String usernameString = usernameRegistrationTextInputLayout.getEditText().getText().toString();
                String emailString = emailRegistrationTextInputLayout.getEditText().getText().toString();
                String passwordString = passwordRegistrationTextInputLayout.getEditText().getText().toString();
                if(usernameString.matches("") == false
                    && emailString.matches("") == false
                        && passwordString.matches("") == false) { //se l'utente ha compilato tutti i campi

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://lam21.modron.network/users";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", usernameString);
                        jsonBody.put("email", emailString);
                        jsonBody.put("password", passwordString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("REGISTRATION", "Registrazione effettuata");


                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, R.string.text_label, Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.action_text, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                }
                                            })
                                            .show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("REGISTRATION", "Registrazione non effettuata: error code " + error.networkResponse.statusCode);
                                }
                            });
                    requestQueue.add(jsonObjectRequest);
                }
                usernameRegistrationTextInputLayout.setError(null);
                emailRegistrationTextInputLayout.setError(null);
                passwordRegistrationTextInputLayout.setError(null);
                if (usernameString.matches("")){
                    Log.e("REGISTRATION", "Registrazione non effettuata: errore campo username non compilato");
                    usernameRegistrationTextInputLayout.setError("Campo obbligatorio");
                }
                if (emailString.matches("")){
                    Log.e("REGISTRATION", "Registrazione non effettuata: errore campo email non compilato");
                    emailRegistrationTextInputLayout.setError("Campo obbligatorio");
                }
                if (passwordString.matches("")){
                    Log.e("REGISTRATION", "Registrazione non effettuata: errore campo password non compilato");
                    passwordRegistrationTextInputLayout.setError("Campo obbligatorio");
                }
            }
        });
    }
}