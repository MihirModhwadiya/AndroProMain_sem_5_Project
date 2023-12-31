package com.example.androidwithfirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    ListView notesListView;
    public String[] titleArray;
    public String[] descriptionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetch();

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton new_note = findViewById(R.id.new_note);
        FloatingActionButton log_out = findViewById(R.id.log_out);

        notesListView = findViewById(R.id.lst_view);

//        if (mAuth.getCurrentUser() == null) {
//            Intent intent = new Intent(this, SignIn.class);
//            startActivity(intent);
//            finish();
//        }

        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Save_Notes.class);
                startActivity(intent);
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle the click on a note to edit it
                edit(i);
            }
        });

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete(i);
                return false;
            }
        });
    }

    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent(MainActivity.this, SignIn.class);
        startActivity(intent);
        finish();
    }

    public void edit(int position) {
        if (position >= 0 && position < titleArray.length) {
            // Start the EditNoteActivity and pass the data for the selected note
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("title", titleArray[position]);
            intent.putExtra("description", descriptionArray[position]);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    public void delete(int position) {
        // Ensure that the position is within valid bounds
        if (position >= 0 && position < titleArray.length) {
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
                                            // Remove the element from the notes subarray
                                            notesList.remove(position);

                                            // Update the document in Firebase Firestore
                                            userDocRef.update("notes", notesList)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Note deleted successfully in Firebase
                                                            // You may want to refresh your local data as well
                                                            fetch();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
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
                                Log.w(TAG, "Error fetching user document", e);
                            }
                        });
            }
        }
    }

    public void fetch() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(userUid);

            userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(MainActivity.this, "Listen failed.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();

                        if (userData != null && userData.containsKey("notes")) {
                            Object notesData = userData.get("notes");

                            if (notesData instanceof List) {
                                List<Map<String, Object>> notesList = (List<Map<String, Object>>) notesData;

                                List<String> titles = new ArrayList<>();
                                List<String> descriptions = new ArrayList();
                                for (Map<String, Object> noteData : notesList) {
                                    String title = noteData.get("Title").toString();
                                    String description = noteData.get("Description").toString();

                                    titles.add(title);
                                    descriptions.add(description);
                                }
                                titleArray = titles.toArray(new String[0]);
                                descriptionArray = descriptions.toArray(new String[0]);

                                Notes_info customAdapter = new Notes_info(titleArray, descriptionArray, MainActivity.this);
                                notesListView.setAdapter(customAdapter);
                            } else {
                                Toast.makeText(MainActivity.this, "Notes data is not in the expected format (List)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "no user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            finish();
        }
    }
}