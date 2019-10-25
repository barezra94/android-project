package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

        CalendarView cv = (CalendarView)findViewById(R.id.calendarView);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, final int i, final int i1, final int i2) {
                final TextView calenderText = findViewById(R.id.calendarText);

                mDatabase.child("reservation").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        HashMap<String, Object> reservations = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> entry : reservations.entrySet()) {
                            final String key = entry.getKey();
                            HashMap<String, Object> reservation = (HashMap)((HashMap) entry.getValue()).values().toArray()[0];

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            LocalDateTime now = LocalDateTime.of(i, i1+1, i2,0,0);
                            if (dtf.format(now).toString().equalsIgnoreCase(reservation.values().toArray()[0].toString())) {
                                String text = reservation.values().toArray()[0].toString() + " - " + reservation.values().toArray()[1].toString() + " for " + reservation.values().toArray()[2].toString();
                                calenderText.append(text);
                                calenderText.append("\n");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Tag","Failed to read value.", error.toException());
                    }
                });
            }
        });
    }

    private void EditHomepageView(User user){
        TextView helloTxt = (TextView) findViewById(R.id.helloTxt);
        String hello = "Hello " + user.username;
        helloTxt.setText(hello);

        if(user.type == UserType.Manager){
            // Read from the database
            findViewById(R.id.managementLL).setVisibility(View.VISIBLE);
            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> users = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdateUsersCardUI(users);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });

            // TODO: Get only the reservations that the from date and to date today date is in range
            mDatabase.child("pets").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String, Object> pets = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdatePetsCardUI(pets);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
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

    private void UpdateUsersCardUI(HashMap<String, Object> users){
        TextView numOfUsersTxt = (TextView)findViewById(R.id.numUsersTxt);
        numOfUsersTxt.setText(String.valueOf(users.size()));
    }

    private void UpdatePetsCardUI(HashMap<String, Object> pets){
        TextView numOfUsersTxt = (TextView)findViewById(R.id.numPetsTxt);
        int size = 0;
        for(Map.Entry<String, Object> entry : pets.entrySet()) {
            size += ((HashMap) entry.getValue()).values().toArray().length;
        }

        numOfUsersTxt.setText(String.valueOf(size));
    }
}
