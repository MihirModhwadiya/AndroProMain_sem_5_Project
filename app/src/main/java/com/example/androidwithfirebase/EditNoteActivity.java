package com.example.androidwithfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {
    EditText editTitle, editDescription;
    FloatingActionButton updateButton;
    int position;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseUser user;
    public FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_notes);

        editTitle = findViewById(R.id.Title_EditText);
        editDescription = findViewById(R.id.Desc_EditText);
        updateButton = findViewById(R.id.new_note);

        // Retrieve the data for the selected note
        Intent intent = getIntent();
        String originalTitle = intent.getStringExtra("title");
        String originalDescription = intent.getStringExtra("description");
        position = intent.getIntExtra("position", -1);

//         Set the retrieved data in the EditText fields
        editTitle.setText(originalTitle);
        editDescription.setText(originalDescription);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the update logic here
                String updatedTitle = editTitle.getText().toString();
                String updatedDescription = editDescription.getText().toString();

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    String userUid = currentUser.getUid();
                    DocumentReference userDocRef = db.collection("users").document(userUid);

                    userDocRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        // Get the user data
                                        Map<String, Object> userData = documentSnapshot.getData();

                                        if (userData != null && userData.containsKey("notes")) {
                                            // Get the notes subarray
                                            List<Map<String, Object>> notesList = (List<Map<String, Object>>) userData.get("notes");

                                            // Ensure that the position is within valid bounds
                                            if (position >= 0 && position < notesList.size()) {
                                                // Update the note data for the specified position
                                                notesList.get(position).put("Title", updatedTitle);
                                                notesList.get(position).put("Description", updatedDescription);

                                                // Update the document in Firebase Firestore
                                                userDocRef.update("notes", notesList)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Note updated successfully in Firebase
                                                                // You may want to notify the user or go back to the MainActivity
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(EditNoteActivity.this, "Error updating document", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditNoteActivity.this, "Error fetching user document", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }
        });
    }
}
