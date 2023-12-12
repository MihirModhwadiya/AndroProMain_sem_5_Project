package com.example.androidwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Save_Notes extends AppCompatActivity {

    EditText Title, Desc;
    FloatingActionButton Add_note;
    public FirebaseAuth mAuth;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_notes);

        MainActivity mn = new MainActivity();
        mAuth = FirebaseAuth.getInstance();

//        Toast.makeText(this, "" + mAuth.getUid(), Toast.LENGTH_SHORT).show();
//        if (mAuth.getUid() == null) {
//            Intent intent = new Intent(this, SignIn.class);
//            startActivity(intent);
//            finish();
//        }

        Title = findViewById(R.id.Title_EditText);
        Desc = findViewById(R.id.Desc_EditText);

        Add_note = findViewById(R.id.new_note);

        FloatingActionButton back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Save_Notes.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title_txt = Title.getText().toString();
                String Desc_txt = Desc.getText().toString();

                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();

                if (!Title_txt.isEmpty() && !Desc_txt.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Create a new note document
                    Map<String, Object> note = new HashMap<>();
                    note.put("Title", Title_txt);
                    note.put("Description", Desc_txt);
//                    note.put("description", Desc_txt);
                    Toast.makeText(Save_Notes.this, "" + user.getUid(), Toast.LENGTH_SHORT).show();
                    // Add the note to the "notes" collection for the current user
                    db.collection("users")
                            .document(user.getUid())
                            .update("notes", FieldValue.arrayUnion(note))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    Toast.makeText(Save_Notes.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Save_Notes.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Save_Notes.this, "Error while adding", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Save_Notes.this, "Title and Description can not be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            finish();
        }
    }
}
