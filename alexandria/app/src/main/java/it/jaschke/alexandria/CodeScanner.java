package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeScanner extends ActionBarActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    final static String LOG_TAG = "CodeScanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        if (rawResult != null) {
            Log.v("Scanner result: ", rawResult.getText()); // Prints scan results
            Log.v("Scanner format: ", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
            Intent intent = new Intent();
            intent.putExtra("barCode", rawResult.getText());
            intent.putExtra("barCodeFormat", rawResult.getBarcodeFormat().toString());
            setResult(9001, intent);
            finish();
        } else {
            Log.v(LOG_TAG, "There is no barcode scan result");
        }
    }
}
