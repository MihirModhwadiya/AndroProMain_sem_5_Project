package com.example.androidwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText Input0, Input1, Input2, Input3;
    Button Submit;
    FirebaseAuth mAuth;

    FirebaseUser userr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        Input0 = findViewById(R.id.Input_email);
        Input1 = findViewById(R.id.Input_username);
        Input2 = findViewById(R.id.Input_password);
        Input3 = findViewById(R.id.Input_C_password);
        Submit = findViewById(R.id.Btn_submit);
    }

    public void redirect_sign_up(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finish();
    }

    public void check(View view) {
        String email = Input0.getText().toString();
        String username = Input1.getText().toString();
        String password = Input2.getText().toString();
        String C_password = Input3.getText().toString();

        if (password.equals(C_password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(SignUp.this, "createUserWithEmail:successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                setAuth(user);
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUp.this, "createUserWithEmail:failure", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }

                        public void setAuth(FirebaseUser user) {
                            MainActivity main = new MainActivity();
                            FirebaseFirestore db = main.db;
                            mAuth = FirebaseAuth.getInstance();

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("username", username);

                            db.collection("users")
                                    .document(user.getUid())
                                    .set(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void avoid) {
                                            Toast.makeText(SignUp.this, "Successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUp.this, "Error while adding document", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        } else {
            Toast.makeText(SignUp.this, "Please enter same password", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI(FirebaseUser user) {
        MainActivity mn = new MainActivity();
        mn.user = user;
    }
}