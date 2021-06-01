package com.example.projrctmanagement.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class SendMessageActivity extends AppCompatActivity {

    EditText text_msg;
    ImageView send_btn,attach;
    String Message_type;
    String Message;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    FirebaseAuth mAuth;
    String userId,messageId;
    DatabaseReference messageRef,userRef;
    String GroupName;
    ImageView add_image;
    LoadingDialog loadingDialog;
    private static final int REQUEST_CODE = 1;
    private StorageReference mstorageref;
    String project_id;
    String type = "text";
    Uri pdfData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Intent intent = getIntent();
        project_id = intent.getStringExtra("project_id");
        text_msg = findViewById(R.id.text_msg);

        send_btn = findViewById(R.id.send_btn);
        add_image = findViewById(R.id.add_image);
        attach = findViewById(R.id.attach);
        calendar = Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);
        userId = mAuth.getCurrentUser().getUid();
        mstorageref = FirebaseStorage.getInstance().getReference();

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageActivity.super.onBackPressed();
            }
        });

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("group_name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroupName = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "pdf";
                Message_type = "PDF";
                selectPDF();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                messageRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("project").child("ongoing").child(project_id).child("Chat").push();
                String message_id = messageRef.getKey();
                Message = text_msg.getText().toString().trim();
                dateFormat = new SimpleDateFormat("dd MMM");
                date = dateFormat.format(calendar.getTime());
                Log.d("TAG", "onClick: "+date);
                if(TextUtils.isEmpty(Message)){

                }else{
                    if(type.equals("pdf")){

                        Message_type = "PDF";
                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("PDF Files").child(message_id+".pdf");
                        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isComplete());
                                Uri uri = uriTask.getResult();

                                HashMap<String,String> messageMap = new HashMap<>();
                                messageMap.put("date",date);
                                messageMap.put("file_name",Message);
                                messageMap.put("message_type",Message_type);
                                messageMap.put("Message",uri.toString());
                                messageMap.put("sender",userId);

                                messageRef.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            loadingDialog.hide();
                                            SendMessageActivity.super.onBackPressed();
                                        }
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            }
                        });

                    }else if(type.equals("text")){
                        Message_type = "text";
                        HashMap<String,String> messageMap = new HashMap<>();
                        messageMap.put("date",date);
                        messageMap.put("message_type",Message_type);
                        messageMap.put("Message",Message);
                        messageMap.put("sender",userId);

                        messageRef.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    loadingDialog.hide();
                                    SendMessageActivity.super.onBackPressed();
                                }
                            }
                        });
                    }
                }
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "image";
                Message_type = "image";
                camerapermission();
            }
        });

    }

    private void selectPDF() {
        Intent pdfIntent = new Intent();
        pdfIntent.setType("application/pdf");
        pdfIntent.setAction(pdfIntent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pdfIntent,"PDF FILE SELECT"),12);
    }

    private void camerapermission() {
        String[] permission = {Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[0]) == PackageManager.PERMISSION_GRANTED) {
            CropImage.activity().start(SendMessageActivity.this);
        } else {
            ActivityCompat.requestPermissions(SendMessageActivity.this, permission, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(type.equals("image")){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    loadingDialog.show();
                    Uri resultUri = result.getUri();
                    File thumb_file = new File(resultUri.getPath());
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setQuality(60)
                            .compressToBitmap(thumb_file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();
                    messageRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("project").child("ongoing").child(project_id).child("Chat").push();
                    messageId = messageRef.getKey();
                    final StorageReference filepath = mstorageref.child("Project_images").child(userId).child(messageId + ".jpg");

                    filepath.putBytes(thumb_byte).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final String downloadurl = task.getResult().toString();
                                dateFormat = new SimpleDateFormat("dd MMM");
                                date = dateFormat.format(calendar.getTime());

                                HashMap<String,String> messageMap = new HashMap<>();
                                messageMap.put("date",date);
                                messageMap.put("message_type",Message_type);
                                messageMap.put("Message",downloadurl);
                                messageMap.put("sender",userId);

                                messageRef.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadingDialog.hide();
                                        SendMessageActivity.super.onBackPressed();
                                    }
                                });
                            }
                        }
                    });

                }

            }
        }else if(type.equals("pdf")){
            if(requestCode ==12 && resultCode == RESULT_OK && data != null && data.getData() != null){
                text_msg.setText(data.getDataString()
                        .substring(data.getDataString().lastIndexOf("/")+1));

                 pdfData = data.getData();

            }
        }

    }
}