package com.example.pethotel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

    public void DisplayAllPets(){
        mDatabase.child("pets").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> pets = (HashMap<String, Object>)dataSnapshot.getValue();
                if (pets != null){
                    UpdateUI(pets);
                }
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
                    mDatabase.child("pets").child(mAuth.getUid()).child(key).removeValue();
                }
            });
            // Add picture of cat or dog
            card.addView(textView);
            card.addView(deleteBtn);

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
                    ((RadioButton)findViewById(R.id.catRadio)).setChecked(false);
                }
                    break;
            case R.id.catRadio:
                if (checked) {
                    petToAdd = PetType.Cat;
                    ((RadioButton)findViewById(R.id.dogRadio)).setChecked(false);
                }
                    break;
        }
    }

    public void CreateNewPet(View view){

        EditText petName = (EditText)findViewById(R.id.petNameTxt);
        
        if(!petName.getText().toString().isEmpty()) {
            mDatabase.child("pets").child(mAuth.getUid()).push().setValue(new Animal(petName.getText().toString(), petToAdd));

            petName.setText(null);

            Toast.makeText(this, "Pet Added Successfully", Toast.LENGTH_SHORT).show();

            int cx = findViewById(R.id.addNewCard).getWidth() / 2;
            int cy = findViewById(R.id.addNewCard).getHeight() / 2;

            // get the initial radius for the clipping circle
            float initialRadius = (float) Math.hypot(cx, cy);

            // create the animation (the final radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewCard), cx, cy, initialRadius, 0f);

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
        } else {
            Toast.makeText(this, "Pet must have a name", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAddNewPet(View view){

        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.addPet);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);

        if(findViewById(R.id.addNewCard).getVisibility() == View.GONE){
            // get the center for the clipping circle
            int cx = findViewById(R.id.addNewCard).getWidth() / 2;
            int cy = findViewById(R.id.addNewCard).getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewCard), cx, cy, 0f, finalRadius);

            // make the view visible and start the animation
            findViewById(R.id.addNewCard).setVisibility(View.VISIBLE);
            anim.start();
        } else {
            int cx = findViewById(R.id.addNewCard).getWidth() / 2;
            int cy = findViewById(R.id.addNewCard).getHeight() / 2;

            // get the initial radius for the clipping circle
            float initialRadius = (float) Math.hypot(cx, cy);

            // create the animation (the final radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(findViewById(R.id.addNewCard), cx, cy, initialRadius, 0f);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    findViewById(R.id.addNewCard).setVisibility(View.GONE);
                }
            });

            // start the animation
            anim.start();
        }

    }
}
