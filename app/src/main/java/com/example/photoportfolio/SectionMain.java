package com.example.photoportfolio;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.ImageButton;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

public class SectionMain extends AppCompatActivity {

    private GridView pictureG;

    private FirebaseAuth mAuth;

    private ImageButton btnUploadImage;
    private ImageButton btnOpenCamera;

    private TextView tvSectionTitle;

    private Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String pictureType;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_main);




        pictureG = findViewById(R.id.main_gridview);
        mAuth = FirebaseAuth.getInstance();
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);

        ArrayList<AssetModal> assetModelArrayList = new ArrayList<AssetModal>();
        Intent intent = getIntent();

        if (intent != null) {
            pictureType = intent.getStringExtra("title");
            // Use the receivedText as needed
            String form = pictureType +" " + String.valueOf(assetModelArrayList.size()) + " Pictures";
            tvSectionTitle.setText(form);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        retrieveUserImagesByType(pictureType, assetModelArrayList, tvSectionTitle);



        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            imageUri = data.getData();
                            int count = assetModelArrayList.size() + 1;
                            assetModelArrayList.add(new AssetModal(String.valueOf(count),imageUri, "", pictureType));
                            updateGrid(assetModelArrayList);
                            uploadImage();

                        }
                    }
        });


        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadImageClick(someActivityResultLauncher);

            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenCameraClick(someActivityResultLauncher);
            }
        });




    }

    public void updateGrid(ArrayList<AssetModal> assetModelArrayList){
        PictureAdapter adapter = new PictureAdapter(this, assetModelArrayList);
        pictureG.setAdapter(adapter);
    }


    public void onUploadImageClick(ActivityResultLauncher<Intent> someActivityResultLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);

    }

    public void onOpenCameraClick(ActivityResultLauncher<Intent> someActivityResultLauncher) {

    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {

                                    storeImageUrlInRealtimeDatabase(downloadUri.toString(), pictureType);
                                }
                            });

                            Toast.makeText(SectionMain.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SectionMain.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    private void storeImageUrlInRealtimeDatabase(String imageUrl, String pictureType) {
        // Assuming you have initialized the Realtime Database in your activity
        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Get the user's email
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                String sanitizedEmail = userEmail.split("@")[0];

                // Create a reference to the "pictures" node in the database
                DatabaseReference picturesRef = database.getReference("pictures");

                // Create a child reference with the user's email
                DatabaseReference userPicturesRef = picturesRef.child(sanitizedEmail);

                // Generate a unique key for the new data
                String pictureId = userPicturesRef.push().getKey();

                // Create a child reference with the unique key
                DatabaseReference pictureRef = userPicturesRef.child(pictureId);

                // Create a data object with the image URL and picture type
                Map<String, Object> pictureData = new HashMap<>();
                pictureData.put("imageUrl", imageUrl);
                pictureData.put("pictureType", pictureType);

                // Set the data in the Realtime Database
                pictureRef.setValue(pictureData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SectionMain.this, "Image URL stored in Realtime Database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SectionMain.this, "Failed to store image URL in Realtime Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }catch (Exception e){
            Log.e("YourTag", e.getMessage() + e.getMessage(), e);
        }
    }

    private void retrieveUserImagesByType(String pictureType, ArrayList<AssetModal> assetModelArrayList, TextView tvSectionTitle) {
        // Assuming you have initialized the Realtime Database in your activity
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Get the user's email
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                String sanitizedEmail = userEmail.split("@")[0];

                // Create a reference to the "pictures" node in the database
                DatabaseReference picturesRef = database.getReference("pictures");

                // Create a child reference with the user's email
                DatabaseReference userPicturesRef = picturesRef.child(sanitizedEmail);

                // Retrieve all images with the specified type
                userPicturesRef.orderByChild("pictureType").equalTo(pictureType)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Iterate through the retrieved data
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Access the imageUrl and other data
                                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                                    String retrievedPictureType = snapshot.child("pictureType").getValue(String.class);
                                    int count = assetModelArrayList.size() + 1;
                                    Uri imageUri = Uri.parse(imageUrl);
                                    assetModelArrayList.add(new AssetModal(String.valueOf(count),imageUri, imageUrl, pictureType));
                                    updateGrid(assetModelArrayList);
                                    String form = pictureType +" " + String.valueOf(assetModelArrayList.size()) + " Pictures";
                                    tvSectionTitle.setText(form);
                                    // Perform actions with the retrieved data
                                    Log.d("YourTag", "Image URI: " + imageUri + ", Picture Type: " + retrievedPictureType);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("YourTag", "Error retrieving data: " + databaseError.getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            Log.e("YourTag", "Error: " + e.getMessage(), e);
        }
    }



}