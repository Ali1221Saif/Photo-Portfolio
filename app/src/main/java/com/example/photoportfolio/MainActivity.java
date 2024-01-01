package com.example.photoportfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView sectionGV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionGV = findViewById(R.id.main_gridview);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(currentUser.getEmail());

        ArrayList<SectionModal> sectionModelArrayList = new ArrayList<SectionModal>();
        sectionModelArrayList.add(new SectionModal("Personal", R.drawable.personal));
        sectionModelArrayList.add(new SectionModal("Nature", R.drawable.nature));
        sectionModelArrayList.add(new SectionModal("Travel", R.drawable.travel));
        sectionModelArrayList.add(new SectionModal("Food", R.drawable.food));
        sectionModelArrayList.add(new SectionModal("Art and Creativity", R.drawable.art));
        sectionModelArrayList.add(new SectionModal("Others", R.drawable.others));


        ItemAdapter adapter = new ItemAdapter(this, sectionModelArrayList);
        sectionGV.setAdapter(adapter);
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase sign out
                FirebaseAuth.getInstance().signOut();

                // Navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the current activity to prevent navigating back with the back button
            }
        });

        sectionGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.idTVSection);

                String textToPass = textView.getText().toString();

                // Handle item click here
                Intent intent = new Intent(MainActivity.this, SectionMain.class);

                intent.putExtra("title", textToPass);

                startActivity(intent);
            }
        });



    }
}