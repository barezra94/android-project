package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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

    private void displayAllReservations(){

    }

    public void CreateNewReservation(View view){

        HashMap<String, Object> reservation = new HashMap<>();
        reservation.put("fromDate", );
        reservation.put("toDate", );
        reservation.put("petStaying", )
        
        mDatabase.child("reservation").child(mAuth.getUid()).setValue(reservation);

        Toast.makeText(this, "Reservation Added Successfully", Toast.LENGTH_SHORT).show();
    }
}
