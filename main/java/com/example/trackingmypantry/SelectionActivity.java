package com.example.trackingmypantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelectionActivity extends AppCompatActivity {

    protected MaterialToolbar materialToolbar;
    protected BottomNavigationView bottomNavigationView;
    private static boolean mBool = true;
    protected ProductsViewModel mProductsViewModel; //utilizzato per salvare la lista di prodotti richiesta tramite barcode
    protected String accessToken;
    protected String userId;
    protected ProductRepository productRepository; //repository utilizzata per la gestione del database

    protected void openFragment(Fragment fragment){
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentSelection, fragment);
        transaction.addToBackStack("Transaction");
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //nasconde l'action bar dell'applicazione
        setContentView(R.layout.activity_selection);

        materialToolbar = findViewById(R.id.topAppBarLogin);
        materialToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectionActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        accessToken = getIntent().getExtras().getString("EXTRA_ACCESS_TOKEN");
        userId = getIntent().getExtras().getString("EXTRA_USERID");
        mProductsViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        productRepository = new ProductRepository(getApplication(), userId);

        mProductsViewModel.setUserId(userId);

        if (mBool) {//visualizza il layout di SpesaFragment solo la prima volta in cui viene creata l'activity
            openFragment(new SpesaFragment());
            mBool = false;
        } else if (getIntent().getExtras().getString("EXTRA_BARCODE_STRING") != null){
            String barcode = getIntent().getExtras().getString("EXTRA_BARCODE_STRING");
            FragmentTransaction transaction;
            transaction = getSupportFragmentManager().beginTransaction();
            Bundle data = new Bundle();
            data.putString("EXTRA_BARCODE_STRING", barcode);
            SpesaFragment spesaFragment = new SpesaFragment();
            spesaFragment.setArguments(data);
            transaction.replace(R.id.fragmentSelection, spesaFragment);
            transaction.addToBackStack("Transaction");
            transaction.commit();
            Log.i("BARCODE", "Barcode recognized");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                if (item.getItemId() == R.id.spesaButton) {
                    fragment = new SpesaFragment();
                } else {
                    fragment = new DispensaFragment();
                }
                openFragment(fragment);
                return true;
            }
        });

    }
}