package com.example.pethotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }



    public void onRegisterClicked(View view){

        final EditText userName = (EditText)findViewById(R.id.name);
        final EditText emailTxt = (EditText)findViewById(R.id.email);
        EditText passwordTxt = (EditText)findViewById(R.id.password);
        EditText passwordConTxt = (EditText) findViewById(R.id.confirmationPassword);

        if (!passwordConTxt.getText().toString().equals(passwordTxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(emailTxt.getText().toString(), passwordTxt.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // TODO: Separate user insert to a different method with try catch
                                User user = new User(userName.getText().toString(), emailTxt.getText().toString(), UserType.PetOwner);
                                FirebaseUser u1 = mAuth.getCurrentUser();
                                database.child("users").child(u1.getUid()).setValue(user);
                                updateUI();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
    }

    private void updateUI(){
        Intent intent = new Intent(this, Homepage.class);
        startActivity(intent);
    }
}
