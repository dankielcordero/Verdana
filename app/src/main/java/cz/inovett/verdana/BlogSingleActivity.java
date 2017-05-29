package cz.inovett.verdana;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private TextView mBlogSingleTitle, mBlogSingleDesc;
    private Button mSingleRomeBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        getSupportActionBar().hide();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mAuth = FirebaseAuth.getInstance();

        mPost_key = getIntent().getExtras().getString("blog_id");

        mBlogSingleDesc = (TextView) findViewById(R.id.textViewTextBlogSimple);
        mBlogSingleTitle = (TextView) findViewById(R.id.textViewTitleBlogSimple);
        mSingleRomeBtn = (Button) findViewById(R.id.buttonRemove);

        //Toast.makeText(BlogSingleActivity.this, post_key, Toast.LENGTH_SHORT).show();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("Text").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                    mSingleRomeBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSingleRomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).removeValue();

                Intent mainIntent = new Intent(BlogSingleActivity.this, MainActivity.class);
                startActivity(mainIntent);

            }
        });

    }
}
