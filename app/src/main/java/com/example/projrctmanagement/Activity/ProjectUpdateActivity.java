package com.example.projrctmanagement.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.ModelClasses.projectDetails;
import com.example.projrctmanagement.R;
import com.example.projrctmanagement.ViewHolder.ProjectViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import anil.sardiwal.reboundrecycler.ReboundRecycler;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import pl.droidsonroids.gif.GifImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ProjectUpdateActivity extends AppCompatActivity {

    MaterialCardView shareCard;
    RecyclerView message_list;
    DatabaseReference messageRef;
    String GroupName,project_id;
    LoadingDialog loadingDialog;
    DatabaseReference UserRef;
    TextView project_title,project_description,progress;
    ProgressIndicator progressIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_update);

        Intent intent = getIntent();
        GroupName = intent.getStringExtra("group_name");
        project_id = intent.getStringExtra("project_id");
        Toast.makeText(this, project_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, GroupName, Toast.LENGTH_SHORT).show();
        loadingDialog = new LoadingDialog(this);

        project_title = findViewById(R.id.project_title);
        project_description = findViewById(R.id.project_description);
        progress = findViewById(R.id.progress);
        progressIndicator = findViewById(R.id.progressBar);

        shareCard = findViewById(R.id.shareCard);
        message_list = findViewById(R.id.message_list);
        message_list.setHasFixedSize(true);
        message_list.setLayoutManager(new LinearLayoutManager(this));
        ReboundRecycler.init(message_list);
        OverScrollDecoratorHelper.setUpOverScroll(message_list, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        messageRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("project").child("ongoing").child(project_id).child("Chat");
        messageRef.keepSynced(true);

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectUpdateActivity.super.onBackPressed();
            }
        });

        shareCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SendMessageActivity.class);
                intent.putExtra("project_id",project_id);
                startActivity(intent);
            }
        });

        FetchMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference projectRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("project").child("ongoing").child(project_id);
        projectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    project_title.setText(snapshot.child("project_title").getValue().toString());
                    project_description.setText(snapshot.child("project_description").getValue().toString());
                    progress.setText(snapshot.child("progress").getValue().toString()+"%");
                    progressIndicator.setProgress(Integer.parseInt(snapshot.child("progress").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FetchMessages();
    }

    private void FetchMessages() {
        FirebaseRecyclerOptions<projectDetails> options = new FirebaseRecyclerOptions.Builder<projectDetails>().setQuery(messageRef, projectDetails.class).build();
        FirebaseRecyclerAdapter<projectDetails, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<projectDetails, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position, @NonNull final projectDetails model) {

                ReboundRecycler.bind(holder.itemView,position);
                final Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_animation);

                final String message_id = getRef(position).getKey();
                Log.d("TAG", "onBindViewHolder: "+message_id);
                    messageRef.child(message_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                holder.sent_date.setText(snapshot.child("date").getValue().toString());
                                if(snapshot.child("message_type").getValue().toString().equals("text")){
                                    holder.text_msg.setVisibility(View.VISIBLE);
                                    holder.msg_image_card.setVisibility(View.GONE);
                                    holder.pdfLayout.setVisibility(View.GONE);
                                    holder.text_msg.setText(snapshot.child("Message").getValue().toString());
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.itemView.startAnimation(myAnim);
                                        }
                                    });
                                }else if(snapshot.child("message_type").getValue().toString().equals("image")){
                                    holder.text_msg.setVisibility(View.GONE);
                                    holder.msg_image_card.setVisibility(View.VISIBLE);
                                    holder.pdfLayout.setVisibility(View.GONE);
                                    Picasso.get().load(snapshot.child("Message").getValue().toString()).into(holder.msg_image);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.itemView.startAnimation(myAnim);
                                            Intent intent = new Intent(getApplicationContext(), PhotoViewerActivity.class);
                                            intent.putExtra("msg_image", snapshot.child("Message").getValue().toString());
                                            intent.putExtra("project_id", project_id);
                                            intent.putExtra("group_name", GroupName);
                                            startActivity(intent);
                                        }
                                    });
                                }else if(snapshot.child("message_type").getValue().toString().equals("PDF")){
                                    holder.text_msg.setVisibility(View.GONE);
                                    holder.msg_image_card.setVisibility(View.GONE);
                                    holder.pdfLayout.setVisibility(View.VISIBLE);
                                    holder.file_name.setText(snapshot.child("file_name").getValue().toString()+".pdf");

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                                            downloadFiles(ProjectUpdateActivity.this,
//                                                    snapshot.child("file_name").getValue().toString(),
//                                                    ".pdf",
//                                                    DIRECTORY_DOWNLOADS,snapshot.child("Message").getValue().toString());

                                            Intent intent = new Intent(getApplicationContext(),PdfReaderActivity.class);
                                            intent.putExtra("pdf_file",snapshot.child("Message").getValue().toString());
                                            startActivity(intent);
                                        }
                                    });

                                }

                                UserRef.child(snapshot.child("sender").getValue().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        setImageResource(snapshot.child("user_image").getValue().toString(),holder.Sender_image);
                                        holder.sender_name.setText(snapshot.child("user_name").getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
                ReboundRecycler.first((ViewGroup) view);
                return new MessageViewHolder(view);
            }
        };
        adapter.startListening();
        message_list.setAdapter(adapter);

    }

    private class MessageViewHolder extends RecyclerView.ViewHolder{

        GifImageView Sender_image;
        TextView sender_name,sent_date,text_msg,file_name;
        ImageView msg_image;
        MaterialCardView msg_image_card;
        LinearLayout pdfLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            Sender_image = itemView.findViewById(R.id.sender_image);
            sender_name = itemView.findViewById(R.id.sender_name);
            sent_date = itemView.findViewById(R.id.sent_date);
            text_msg = itemView.findViewById(R.id.text_msg);
            msg_image = itemView.findViewById(R.id.msg_image);
            msg_image_card = itemView.findViewById(R.id.msg_image_card);
            pdfLayout = itemView.findViewById(R.id.pdf_layout);
            file_name = itemView.findViewById(R.id.file_name);
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

    public void downloadFiles(Context context, String filename, String fileExtension, String destinationDirectory, String url ){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,filename+fileExtension);
        downloadManager.enqueue(request);
    }
}