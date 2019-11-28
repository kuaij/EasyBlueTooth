package com.xiaok.superbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SuperBluetooth mySB = new SuperBluetooth();
        mySB.setMacAddress("00:0C:BF:10:6A:51");
        boolean isSuccessful = mySB.connectWithMAC();
        Log.e("MainActivity",isSuccessful+"");

        Button btn_write_test = findViewById(R.id.btn_write_test);
        btn_write_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySB.writeDate("open");
            }
        });
    }
}
