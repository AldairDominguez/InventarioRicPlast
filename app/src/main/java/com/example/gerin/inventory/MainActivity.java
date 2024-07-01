package com.example.gerin.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = (ImageView) findViewById(R.id.splash_screen_logo);
        TextView name = (TextView) findViewById(R.id.splash_screen_name);

        Animation fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);

        logo.setAnimation(fromBottom);
        name.setAnimation(fadeIn);

        fadeIn.setStartOffset(2000);
        // Aplicar diseño de texto a TextView
        String text = getString(R.string.app_name);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int start = text.indexOf("RIC");
        int end = start + "RIC".length();
        builder.append(text);
        builder.setSpan(new ForegroundColorSpan(0xFFFF9900), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Naranja

        start = text.indexOf("PLAST");
        end = start + "PLAST".length();
        builder.setSpan(new ForegroundColorSpan(0xFF003366), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Azul

        name.setText(builder);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent catalogIntent = new Intent(MainActivity.this, CatalogActivity.class);
                startActivity(catalogIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
