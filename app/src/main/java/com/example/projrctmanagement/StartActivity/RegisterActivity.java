package com.example.projrctmanagement.StartActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.InformationActivity.InfoActivity;
import com.example.projrctmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    ImageButton onCancelBtn;
    MaterialButton signUp;
    EditText groupName,email,password;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingDialog = new LoadingDialog(RegisterActivity.this);

        onCancelBtn = findViewById(R.id.oncanclebtn);
        signUp = findViewById(R.id.signUp);
        groupName = findViewById(R.id.group_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        mAuth=FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String group_name = groupName.getText().toString().trim();
                final String userEmail = email.getText().toString().trim();
                final String userPassword = password.getText().toString().trim();
                if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(group_name))
                {

                    Toast.makeText(RegisterActivity.this, "Please Fill all the credentials", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    group_name.subSequence(0,2);
                    Pattern p = Pattern.compile("[^SG0-9]");
                    Matcher m = p.matcher(group_name);
                    if (m.find())
                    {
                        Toast.makeText(RegisterActivity.this, "Group name is not in its format", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        loadingDialog.show();
                        register_user(group_name, userEmail, userPassword);
                    }
                }


            }
        });

        onCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.super.onBackPressed();
            }
        });
    }

    private void register_user(final String group_name, final String userEmail, String userPassword) {

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                            String UID = current_user.getUid();
                            rootRef = FirebaseDatabase.getInstance().getReference("Users").child(UID);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("email",userEmail);
                            userMap.put("group_name",group_name);
                            rootRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingDialog.hide();
                                        Intent userInfoIntent = new Intent(getApplicationContext(),InfoActivity.class);
                                        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(userInfoIntent);
                                        finish();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                        loadingDialog.hide();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loadingDialog.hide();
                        }
                    }
                });

    }

}