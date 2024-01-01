package com.example.photoportfolio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Picture_activity extends AppCompatActivity {

    private ImageButton btnDelete;
    private TextView imageUrl;

    private TextView imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        btnDelete = findViewById(R.id.btnDelete);
        imageUrl = findViewById(R.id.idUrl);
        imageType = findViewById(R.id.imgType);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        String sanitizedEmail = userEmail.split("@")[0];

        final String storageReference = "images";
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageFromStorage(storageReference, imageUrl.getText().toString());
                deleteRecord(sanitizedEmail, imageUrl.getText().toString(), imageType.getText().toString());
            }
        });


    }


    private void deleteImageFromStorage(String storageReference, String imageUrl) {
        // Convert the URL to a Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        // Delete the file
        storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DeleteImageTask", "Image deleted from storage");
                    // You may want to update your database here to remove the reference to the image
                } else {
                    Log.e("DeleteImageTask", "Error deleting image from storage", task.getException());
                }
            }
        });
    }

    private void deleteRecord(String userEmail, String imageUrlToRemove, String pictureTypeToRemove){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference picturesRef = database.getReference("pictures");
        DatabaseReference userPicturesRef = picturesRef.child(userEmail);
        Query queryToRemove = userPicturesRef.orderByChild("imageUrl").equalTo(imageUrlToRemove).limitToFirst(1);
        queryToRemove.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Remove the specific node based on imageUrl and pictureType
                    String pictureTypeInDatabase = snapshot.child("pictureType").getValue(String.class);
                    if (pictureTypeToRemove.equals(pictureTypeInDatabase)) {
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("DeleteImageTask", "Image deleted from database");
                                            // You may want to update your database here to remove the reference to the image
                                        } else {
                                            Log.e("DeleteImageTask", "Error deleting image from storage", task.getException());
                                        }
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if necessary
            }

        });

    }




}