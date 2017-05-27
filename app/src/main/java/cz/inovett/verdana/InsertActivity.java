package cz.inovett.verdana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {
    private EditText name, fullText;
    private Button mSubmitBtn;
    private DatabaseReference databaseReference;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        getSupportActionBar().hide();


        name = (EditText) findViewById(R.id.nameField);
        fullText = (EditText) findViewById(R.id.editTextFullText);
        mSubmitBtn = (Button) findViewById(R.id.buttonSubmit);
        mProgress = new ProgressDialog(this);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");


        mSubmitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                StartPosting();
            }
        });
    }
    private void StartPosting() {
        String strName = name.getText().toString().trim();
        String strFullText = fullText.getText().toString().trim();

        if (!TextUtils.isEmpty(strName)
                &&!TextUtils.isEmpty(strFullText)){
            mProgress.setMessage("Posting ....");
            mProgress.show();
            DatabaseReference newPost = databaseReference.push();
            newPost.child("title").setValue(strName);
            newPost.child("Text").setValue(strFullText);
            mProgress.dismiss();
            Intent i = new Intent(this,MainActivity.class);
            Toast.makeText(this,"Successfully added", Toast.LENGTH_SHORT).show();
            startActivity(i);
        }

    }
}
