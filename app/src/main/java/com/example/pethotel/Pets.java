package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class Pets extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private PetType petToAdd = PetType.Dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DisplayAllPets();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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

    public void DisplayAllPets(){
        mDatabase.child("pets").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
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

    private void UpdateUI(HashMap<String, Object> pets){
        LinearLayout layout = (LinearLayout) findViewById(R.id.petsViewLL);
        layout.removeAllViews();

        for(Map.Entry<String, Object> entry : pets.entrySet()) {
            String key = entry.getKey();
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

            // TODO: add delete button

            card.addView(textView);

            layout.addView(card);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.dogRadio:
                if (checked) {
                    petToAdd = PetType.Dog;
                }
                    break;
            case R.id.catRadio:
                if (checked)
                    petToAdd = PetType.Cat;
                    break;
        }
    }

    public void CreateNewPet(View view){

        EditText petName = (EditText)findViewById(R.id.petNameTxt);

        mDatabase.child("pets").child(mAuth.getUid()).push().setValue(new Animal(petName.getText().toString(), petToAdd));

        petName.setText(null);

        Toast.makeText(this, "Pet Added Successfully", Toast.LENGTH_SHORT).show();

        // TODO: Add animation
        findViewById(R.id.addNewCard).setVisibility(View.GONE);
    }

    public void showAddNewPet(View view){
        // TODO: Add Animation

        if(findViewById(R.id.addNewCard).getVisibility() == View.GONE){
            findViewById(R.id.addNewCard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.addNewCard).setVisibility(View.GONE);
        }

    }

    // TODO: Add remove
}
