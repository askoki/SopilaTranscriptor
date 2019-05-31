package com.example.arcibald160.sopilatranscriptor;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.example.arcibald160.sopilatranscriptor.helpers.Utils;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Intent intent = getIntent();

        PDFView myPdfView = findViewById(R.id.pdfView);
        TextView pdfName = findViewById(R.id.pdf_name_id);
        TextView pdfSize = findViewById(R.id.free_space);
        TextView pdfDate = findViewById(R.id.date_view);


        File pdfFile = (File) intent.getExtras().get(getApplicationContext().getString(R.string.pdf_extra_key));

        pdfName.setText(pdfFile.getName());

//        Not working cause it is a pdf file not wav
        String size = Utils.formatFileSize(pdfFile.length());
        pdfSize.setText(size);
//
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(new Date(pdfFile.lastModified()));
        pdfDate.setText(date);

        myPdfView.fromFile(pdfFile).load();
    }
}
