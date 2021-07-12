package com.example.trackingmypantry;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpesaFragment extends Fragment {

    private Button btnCerca;
    private Button btnAddProduct;
    private Button btnBarcode;
    private ListView mylist;
    private ArrayAdapter<Product> adapter;
    private TextInputLayout barcodeTextInputLayout;
    private String token;
    private String accessToken;
    private String userId;

    private void setmAdapter(Context context, List<Product> productsList){
        adapter = new ArrayAdapter<Product>(context, R.layout.card_relative_layout, productsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View itemView = inflater.inflate(R.layout.card_relative_layout, null, true);

                TextView title = (TextView) itemView.findViewById(R.id.card_title);
                title.setText(productsList.get(position).name);

                TextView secondaryText = (TextView) itemView.findViewById(R.id.card_secondary_text);
                secondaryText.setText(productsList.get(position).description);

                TextView quantityText = (TextView) itemView.findViewById(R.id.card_quantity);
                quantityText.setText("");

                if (productsList.get(position).image != null) {
                    byte[] decodedString = Base64.decode(productsList.get(position).image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView iv_icon = (ImageView) itemView.findViewById(R.id.card_image);
                    iv_icon.setImageBitmap(decodedByte);
                }

                return itemView;
            }
        };

        mylist.setAdapter(adapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                materialAlertDialogBuilder.setMessage("Vuoi aggiungere " + adapter.getItem(i).name + " alla tua dispensa?");
                materialAlertDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });
                materialAlertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("INSERT PRODUCT", adapter.getItem(i).productId);
                        ((SelectionActivity) getActivity()).productRepository.insert(adapter.getItem(i));

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                        String url = "https://lam21.modron.network/votes";
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("token", token);
                            jsonBody.put("rating", 1);
                            jsonBody.put("productId", adapter.getItem(i).productId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("RATING PRODUCT", "Modifica rating avvenuta con successo");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("RATING PRODUCT", "Modifica rating non avvenuta: error code " + error.networkResponse.statusCode);
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
                });
                materialAlertDialogBuilder.show();
            }
        });
    }


    public SpesaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spesa, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accessToken = getActivity().getIntent().getExtras().getString("EXTRA_ACCESS_TOKEN");
        userId = ((SelectionActivity) getActivity()).mProductsViewModel.getUserId();
        mylist = getView().findViewById(R.id.list_product);

        barcodeTextInputLayout = getView().findViewById(R.id.textBarcode);
        if (getActivity().getIntent().getExtras().getString("EXTRA_BARCODE_STRING") != null){
            ((SelectionActivity) getActivity()).mProductsViewModel.setBarcodeSpesa(
                    getActivity().getIntent().getExtras().getString("EXTRA_BARCODE_STRING"));
        }
        if (((SelectionActivity) getActivity()).mProductsViewModel.getBarcodeSpesa() != null){
            barcodeTextInputLayout.getEditText().setText(((SelectionActivity) getActivity()).mProductsViewModel.getBarcodeSpesa());
        }

        if (((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsSpesa().isEmpty() == false) {
            setmAdapter(getContext(), ((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsSpesa());
        }

        btnBarcode = getView().findViewById(R.id.photoButton);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intentToBarcodeActivity = new Intent(getContext(), BarcodeActivity.class);
                    intentToBarcodeActivity.putExtra("EXTRA_ACCESS_TOKEN", accessToken);
                    intentToBarcodeActivity.putExtra("EXTRA_USERID", userId);
                    startActivity(intentToBarcodeActivity);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                }
            }
        });

        btnAddProduct = getView().findViewById(R.id.addProductButton);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (token != null) {
                    String barcodeString = barcodeTextInputLayout.getEditText().getText().toString();
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                    materialAlertDialogBuilder.setMessage("Vuoi aggiungere un prodotto con barcode " + barcodeString + " al database globale?");
                    materialAlertDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the dialog
                        }
                    });
                    materialAlertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction transaction;
                            transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle data = new Bundle();
                            data.putString("EXTRA_TOKEN", token);
                            data.putString("EXTRA_BARCODE", barcodeString);
                            NewProductFragment newProductFragment = new NewProductFragment();
                            newProductFragment.setArguments(data);
                            transaction.replace(R.id.fragmentSelection, newProductFragment);
                            transaction.addToBackStack("Transaction");
                            transaction.commit();
                        }
                    });
                    materialAlertDialogBuilder.show();
                } else {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                    materialAlertDialogBuilder.setMessage("Prima di inserire un nuovo prodotto prova ad effettuare una ricerca, il prodotto che stai cercando potrebbe essere gia' stato inserito nel database globale!");
                    materialAlertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the dialog
                        }
                    });
                    materialAlertDialogBuilder.show();
                }
            }
        });

        btnCerca = getView().findViewById(R.id.buttonBarcode);
        btnCerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barcodeTextInputLayout = getView().findViewById(R.id.textBarcode);
                String barcodeString = barcodeTextInputLayout.getEditText().getText().toString();
                if(barcodeString.matches("") == false) { //se l'utente ha compilato il campo

                    barcodeTextInputLayout.setError(null);

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    String url = "https://lam21.modron.network/products?barcode=" + barcodeString;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("GET_PRODUCT", "Prodotti richiesti con successo");
                                    Log.i("GET_PRODUCT", response.toString());
                                    try {
                                        token = (String) response.get("token");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        JSONArray products = response.getJSONArray("products");
                                        ArrayList<Product> listp = new ArrayList<Product>();
                                        for (int i = 0; i < products.length(); i++) {
                                            JSONObject jsonobject = products.getJSONObject(i);
                                            Product product = new Product();
                                            product.productId = jsonobject.getString("id");
                                            product.name = jsonobject.getString("name");
                                            product.barcode = jsonobject.getString("barcode");
                                            product.description = jsonobject.getString("description");
                                            product.quantity = 1;
                                            product.image = jsonobject.getString("img");
                                            Log.i("USERID", userId);
                                            product.userId = userId;
                                            listp.add(product);
                                        }
                                        if (listp.isEmpty()){
                                            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                                            materialAlertDialogBuilder.setMessage("Non esistono prodotti con barcode " + barcodeString + " nel database globale, vuoi crearne uno nuovo?");
                                            materialAlertDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //close the dialog
                                                }
                                            });
                                            materialAlertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FragmentTransaction transaction;
                                                    transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                    Bundle data = new Bundle();
                                                    data.putString("EXTRA_TOKEN", token);
                                                    data.putString("EXTRA_BARCODE", barcodeString);
                                                    NewProductFragment newProductFragment = new NewProductFragment();
                                                    newProductFragment.setArguments(data);
                                                    transaction.replace(R.id.fragmentSelection, newProductFragment);
                                                    transaction.addToBackStack("Transaction");
                                                    transaction.commit();
                                                }
                                            });
                                            materialAlertDialogBuilder.show();
                                        } else {
                                            ((SelectionActivity) getActivity()).mProductsViewModel.setListOfProductsSpesa(listp);
                                            setmAdapter(getContext(), listp);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("GET_PRODUCT", "Prodotti non richiesti: error code " + error.networkResponse.statusCode);
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
                } else {
                    barcodeTextInputLayout.setError(null);
                    Log.e("CERCA", "Ricerca non effettuata: errore campo barcode non compilato");
                    barcodeTextInputLayout.setError("Campo obbligatorio");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        String barcode = barcodeTextInputLayout.getEditText().getText().toString();
        ((SelectionActivity) getActivity()).mProductsViewModel.setBarcodeSpesa(barcode);
    }

}