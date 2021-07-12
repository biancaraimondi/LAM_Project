package com.example.trackingmypantry;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewProductFragment extends Fragment {

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout descriptionTextInputLayout;
    private ImageView imageView;
    private Button btnInserisciImmagine;
    private Button btnCrea;
    private String accessToken;
    private String token;
    private String barcode;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap resized = ThumbnailUtils.extractThumbnail(imageBitmap, 250, 250);
            imageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            ((SelectionActivity) getActivity()).mProductsViewModel.setImage(Base64.encodeToString(byteArray, Base64.DEFAULT));

            View parentLayout = getActivity().findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Immagine caricata con successo", Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_text, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //close the snackbar
                        }
                    })
                    .show();
        }
    }

    public NewProductFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accessToken = getActivity().getIntent().getExtras().getString("EXTRA_ACCESS_TOKEN");
        token = getArguments().getString("EXTRA_TOKEN");
        barcode = getArguments().getString("EXTRA_BARCODE");
        imageView = getView().findViewById(R.id.imageViewNewProduct);

        nameTextInputLayout = getView().findViewById(R.id.textNameNewProduct);
        descriptionTextInputLayout = getView().findViewById(R.id.textDescriptionNewProduct);
        if (((SelectionActivity) getActivity()).mProductsViewModel.getNameDescriptionNewProduct() != new String[]{null, null}){
            nameTextInputLayout.getEditText().setText(((SelectionActivity) getActivity()).mProductsViewModel.getNameDescriptionNewProduct()[0]);
            descriptionTextInputLayout.getEditText().setText(((SelectionActivity) getActivity()).mProductsViewModel.getNameDescriptionNewProduct()[1]);
        }

        if (((SelectionActivity) getActivity()).mProductsViewModel.getImage() != null){
            byte[] decodedString = Base64.decode(((SelectionActivity) getActivity()).mProductsViewModel.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        btnInserisciImmagine = getView().findViewById(R.id.buttonImageNewProduct);
        btnInserisciImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnCrea = getView().findViewById(R.id.buttonNewProduct);
        btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameTextInputLayout = getView().findViewById(R.id.textNameNewProduct);
                String name = nameTextInputLayout.getEditText().getText().toString();
                descriptionTextInputLayout = getView().findViewById(R.id.textDescriptionNewProduct);
                String description = nameTextInputLayout.getEditText().getText().toString();

                if(name.matches("") == false && description.matches("") == false) { //se l'utente ha compilato il campo

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    String url = "https://lam21.modron.network/products";

                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("token", token);
                        jsonBody.put("name", name);
                        jsonBody.put("description", description);
                        jsonBody.put("barcode", barcode);
                        jsonBody.put("test", true);
                        jsonBody.put("img", ((SelectionActivity) getActivity()).mProductsViewModel.getImage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("POST PRODUCT", "Prodotto creato con successo");

                                    Product product = new Product();
                                    try {
                                        product.productId = (String) response.get("id");
                                        product.name = (String) response.get("name");
                                        product.description = (String) response.get("description");
                                        product.barcode = (String) response.get("barcode");
                                        product.quantity = 1;
                                        product.image = (String) response.get("img");
                                        product.userId = ((SelectionActivity) getActivity()).mProductsViewModel.getUserId();
                                        ((SelectionActivity) getActivity()).productRepository.insert(product);
                                        ((SelectionActivity) getActivity()).mProductsViewModel.setImage(null);
                                        ((SelectionActivity) getActivity()).mProductsViewModel.setNameDescriptionNewProduct(null, null);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    FragmentTransaction transaction;
                                    transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    Bundle data = new Bundle();
                                    SpesaFragment spesaFragment = new SpesaFragment();
                                    spesaFragment.setArguments(data);
                                    transaction.replace(R.id.fragmentSelection, spesaFragment);
                                    transaction.addToBackStack("Transaction");
                                    transaction.commit();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("POST PRODUCT", "Prodotto non creato: error code " + error.networkResponse.statusCode);
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("Authorization", "Bearer " + accessToken);
                            return params;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);
                }
                nameTextInputLayout.setError(null);
                descriptionTextInputLayout.setError(null);
                if (name.matches("")){
                    Log.e("CREA", "Nuovo prodotto non creato: errore campo nome non compilato");
                    nameTextInputLayout.setError("Campo obbligatorio");
                }
                if (description.matches("")){
                    Log.e("CREA", "Nuovo prodotto non creato: errore campo descrizione non compilato");
                    descriptionTextInputLayout.setError("Campo obbligatorio");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        String name = nameTextInputLayout.getEditText().getText().toString();
        String description = descriptionTextInputLayout.getEditText().getText().toString();
        ((SelectionActivity) getActivity()).mProductsViewModel.setNameDescriptionNewProduct(name, description);
    }
}