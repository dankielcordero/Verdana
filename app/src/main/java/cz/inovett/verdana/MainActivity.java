package cz.inovett.verdana;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private RecyclerView blogList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLikes;
    private boolean mProcessLike = false;
    private int likes = 0;
    private int views = 0;
    private TextView tViews, tLikes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,InsertActivity.class);
                startActivity(i);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes.keepSynced(true);
        auth = FirebaseAuth.getInstance();


        blogList = (RecyclerView) findViewById(R.id.blog_list);
        blogList.setHasFixedSize(true);
        blogList.setLayoutManager( new LinearLayoutManager(this));

        tViews = (TextView) findViewById(R.id.textViewViews);
        tLikes = (TextView) findViewById(R.id.textViewLike);




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth == null){
                    Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*checkUserExist();*/
        auth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setFullText(model.getText());

                viewHolder.setLikeBtn(post_key);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*views = views+1;
                        tViews.setText(views + "views");*/
                        Intent singleBlogInetnt = new Intent(MainActivity.this, BlogSingleActivity.class);
                        singleBlogInetnt.putExtra("blog_id", post_key);
                        startActivity(singleBlogInetnt);
                    }
                });

                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;

                        mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        mDatabaseLikes.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                        Toast.makeText(MainActivity.this, "You like it!", Toast.LENGTH_SHORT).show();
                                        /*likes = likes + 1;
                                        tViews.setText(likes);*/

                                    } else {
                                        mDatabaseLikes.child(post_key).child(auth.getCurrentUser().getUid()).setValue("RamdomValue");
                                        mProcessLike = false;
                                        Toast.makeText(MainActivity.this, "You like it!", Toast.LENGTH_SHORT).show();
                                        /*likes = likes + 1;
                                        tViews.setText(likes);*/

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                });
            }
        };
        blogList.setAdapter(firebaseRecyclerAdapter);
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    private void checkUserExist() {

        final String name = auth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(name)){
                    Intent i = new Intent(MainActivity.this, RegisterActivity.class); /**vyřešit rpoblémy*/
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("MainMenu", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }


    public  static  class BlogViewHolder extends RecyclerView.ViewHolder{

        View view;
        DatabaseReference mDatabseLike;
        ImageButton mLikebtn;
        FirebaseAuth mAuth;
        public BlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            mLikebtn = (ImageButton) view.findViewById(R.id.btnLike);

            mDatabseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key) {
            mDatabseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        //zde změnit barvu ikonky
                    } else {
                        //zde změnit barvu ikonky na bílo
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) view.findViewById(R.id.post_Name);
            post_title.setText(title);
        }
        public void setFullText(String Text) {
            TextView full_text = (TextView) view.findViewById(R.id.post_full_text);
            full_text.setText(Text);
        }
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/


}

