package com.example.pethotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onRegisterClicked(View view){

        EditText emailTxt = (EditText)findViewById(R.id.email);
        EditText passwordTxt = (EditText)findViewById(R.id.password);
        EditText passwordConTxt = (EditText) findViewById(R.id.confirmationPassword);

        if (!passwordConTxt.getText().toString().equals(passwordTxt.getText().toString())){
            // TODO: Show error message that passwords don't match
        }
        else{
            mAuth.createUserWithEmailAndPassword(emailTxt.getText().toString(), passwordTxt.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI();
                            } else {
                                // TODO: If sign in fails, display a message to the user.
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
