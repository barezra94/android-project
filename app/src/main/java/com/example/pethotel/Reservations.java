package com.example.pethotel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reservations extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean isManager = false;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getIntent().getSerializableExtra("userType") == UserType.Manager) {
            isManager = true;
            findViewById(R.id.addReservation).setVisibility(View.GONE);
        }

        DisplayAllReservations();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TextView helloTxt = (TextView) findViewById(R.id.helloTxt);
        String hello = "Hello " + getIntent().getStringExtra("username");
        helloTxt.setText(hello);

        EditText fromDate = (EditText) findViewById(R.id.fromDateTxt);
        EditText toDate = (EditText) findViewById(R.id.toDateTxt);

        setDateTimeField();

        fromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fromDatePickerDialog.show();
                return false;
            }
        });

        toDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toDatePickerDialog.show();
                return false;
            }
        });

    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        final EditText fromDate = (EditText) findViewById(R.id.fromDateTxt);
        final EditText toDate = (EditText) findViewById(R.id.toDateTxt);

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                fromDate.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                toDate.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        toDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

    }

    @Override
    public void onStart(){
        super.onStart();

        if (!isManager) {
            mDatabase.child("pets").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final List<String> pets = new ArrayList<String>();

                    for (DataSnapshot areSnapshot : dataSnapshot.getChildren()) {
                        Animal pet = areSnapshot.getValue(Animal.class);
                        String petToAdd = pet.animalName + " the ";
                        petToAdd = pet.type == PetType.Dog ? petToAdd.concat("Dog") : petToAdd.concat("Cat");

                        pets.add(petToAdd);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Reservations.this, android.R.layout.simple_spinner_item, pets);
                    ((Spinner) findViewById(R.id.petSpinner)).setAdapter(arrayAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    public void DisplayAllReservations(){

        if (isManager){
            mDatabase.child("reservation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> reservations = (HashMap<String, Object>)dataSnapshot.getValue();
                    UpdateUI(reservations);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });
        } else{
            mDatabase.child("reservation").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> reservations = (HashMap<String, Object>)dataSnapshot.getValue();
                    if (reservations != null) {
                        UpdateUI(reservations);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Tag","Failed to read value.", error.toException());
                }
            });
        }
    }

    private void UpdateUI(HashMap<String, Object> reservations){
        LinearLayout layout = (LinearLayout) findViewById(R.id.reservationsViewLL);
        layout.removeAllViews();

        // The hashmap is not like in pets - convert to pets
        for(Map.Entry<String, Object> entry : reservations.entrySet()) {
            final String key = entry.getKey();
            Object value;
            if (isManager) {
                Object keyValue = entry.getValue();
                value = ((HashMap) keyValue).values().toArray()[0];
            } else {
                value = entry.getValue();
            }
            CardView card = new CardView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
            params.setMargins(5,5,5,5);
            card.setLayoutParams(params);

            TextView textView = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER);

            String text = ((HashMap) value).values().toArray()[0].toString()
                    + " - " + ((HashMap) value).values().toArray()[1].toString()
                    + " for " + ((HashMap) value).values().toArray()[2].toString();

            textView.setText(text);
            card.addView(textView);

            // Manager can only see reservations and can't delete them
            if(!isManager) {
                Button deleteBtn = new Button(getApplicationContext());
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                deleteBtn.setLayoutParams(btnParams);
                deleteBtn.setText("Delete"); //TODO: Change to icon button
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase.child("reservation").child(mAuth.getUid()).child(key).removeValue();
                    }
                });
                card.addView(deleteBtn);
            }
            layout.addView(card);
        }
    }

    public void showAddNewReservation(View view){

        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.addReservation);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);

        if(findViewById(R.id.addNewReservationCard).getVisibility() == View.GONE){
            // get the center for the clipping circle
            int cx = findViewById(R.id.addNewReservationCard).getWidth() / 2;
            int cy = findViewById(R.id.addNewReservationCard).getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewReservationCard), cx, cy, 0f, finalRadius);

            // make the view visible and start the animation
            findViewById(R.id.addNewReservationCard).setVisibility(View.VISIBLE);
            anim.start();
        } else {
            int cx = findViewById(R.id.addNewReservationCard).getWidth() / 2;
            int cy = findViewById(R.id.addNewReservationCard).getHeight() / 2;

            // get the initial radius for the clipping circle
            float initialRadius = (float) Math.hypot(cx, cy);

            // create the animation (the final radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewReservationCard), cx, cy, initialRadius, 0f);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    findViewById(R.id.addNewReservationCard).setVisibility(View.GONE);
                }
            });

            // start the animation
            anim.start();
        }

    }

    public void CreateNewReservation(View view){

        Spinner petName = (Spinner)findViewById(R.id.petSpinner);
        EditText fromDate = (EditText) findViewById(R.id.fromDateTxt);
        EditText toDate = (EditText) findViewById(R.id.toDateTxt);

        Reservation reservation = new Reservation(fromDate.getText().toString(), toDate.getText().toString(), petName.getSelectedItem().toString());
        
        mDatabase.child("reservation").child(mAuth.getUid()).push().setValue(reservation);

        Toast.makeText(this, "Reservation Added Successfully", Toast.LENGTH_SHORT).show();

        fromDate.setText(null);
        toDate.setText(null);

        int cx = findViewById(R.id.addNewReservationCard).getWidth() / 2;
        int cy = findViewById(R.id.addNewReservationCard).getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewReservationCard), cx, cy, initialRadius, 0f);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                findViewById(R.id.addNewReservationCard).setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();
    }
}
