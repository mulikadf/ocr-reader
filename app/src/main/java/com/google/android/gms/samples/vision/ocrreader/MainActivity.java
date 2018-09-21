package com.google.android.gms.samples.vision.ocrreader;

import android.os.Bundle;
import android.widget.Toast;

import com.star.ocr_reader.OcrActivity;
import com.star.ocr_reader.OcrView;

public class MainActivity extends OcrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDetected(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected OcrView getOcrView() {
        return findViewById(R.id.myView);
    }

}
