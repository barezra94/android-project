package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Homepage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User currUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                currUser = user;
                EditHomepageView(user);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag","Failed to read value.", error.toException());
            }
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void EditHomepageView(User user){
        TextView helloTxt = (TextView) findViewById(R.id.helloTxt);
        String hello = "Hello " + user.username;
        helloTxt.setText(hello);

        if(user.type == UserType.Manager){
            // Read from the database
            // TODO: Change this part to get all the data from the database (Needs also the reservations and pets)
            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> users = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdateManagerUI(users);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });
        } else {
            findViewById(R.id.numOfUsersCard).setVisibility(View.INVISIBLE);
            findViewById(R.id.numOfPetsCard).setVisibility(View.INVISIBLE);
            findViewById(R.id.showMyPets).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.toolbar_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutAction: {
                mAuth.signOut();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onReservationsClicked(View view){
        Intent intent = new Intent(this, Reservations.class);
        intent.putExtra("username", currUser.username);
        intent.putExtra("userType", currUser.type);
        startActivity(intent);
    }

    public void onPetsClicked(View view){
        Intent intent = new Intent(this, Pets.class);
        intent.putExtra("username", currUser.username);
        startActivity(intent);
    }

    private void UpdateManagerUI(HashMap<String, Object> users){
        TextView numOfUsersTxt = (TextView)findViewById(R.id.numUsersTxt);
        numOfUsersTxt.setText(String.valueOf(users.size()));
    }
}
