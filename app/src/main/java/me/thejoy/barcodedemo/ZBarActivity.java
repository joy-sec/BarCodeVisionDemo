package me.thejoy.barcodedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ZBarActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {


    private ZBarScannerView mScannerView;
    private TextView txtBarcodeValue,txtCount;
    private int countNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbar);

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        txtCount=findViewById(R.id.txtCount);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        mScannerView.setAutoFocus(true);
        contentFrame.addView(mScannerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {


        if (rawResult.getContents()!=null){
            Toast.makeText(ZBarActivity.this,"Scan completed",Toast.LENGTH_SHORT).show();

            Log.e("ZBAR_DATA: ",rawResult.getContents());
            txtBarcodeValue.setText(rawResult.getContents());
            countNum+=1;
            txtCount.setText("Total Count: "+countNum);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.resumeCameraPreview(ZBarActivity.this);
                }
            }, 2000);
        }



    }

}
