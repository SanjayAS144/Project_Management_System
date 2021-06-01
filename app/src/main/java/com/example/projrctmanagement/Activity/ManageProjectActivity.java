package com.example.projrctmanagement.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projrctmanagement.ModelClasses.ImageClass;
import com.example.projrctmanagement.ModelClasses.projectDetails;
import com.example.projrctmanagement.R;
import com.example.projrctmanagement.ViewHolder.ProjectViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


public class ManageProjectActivity extends AppCompatActivity {

    FloatingActionButton fab;

    DatabaseReference projectDetailReference;
    RecyclerView project_list_view;
    DatabaseReference RootRef;
    FirebaseAuth mAuth;
    String GroupName;
    String CurrentUserId;
    List<String> usn;
    List<String> imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_project);

        project_list_view = findViewById(R.id.project_list);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        Log.d("TAG", "onCreate: " + CurrentUserId);
        usn = new ArrayList<>();
        imageUrl = new ArrayList<>();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddNewProjectActivity.class);
                intent.putExtra("group_name",GroupName);
                startActivity(intent);
            }
        });
        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageProjectActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GroupName = snapshot.child("group_name").getValue().toString();
                    Log.d("TAG", "onDataChange: " + GroupName);

                    fetchMembersImage();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchProjectDetails() {
        projectDetailReference = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("project").child("ongoing");
        FirebaseRecyclerOptions<projectDetails> options = new FirebaseRecyclerOptions.Builder<projectDetails>().setQuery(projectDetailReference, projectDetails.class).build();
        FirebaseRecyclerAdapter<projectDetails, ProjectViewHolder> adapter = new FirebaseRecyclerAdapter<projectDetails, ProjectViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProjectViewHolder holder, final int position, @NonNull final projectDetails model) {

                projectDetailReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            final String project_id = getRef(position).getKey();

                            setImage(holder);

                            projectDetailReference.child(project_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String progress = snapshot.child("progress").getValue().toString();
                                    holder.project_name.setText(snapshot.child("project_title").getValue().toString());
                                    holder.progressBar.setProgress(Integer.parseInt(progress));
                                    holder.progress.setText(progress+"%");
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(),ProjectUpdateActivity.class);
                                            intent.putExtra("group_name",GroupName);
                                            intent.putExtra("project_id",project_id);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_project_layout, parent, false);
                return new ProjectViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        project_list_view.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        project_list_view.setAdapter(adapter);
    }

    private void fetchMembersImage() {
        DatabaseReference GroupMemberRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("member_List");
        GroupMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        usn.add(childSnapshot.getValue().toString());
                    }
                }
                FetchGroupMemberDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchGroupMemberDetails() {

        DatabaseReference GroupMemberRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName);
        final DatabaseReference GroupMemberInfoRef = FirebaseDatabase.getInstance().getReference("Users");
        GroupMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (int i = 0; i < 4; i++) {
                    if (snapshot.child(usn.get(i)).exists()) {
                        GroupMemberInfoRef.child(snapshot.child(usn.get(i)).child("user_id").getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                imageUrl.add(snapshot.child("user_image").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FetchProjectDetails();

    }

    private void setImage(ProjectViewHolder holder) {
        int length = imageUrl.size();
        Log.d("TAG", "setImage: " + length + imageUrl);
        switch (length) {
            case 1:
                setGifImage(length, holder);
                holder.member2card.setVisibility(View.GONE);
                holder.member3card.setVisibility(View.GONE);
                holder.member4card.setVisibility(View.GONE);
                break;

            case 2:
                setGifImage(length, holder);
                holder.member3card.setVisibility(View.GONE);
                holder.member4card.setVisibility(View.GONE);
                break;

            case 3:
                setGifImage(length, holder);
                holder.member4card.setVisibility(View.GONE);
                break;

            default:
                setGifImage(length, holder);
                break;
        }
    }

    private void setGifImage(int n, ProjectViewHolder holder) {
        Log.d("TAG", "setGifImage: "+n);

        switch (n) {
            case 1:
                setImageResource(imageUrl.get(0), holder.member_image1);
                break;
            case 2:
                setImageResource(imageUrl.get(0), holder.member_image1);
                setImageResource(imageUrl.get(1), holder.member_image2);
                break;
            case 3:
                setImageResource(imageUrl.get(0), holder.member_image1);
                setImageResource(imageUrl.get(1), holder.member_image2);
                setImageResource(imageUrl.get(2), holder.member_image3);
                break;
            case 4:
                setImageResource(imageUrl.get(0), holder.member_image1);
                setImageResource(imageUrl.get(1), holder.member_image2);
                setImageResource(imageUrl.get(2), holder.member_image3);
                setImageResource(imageUrl.get(3), holder.member_image4);
                break;
        }
    }

    private void setImageResource(String imageResource, GifImageView imageView) {
        if (imageResource.equals("not_provided")) {
            imageView.setImageResource(R.drawable.placeholder);
        } else if (imageResource.equals("profileimage1")) {
            imageView.setImageResource(R.drawable.profileimage1);
        } else if (imageResource.equals("profileimage3")) {
            imageView.setImageResource(R.drawable.profileimage3);
        } else if (imageResource.equals("profileimage4")) {
            imageView.setImageResource(R.drawable.profileimage4);
        } else if (imageResource.equals("profileimage5")) {
            imageView.setImageResource(R.drawable.profileimage5);
        } else if (imageResource.equals("profileimage6")) {
            imageView.setImageResource(R.drawable.profileimage6);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageUrl.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageUrl.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageUrl.clear();
    }
}