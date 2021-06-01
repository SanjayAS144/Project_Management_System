package com.example.projrctmanagement.InformationActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.MainActivity;
import com.example.projrctmanagement.ModelClasses.ModleGetSet;
import com.example.projrctmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class InfoActivity extends AppCompatActivity {


    //Model class
    ModleGetSet modleGetSet;

    RelativeLayout userLayout, teamLayout;
    RelativeLayout continuebtn, connect;
    private boolean isGroupExist = false;

    //team info layout attributes
    EditText Member1, Member2, Member3, Member4;
    LinearLayout enter_info_layout, select_info_layout;
    TextView usn1, usn2, usn3, usn4;
    MaterialCheckBox checkBox1, checkBox2, checkBox3, checkBox4;


    //user info layout attributes
    EditText username, userPhone;
    LoadingDialog loadingDialog;
    String current_user_usn;
    String user_image = "not_provided";
    RelativeLayout add_profile_image;
    GifImageView profile_image;


    //Constants
    String userEmail, groupName, userId, user_name, user_phone, member1, member2, member3, member4;

    //Database attributes
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, groupRef;
    boolean is1checkable = true, is2checkable = true, is3checkable = true, is4checkable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        userLayout = findViewById(R.id.user_info_layout);
        teamLayout = findViewById(R.id.team_info_layout);
        connect = findViewById(R.id.connect);
        continuebtn = findViewById(R.id.continuebtn);
        Member1 = findViewById(R.id.userusn);
        Member2 = findViewById(R.id.member1);
        Member3 = findViewById(R.id.member2);
        Member4 = findViewById(R.id.member3);
        enter_info_layout = findViewById(R.id.enter_info_layout);
        select_info_layout = findViewById(R.id.select_info_layout);
        username = findViewById(R.id.username);
        userPhone = findViewById(R.id.userPhone);
        add_profile_image = findViewById(R.id.add_profile_image);
        profile_image = findViewById(R.id.profile_image);
        usn1 = findViewById(R.id.usn1);
        usn2 = findViewById(R.id.usn2);
        usn3 = findViewById(R.id.usn3);
        usn4 = findViewById(R.id.usn4);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);


        loadingDialog = new LoadingDialog(this);

        add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageDialog();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.child("group_name").getValue().toString();
                groupRef.child(groupName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isGroupExist = true;
                        } else {
                            isGroupExist = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_name = username.getText().toString().trim();
                user_phone = userPhone.getText().toString().trim();
                if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_phone)) {
                    Toast.makeText(InfoActivity.this, "Please Enter All the Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (user_phone.length() != 10) {
                        Toast.makeText(InfoActivity.this, "Please Give correct NUmber", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        userLayout.setVisibility(View.GONE);
                        teamLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGroupExist) {
                    loadingDialog.show();
                    HashMap<String, String> SingleMemberMap = new HashMap<>();
                    SingleMemberMap.put("user_id", userId);
                    SingleMemberMap.put("user_name", user_name);

                    DatabaseReference CurrentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    Map<String, Object> user_Update_map = new HashMap<>();
                    user_Update_map.put("usn_number", current_user_usn);
                    user_Update_map.put("user_name", user_name);
                    user_Update_map.put("user_phone", user_phone);
                    user_Update_map.put("user_image", user_image);
                    groupRef.child(groupName).child(current_user_usn).setValue(SingleMemberMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                            }
                        }
                    });

                    Log.d("TAG", "onComplete: " + user_name);
                    Log.d("TAG", "onComplete: " + user_phone);
                    Log.d("TAG", "onComplete: " + current_user_usn);

                    CurrentUserRef.updateChildren(user_Update_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "onComplete: " + "yes syccess");
                                loadingDialog.hide();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Log.d("Error", "onComplete: " + task.getException().getMessage());
                            }
                        }
                    });

                }
                else {
                    member1 = Member1.getText().toString().trim();
                    member2 = Member2.getText().toString().trim();
                    member3 = Member3.getText().toString().trim();
                    member4 = Member4.getText().toString().trim();

                    if (TextUtils.isEmpty(member1) || TextUtils.isEmpty(member2) || TextUtils.isEmpty(member3) || TextUtils.isEmpty(member4)) {
                        Toast.makeText(InfoActivity.this, "Please Fill the Details", Toast.LENGTH_SHORT).show();
                    } else {
                        loadingDialog.show();
                        HashMap<String, String> memberMap = new HashMap<>();
                        memberMap.put("member1", member1);
                        memberMap.put("member2", member2);
                        memberMap.put("member3", member3);
                        memberMap.put("member4", member4);

                        groupRef.child(groupName).child("member_List").setValue(memberMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, String> SingleMemberMap = new HashMap<>();
                                    SingleMemberMap.put("user_id", userId);
                                    SingleMemberMap.put("user_name", user_name);


                                    groupRef.child(groupName).child(member1).setValue(SingleMemberMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Map<String, Object> user_map = new HashMap<>();
                                                user_map.put("usn_number", member1);
                                                user_map.put("user_name", user_name);
                                                user_map.put("user_phone", user_phone);
                                                user_map.put("user_image", user_image);
                                                rootRef.updateChildren(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            loadingDialog.hide();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(InfoActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }
            }
        });

        toggelCheckBox();

    }

    @Override
    protected void onStart() {
        super.onStart();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.child("group_name").getValue().toString();
                groupRef.child(groupName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isGroupExist = true;
                            Member1.setEnabled(false);
                            Member1.setText("Select Your USN number");
                            enter_info_layout.setVisibility(View.GONE);
                            select_info_layout.setVisibility(View.VISIBLE);

                            member1 = snapshot.child("member_List").child("member1").getValue().toString();
                            member2 = snapshot.child("member_List").child("member2").getValue().toString();
                            member3 = snapshot.child("member_List").child("member3").getValue().toString();
                            member4 = snapshot.child("member_List").child("member4").getValue().toString();

                            usn1.setText(member1);
                            usn2.setText(member2);
                            usn3.setText(member3);
                            usn4.setText(member4);

                            if (snapshot.child(member1).exists()) {
                                checkBox1.setChecked(true);
                                checkBox1.setEnabled(false);
                                is1checkable = false;
                            }
                            if (snapshot.child(member2).exists()) {
                                checkBox2.setChecked(true);
                                checkBox2.setEnabled(false);
                                is2checkable = false;
                            }
                            if (snapshot.child(member3).exists()) {
                                checkBox3.setChecked(true);
                                checkBox3.setEnabled(false);
                                is3checkable = false;
                            }
                            if (snapshot.child(member4).exists()) {
                                checkBox4.setChecked(true);
                                checkBox4.setEnabled(false);
                                is4checkable = false;
                            }

                        } else {
                            isGroupExist = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void toggelCheckBox() {

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (checkBox2.isChecked()) {
                    Member1.setText(member2);
                    current_user_usn = member2;

                    if (is1checkable) {
                        if (checkBox1.isChecked()) checkBox1.setChecked(false);
                    }

                    if (is3checkable) {
                        if (checkBox3.isChecked()) checkBox3.setChecked(false);
                    }

                    if (is4checkable) {
                        if (checkBox4.isChecked()) checkBox4.setChecked(false);
                    }

                }

            }
        });


        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean c) {

                if (checkBox3.isChecked()) {

                    Member1.setText(member3);
                    current_user_usn = member3;
                    if (is1checkable) {
                        if (checkBox1.isChecked()) checkBox1.setChecked(false);
                    }

                    if (is2checkable) {
                        if (checkBox2.isChecked()) checkBox2.setChecked(false);
                    }

                    if (is4checkable) {
                        if (checkBox4.isChecked()) checkBox4.setChecked(false);
                    }

                }


            }
        });

        checkBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean d) {

                if (checkBox4.isChecked()) {
                    Member1.setText(member4);
                    current_user_usn = member4;

                    if (is1checkable) {
                        if (checkBox1.isChecked()) checkBox1.setChecked(false);
                    }

                    if (is3checkable) {
                        if (checkBox3.isChecked()) checkBox3.setChecked(false);
                    }

                    if (is2checkable) {
                        if (checkBox4.isChecked()) checkBox2.setChecked(false);
                    }

                }
            }
        });
    }

    private void profileImageDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.pick_a_profile,null);
        Button gallery,cancel;
        final MaterialCardView profile1,profile2,profile3,profile4,profile5;

        profile1 = view.findViewById(R.id.Profile1);
        profile2 = view.findViewById(R.id.Profile2);
        profile3 = view.findViewById(R.id.Profile3);
        profile4 = view.findViewById(R.id.Profile4);
        profile5 = view.findViewById(R.id.Profile5);


        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alertdialogbackground));
        alertDialog.show();


        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleGetSet = new ModleGetSet("profileimage1");
                modleGetSet.setProfileImage("profileimage1");
                user_image = modleGetSet.getProfileImage();
                profile_image.setImageResource(R.drawable.profileimage1);
                alertDialog.dismiss();
            }
        });

        profile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleGetSet = new ModleGetSet("profileimage5");
                modleGetSet.setProfileImage("profileimage5");
                profile_image.setImageResource(R.drawable.profileimage5);
                user_image = modleGetSet.getProfileImage();
                alertDialog.dismiss();
            }
        });

        profile5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleGetSet = new ModleGetSet("profileimage3");
                modleGetSet.setProfileImage("profileimage3");
                profile_image.setImageResource(R.drawable.profileimage3);
                user_image = modleGetSet.getProfileImage();
                alertDialog.dismiss();
            }
        });

        profile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleGetSet = new ModleGetSet("profileimage4");
                modleGetSet.setProfileImage("profileimage4");
                profile_image.setImageResource(R.drawable.profileimage4);
                user_image = modleGetSet.getProfileImage();
                alertDialog.dismiss();
            }
        });

        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleGetSet = new ModleGetSet("profileimage6");
                modleGetSet.setProfileImage("profileimage6");
                profile_image.setImageResource(R.drawable.profileimage6);
                user_image = modleGetSet.getProfileImage();
                alertDialog.dismiss();
            }
        });
    }

}