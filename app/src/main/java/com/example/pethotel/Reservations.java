package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Reservations extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean isManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(getIntent().getSerializableExtra("userType") == UserType.Manager){
            isManager = true;

            findViewById(R.id.addPet).setVisibility(View.GONE);
        }

        DisplayAllReservations();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TextView helloTxt = (TextView) findViewById(R.id.helloTxt);
        String hello = "Hello " + getIntent().getStringExtra("username");
        helloTxt.setText(hello);
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

    public void DisplayAllReservations(){

        if (isManager){
            mDatabase.child("reservations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> reservations = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdateUIManager(reservations);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });
        } else{
            mDatabase.child("reservations").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> pets = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdateUI(pets);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });
        }
    }

    // TODO: Change UI so that it shows the reservations
    private void UpdateUI(HashMap<String, Object> reservations){
        LinearLayout layout = (LinearLayout) findViewById(R.id.petsViewLL);
        layout.removeAllViews();

        for(Map.Entry<String, Object> entry : reservations.entrySet()) {
            final String key = entry.getKey();
            Object value = entry.getValue();

            CardView card = new CardView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
            params.setMargins(5,5,5,5);
            card.setLayoutParams(params);

            TextView textView = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER);

            String text = ((HashMap) value).values().toArray()[0].toString() + " the ";
            if (((HashMap) value).values().toArray()[1].toString().equalsIgnoreCase("Dog")) {
                text = text.concat("Dog");
            } else {
                text = text.concat("Cat");
            }

            textView.setText(text);

            Button deleteBtn = new Button(getApplicationContext());
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            deleteBtn.setLayoutParams(btnParams);
            deleteBtn.setText("Delete"); //TODO: Change to icon button
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabase.child("reservations").child(mAuth.getUid()).child(key).removeValue();
                }
            });

            card.addView(textView);
            card.addView(deleteBtn);

            layout.addView(card);
        }
    }

    public void showAddNewReservation(View view){
        // TODO: Add Animation

        if(findViewById(R.id.addNewCard).getVisibility() == View.GONE){
            findViewById(R.id.addNewCard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.addNewCard).setVisibility(View.GONE);
        }

    }

    /*public void CreateNewReservation(View view){

        HashMap<String, Object> reservation = new HashMap<>();
        reservation.put("fromDate", );
        reservation.put("toDate", );
        reservation.put("petStaying", )
        
        mDatabase.child("reservation").child(mAuth.getUid()).setValue(reservation);

        Toast.makeText(this, "Reservation Added Successfully", Toast.LENGTH_SHORT).show();
    }*/
}
