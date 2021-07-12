package com.example.trackingmypantry;

import android.content.Intent;
import android.os.Bundle;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

//code from https://github.com/yuriy-budiyev/code-scanner
public class BarcodeActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private String accessToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        accessToken = getIntent().getExtras().getString("EXTRA_ACCESS_TOKEN");
        userId = getIntent().getExtras().getString("EXTRA_USERID");

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                        intent.putExtra("EXTRA_BARCODE_STRING", result.getText());
                        intent.putExtra("EXTRA_ACCESS_TOKEN", accessToken);
                        intent.putExtra("EXTRA_USERID", userId);
                        startActivity(intent);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}