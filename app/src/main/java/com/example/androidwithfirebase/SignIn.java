package com.example.androidwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText Input1, Input2;
    Button Submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        Input1 = findViewById(R.id.Input_email);
        Input2 = findViewById(R.id.Input_password);
        Submit = findViewById(R.id.Btn_submit_sign_in);

    }
    public void redirect_sign_in(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }
    public void check(View view){
        String email = Input1.getText().toString();
        String password = Input2.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(SignIn.this, "SignIn successfully completed", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, "SignIn failed", Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }
                    }
                    public void updateUI(FirebaseUser user) {
                        MainActivity mn = new MainActivity();
                        mn.user = user;
                    }

                });
    }

}