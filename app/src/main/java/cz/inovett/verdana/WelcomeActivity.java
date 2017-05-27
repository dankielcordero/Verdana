package cz.inovett.verdana;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);


    }

    public void startRegister(View view) {
        Intent i = new Intent(WelcomeActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    public void startLogIn(View view) {
        Intent i = new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(i);
    }
}
