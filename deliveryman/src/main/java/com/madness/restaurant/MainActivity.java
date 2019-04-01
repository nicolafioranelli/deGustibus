package com.madness.restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /* Views */
    private Toolbar toolbar;
    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private ImageView img;
    private TextView loc;
    private TextView avail;
    private TextView car;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("deGustibus");
        toolbar.setSubtitle("for Riders");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        if (id == R.id.action_edit) {
            //Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        fullname = findViewById(R.id.tv_show_fullName);
        email = findViewById(R.id.tv_show_email);
        desc = findViewById(R.id.tv_show_desc);
        phone = findViewById(R.id.tv_show_phone);
        img = findViewById(R.id.imageview);
        loc = findViewById(R.id.tv_show_location);
        avail = findViewById(R.id.tv_show_availab);
        car = findViewById(R.id.tv_show_car);

        fullname.setText(pref.getString("name", null));
        email.setText(pref.getString("email", null));
        desc.setText(pref.getString("desc", null));
        phone.setText(pref.getString("phone", null));
        loc.setText(pref.getString("loc", null));
        avail.setText(pref.getString("avail", null));
        car.setText(pref.getString("car", null));
        if(pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
        super.onResume();
    }
}
