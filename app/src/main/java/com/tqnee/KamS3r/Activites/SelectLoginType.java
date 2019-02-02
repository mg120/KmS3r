package com.tqnee.KamS3r.Activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tqnee.KamS3r.R;

public class SelectLoginType extends AppCompatActivity implements View.OnClickListener {

    Button ads_btn, asks_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login_type);


        ads_btn = (Button) findViewById(R.id.ads_btn_id);
        asks_btn = (Button) findViewById(R.id.asks_btn_id);

        ads_btn.setOnClickListener(this);
        asks_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ads_btn_id:
                Intent intent = new Intent(SelectLoginType.this, HomeActivity.class);
                intent.putExtra("selected", 2);
                startActivity(intent);
                finish();
                break;
            case R.id.asks_btn_id:
                Intent intent2 = new Intent(SelectLoginType.this, HomeActivity.class);
                intent2.putExtra("selected", 1);
                startActivity(intent2);
                finish();
                break;
        }
    }
}
