package com.madness.deliveryman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /* Views */
    private Toolbar toolbar;
    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private TextView vehicle;
    private ImageView img;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("deGustibus");
        toolbar.setSubtitle("Riders");
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
        vehicle = findViewById(R.id.tv_show_vehicle);
        img = findViewById(R.id.imageview);


        fullname.setText(pref.getString("name", getResources().getString(R.string.name)));
        email.setText(pref.getString("email", getResources().getString(R.string.email)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.desc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.phone)));

        String selector = pref.getString("vehicle","bike");

        switch (selector) {
            case "bike":{
                String v  = this.getString(R.string.bike);
                vehicle.setText(v);
            }
            break;
            case "car":{
                String v  = this.getString(R.string.car);
                vehicle.setText(v);
            }
            break;
            case "motorbike":{
                String v  = this.getString(R.string.motorbike);
                vehicle.setText(v);
            }
            break;
        }


        if(pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
        super.onResume();
    }
}
