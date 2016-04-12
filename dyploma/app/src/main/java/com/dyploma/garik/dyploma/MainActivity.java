package com.dyploma.garik.dyploma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            LoadTask myTask = new LoadTask();
            myTask.execute(getResources().getString(R.string.load_query));
            Toast.makeText(this,  "load_query = " + myTask.get(),
                    Toast.LENGTH_LONG).show();

            LoadTask myTask2 = new LoadTask();

            myTask2.execute(getResources().getString(R.string.number_query));
            Toast.makeText(this,  "number_query = " + myTask2.get(),
                    Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ;
    }
}
