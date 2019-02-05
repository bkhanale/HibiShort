package com.bhushan.hibishort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText student_id;
    EditText password;
    Button login_button;
    SharedPreferences shared_pref;
    Intent webscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        student_id = findViewById(R.id.input_studentid);
        password = findViewById(R.id.input_password);
        login_button = findViewById(R.id.input_button);
        webscreen = new Intent(this, HibiActivity.class);

        shared_pref = getApplicationContext().getSharedPreferences("user_info",
                                                                    Context.MODE_PRIVATE);

        if(!shared_pref.getAll().isEmpty()) getUserInfoFromSharedPref();


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sid = student_id.getText().toString();
                String pwd = password.getText().toString();
                SharedPreferences.Editor editor = shared_pref.edit();
                editor.putString("student_id", sid);
                editor.putString("password", pwd);
                editor.commit();
                startActivity(webscreen);
            }
        });
    }

    public void getUserInfoFromSharedPref() {

        String sid_shared, pwd_shared;
        sid_shared = shared_pref.getString("student_id", "student_id_not_found");
        pwd_shared = shared_pref.getString("password", "password_not_found");

        if(sid_shared != null && !sid_shared.isEmpty() && pwd_shared != null && !pwd_shared.isEmpty()) {
            webscreen = new Intent(this, HibiActivity.class);
            startActivity(webscreen);
        }
    }
}
