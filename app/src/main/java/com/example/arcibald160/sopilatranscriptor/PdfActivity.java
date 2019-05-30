package com.example.arcibald160.sopilatranscriptor;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.github.barteksc.pdfviewer.PDFView;

import java.io.InputStream;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        PDFView myPdfView = findViewById(R.id.pdfView);

        InputStream myFile = getResources().openRawResource(R.raw.sadila_je_mare_rf);
        myPdfView.fromStream(myFile).load();

    }
}
