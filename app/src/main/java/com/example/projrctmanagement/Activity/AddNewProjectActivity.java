package com.example.projrctmanagement.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projrctmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class AddNewProjectActivity extends AppCompatActivity {
    EditText project_title,project_description;
    MaterialButton save_btn;
    DatabaseReference projectRef;
    String project_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);

        project_title = findViewById(R.id.project_title);
        project_description = findViewById(R.id.project_description);
        save_btn = findViewById(R.id.save_btn);
        Intent intent = getIntent();
        projectRef = FirebaseDatabase.getInstance().getReference("Groups").child(intent.getStringExtra("group_name")).child("project").child("ongoing").push();
        project_id = projectRef.getKey();

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewProjectActivity.super.onBackPressed();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dTitle = project_title.getText().toString().trim();
                String dDescription = project_description.getText().toString().trim();
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                if(TextUtils.isEmpty(dTitle) || TextUtils.isEmpty(dDescription)){
                    Toast.makeText(AddNewProjectActivity.this, "Please Fill all the details", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, String> project_map = new HashMap<>();
                    project_map.put("project_title",dTitle);
                    project_map.put("project_description",dDescription);
                    project_map.put("color",String.valueOf(color));
                    project_map.put("progress","0");

                    projectRef.setValue(project_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),ManageProjectActivity.class));
                                finish();
                            }
                        }
                    });
                }

            }
        });


    }
}