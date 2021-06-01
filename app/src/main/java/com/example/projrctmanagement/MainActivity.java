package com.example.projrctmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projrctmanagement.Activity.AddNewProjectActivity;
import com.example.projrctmanagement.Activity.ManageProjectActivity;
import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.InformationActivity.InfoActivity;
import com.example.projrctmanagement.ModelClasses.ImageClass;
import com.example.projrctmanagement.ModelClasses.projectDetails;
import com.example.projrctmanagement.StartActivity.LoginActivity;
import com.example.projrctmanagement.StartActivity.RegisterActivity;
import com.example.projrctmanagement.ViewHolder.ProjectViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    String member1imageUrl="not_exist",member2imageUrl ="not_exist",member3imageUrl="not_exist",member4imageUrl="not_exist";

    TextView profile_name;
    TextView group_name;
    LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,GroupMemberRef,GroupMemberInfoRef,TaskRef;
    private PieChart pieChart;
    private ArrayList<PieEntry> goals;
    private List<Integer> colors;
    EditText goal,goalPercent;
    Button setGoal,cancelGoal;
    MaterialButton setBtn;
    RelativeLayout LayoutSetGoal,graph_layout;
    String Goal,GoalPercentage,RemainingWork;
    TextView textGoal;
    String CurrentUserUsn,CurrentUserId,GroupName;
    List<String> usn;
    TextView CurrentUserName,CurrentUserNumber,CurrentUserEmail,CurrentUserUSN;
    TextView Member1name,Member1Phone,Member1email,Member1usn;
    TextView Member2name,Member2Phone,Member2email,Member2usn;
    TextView Member3name,Member3Phone,Member3email,Member3usn;
    RelativeLayout Member2Layout,Member1Available,Member3Layout,Member2Available,Member4Layout,Member3Available;
    ShapeableImageView MenuBtn;
    Map<String,Object> TaskMap;
    String goal_for_today;
    String goal_percent;
    String remaining_percent;
    GifImageView member1_image,member2_image,member3_image,member4_image;
    public MaterialCardView add_project_card;
    public MaterialCardView manage_card;
    ImageClass imageClass;
    MaterialButton invite1,invite2,invite3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageClass = new ImageClass();

        InitializationControl();


        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        MenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        add_project_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddNewProjectActivity.class);
                intent.putExtra("group_name",GroupName);
                startActivity(intent);

            }
        });

        invite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsAppMsg();
            }
        });

        invite2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsAppMsg();
            }
        });

        invite3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsAppMsg();
            }
        });

    }

    public void InitializationControl(){

        pieChart = findViewById(R.id.pieChart);
        LayoutSetGoal = findViewById(R.id.layoutSetGoal);
        graph_layout = findViewById(R.id.graph_layout);
        textGoal = findViewById(R.id.textGoal);
        MenuBtn = findViewById(R.id.menuBtn);

        profile_name = findViewById(R.id.profile_name);
        group_name = findViewById(R.id.group_name);

        CurrentUserName = findViewById(R.id.member1_name);
        member1_image = findViewById(R.id.member1_image);
        CurrentUserEmail = findViewById(R.id.member1_email);
        CurrentUserNumber = findViewById(R.id.member1_phone);
        CurrentUserUSN = findViewById(R.id.member1_usn);

        Member1name = findViewById(R.id.member2_name);
        member2_image = findViewById(R.id.member2_image);
        Member1email = findViewById(R.id.member2_email);
        Member1Phone = findViewById(R.id.member2_phone);
        Member1usn = findViewById(R.id.member2_usn);
        Member1Available = findViewById(R.id.memberavailable1);
        Member2Layout = findViewById(R.id.member2layout);

        Member2name = findViewById(R.id.member3_name);
        member3_image = findViewById(R.id.member3_image);
        Member2email = findViewById(R.id.member3_email);
        Member2Phone = findViewById(R.id.member3_phone);
        Member2usn = findViewById(R.id.member3_usn);
        Member2Available = findViewById(R.id.memberavailable2);
        Member3Layout = findViewById(R.id.member3layout);

        Member3name = findViewById(R.id.member4_name);
        member4_image = findViewById(R.id.member4_image);
        Member3email = findViewById(R.id.member4_email);
        Member3Phone = findViewById(R.id.member4_phone);
        Member3usn = findViewById(R.id.member4_usn);
        Member3Available = findViewById(R.id.memberavailable3);
        Member4Layout = findViewById(R.id.member4layout);

        invite1 = findViewById(R.id.invite1);
        invite2 = findViewById(R.id.invite2);
        invite3 = findViewById(R.id.invite3);

        add_project_card = findViewById(R.id.add_project_card);
        manage_card = findViewById(R.id.manage_card);

        loadingDialog = new LoadingDialog(this);

        setBtn = findViewById(R.id.set);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();


        usn = new ArrayList<>();




    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser== null){
            sendtostart();
        }else{
            VerifyUserExistence();
        }
    }

    private void sendtostart() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void VerifyUserExistence() {
        loadingDialog.show();
        final String curentUserID = mAuth.getCurrentUser().getUid();
        CurrentUserId = curentUserID;

        RootRef.child("Users").child(curentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("user_name").exists())) {
                    GroupName = dataSnapshot.child("group_name").getValue().toString();
                    group_name.setText(dataSnapshot.child("group_name").getValue().toString());
                    CurrentUserUsn = dataSnapshot.child("usn_number").getValue().toString();
                    CurrentUserName.setText(dataSnapshot.child("user_name").getValue().toString());
                    profile_name.setText(dataSnapshot.child("user_name").getValue().toString());
                    CurrentUserUSN.setText(CurrentUserUsn);
                    CurrentUserEmail.setText(dataSnapshot.child("email").getValue().toString());
                    CurrentUserNumber.setText(dataSnapshot.child("user_phone").getValue().toString());
                    CurrentUserNumber.setText(dataSnapshot.child("user_phone").getValue().toString());
                    member1imageUrl = dataSnapshot.child("user_image").getValue().toString();
                    setImageResource(dataSnapshot.child("user_image").getValue().toString(),member1_image);
                    FetchGroupMemberUserId();
                    CheckForProject();
                    GetPieData();
                    imageClass.setImage1(member1imageUrl);


                } else {
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetUpPieChart(String Task , String task_percent, String remaining_percent){

        goals = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.WHITE);
        colors.add(Color.BLACK);
        goals.add(new PieEntry(Float.parseFloat(task_percent),"Today's\nGoal"));
        goals.add(new PieEntry(Float.parseFloat(remaining_percent),"Left Work"));
        PieDataSet pieDataSet = new PieDataSet(goals,Task);
        pieDataSet.setColors(Color.parseColor("#486FB8"), Color.parseColor("#D6E2F9"));
        pieDataSet.setValueTextColors(colors);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieDataSet.setValueTextSize(16);
        pieDataSet.setSliceSpace(2);
        pieChart.setDrawHoleEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.invalidate();
        pieChart.setRotationAngle(180f);
        pieChart.setMaxAngle(310f);
        pieChart.setHighlightPerTapEnabled(true);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.isDrawRoundedSlicesEnabled();
        pieChart.setCenterTextColor(Color.parseColor("#0049D6"));
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(Task);
        pieChart.setCenterTextRadiusPercent(80);
        pieChart.setTransparentCircleRadius(45);
        pieChart.setHoleRadius(40);
        pieChart.animate();
    }

    private void showDialog(){

        View view = LayoutInflater.from(this).inflate(R.layout.setgoallayout,null);
        goal = view.findViewById(R.id.goal);
        goalPercent = view.findViewById(R.id.goalPercent);
        setGoal = view.findViewById(R.id.setGoal);
        cancelGoal = view.findViewById(R.id.cancelGoal);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alertdialogbackground));


        cancelGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Goal = goal.getText().toString().trim();
                GoalPercentage = goalPercent.getText().toString().trim();

                if(TextUtils.isEmpty(Goal) || TextUtils.isEmpty(GoalPercentage)){
                    Toast.makeText(MainActivity.this, "Please Enter Your Goals For Today", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(Float.parseFloat(GoalPercentage)>100 || Float.parseFloat(GoalPercentage)<=0.00)
                    {
                        Toast.makeText(MainActivity.this, "Please Mention Correctly How Much Do you Want to Work Today.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        RemainingWork = String.valueOf(100.00-Float.parseFloat(GoalPercentage));
                        textGoal.setText(Goal);

                        Date date = new Date();


                        TaskMap = new HashMap<>();
                        TaskMap.put("task",Goal);
                        TaskMap.put("task_percent",GoalPercentage);
                        TaskMap.put("remaining_percent",RemainingWork);
                        TaskMap.put("date",date.getTime());
                        TaskRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
                        TaskRef.child("Task").updateChildren(TaskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                LayoutSetGoal.setVisibility(View.GONE);
                                graph_layout.setVisibility(View.VISIBLE);
                                SetUpPieChart(Goal,GoalPercentage,RemainingWork);
                                alertDialog.dismiss();
                            }
                        });



                    }
                }
            }
        });

    }

    private void FetchGroupMemberDetails(){

        GroupMemberRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName);
        GroupMemberInfoRef = FirebaseDatabase.getInstance().getReference("Users");
        GroupMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(usn.get(0)).exists()){
                    GroupMemberInfoRef.child(snapshot.child(usn.get(0)).child("user_id").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                Member1name.setText(snapshot.child("user_name").getValue().toString());
                                Member1usn.setText(snapshot.child("usn_number").getValue().toString());
                                Member1email.setText(snapshot.child("email").getValue().toString());
                                Member1Phone.setText(snapshot.child("user_phone").getValue().toString());
                                member2imageUrl = snapshot.child("user_image").getValue().toString();
                                setImageResource(snapshot.child("user_image").getValue().toString(),member2_image);
                                String image = "profileimage1";
                                imageClass.setImage2(member2imageUrl);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{

                    Member1Available.setVisibility(View.GONE);
                    Member2Layout.setVisibility(View.VISIBLE);

                }

                if(snapshot.child(usn.get(1)).exists()){

                    GroupMemberInfoRef.child(snapshot.child(usn.get(1)).child("user_id").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                Member2name.setText(snapshot.child("user_name").getValue().toString());
                                Member2usn.setText(snapshot.child("usn_number").getValue().toString());
                                Member2email.setText(snapshot.child("email").getValue().toString());
                                Member2Phone.setText(snapshot.child("user_phone").getValue().toString());
                                member3imageUrl = snapshot.child("user_image").getValue().toString();
                                setImageResource(snapshot.child("user_image").getValue().toString(),member3_image);
                                imageClass.setImage3(member3imageUrl);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{

                    Member2Available.setVisibility(View.GONE);
                    Member3Layout.setVisibility(View.VISIBLE);
                }

                if(snapshot.child(usn.get(2)).exists()){

                    GroupMemberInfoRef.child(snapshot.child(usn.get(2)).child("user_id").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                Member3name.setText(snapshot.child("user_name").getValue().toString());
                                Member3usn.setText(snapshot.child("usn_number").getValue().toString());
                                Member3email.setText(snapshot.child("email").getValue().toString());
                                Member3Phone.setText(snapshot.child("user_phone").getValue().toString());
                                member4imageUrl = snapshot.child("user_image").getValue().toString();
                                setImageResource(snapshot.child("user_image").getValue().toString(),member4_image);
                                imageClass.setImage4(member4imageUrl);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    Member3Available.setVisibility(View.GONE);
                    Member4Layout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchGroupMemberUserId(){
        GroupMemberRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName).child("member_List");
        GroupMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot:snapshot.getChildren()){
                        if(!childSnapshot.getValue().toString().equals(CurrentUserUsn)){
                            usn.add(childSnapshot.getValue().toString());
                        }
                    }
                }
                FetchGroupMemberDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void GetPieData(){

        TaskRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        TaskRef.child("Task").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("task").getValue().toString().equals("task")){

                        loadingDialog.hide();

                    }else{
                        Date date = new Date();
                        String Date = snapshot.child("date").getValue().toString();
                        long diff = date.getTime() - Long.parseLong(Date);
                        long number_of_day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        if(number_of_day<1){
                            goal_for_today = snapshot.child("task").getValue().toString();
                            goal_percent = snapshot.child("task_percent").getValue().toString();
                            remaining_percent = snapshot.child("remaining_percent").getValue().toString();
                            LayoutSetGoal.setVisibility(View.GONE);
                            graph_layout.setVisibility(View.VISIBLE);
                            textGoal.setText(goal_for_today);
                            SetUpPieChart(goal_for_today,goal_percent,remaining_percent);
                            loadingDialog.hide();

                        }else{
                            loadingDialog.hide();
                            ShowNewDialog();
                        }


                    }
                }else{
                    loadingDialog.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowNewDialog(){

        View view = LayoutInflater.from(this).inflate(R.layout.complete_dialog,null);
        Button yes,no,Continue;
        final LinearLayout asklayout,confirmlayout;
        final TextView text1,hurrya;
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);
        Continue = view.findViewById(R.id.Continue);
        asklayout = view.findViewById(R.id.askLayout);
        confirmlayout = view.findViewById(R.id.Confirmlayout);
        hurrya = view.findViewById(R.id.hurrya);
        text1 = view.findViewById(R.id.text1);
        final GifImageView gif = view.findViewById(R.id.gif);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alertdialogbackground));
        alertDialog.show();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asklayout.setVisibility(View.GONE);
                confirmlayout.setVisibility(View.VISIBLE);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asklayout.setVisibility(View.GONE);
                confirmlayout.setVisibility(View.VISIBLE);
                gif.setImageResource(R.drawable.cheer);
                hurrya.setText("Its Okay");
                text1.setText("Don't Feel Low Lets Try Once Again");
            }
        });

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutSetGoal.setVisibility(View.VISIBLE);
                graph_layout.setVisibility(View.GONE);
                TaskMap = new HashMap<>();
                TaskMap.put("task","task");
                TaskMap.put("task_percent","GoalPercentage");
                TaskMap.put("remaining_percent","RemainingWork");
                TaskMap.put("date","date");
                TaskRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
                TaskRef.child("Task").updateChildren(TaskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       alertDialog.dismiss();
                    }
                });

            }
        });


    }

    private void setImageResource(String imageResource,GifImageView imageView){
       if(imageResource.equals("not_provided")){
           imageView.setImageResource(R.drawable.placeholder);
       }else if(imageResource.equals("profileimage1")){
           imageView.setImageResource(R.drawable.profileimage1);
        }else if(imageResource.equals("profileimage3")){
           imageView.setImageResource(R.drawable.profileimage3);
       }else if(imageResource.equals("profileimage4")){
           imageView.setImageResource(R.drawable.profileimage4);
       }else if(imageResource.equals("profileimage5")){
           imageView.setImageResource(R.drawable.profileimage5);
       }else if(imageResource.equals("profileimage6")){
           imageView.setImageResource(R.drawable.profileimage6);
       }else{
           imageView.setImageResource(Integer.parseInt(imageResource));
           imageView.setTag(R.drawable.placeholder);
           int drawable_id = Integer.parseInt(imageView.getTag().toString());
           Log.d("TAG", "setImageResource: "+drawable_id);

           imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   RootRef.child("Users").child(CurrentUserId).child("user_image").setValue(R.drawable.profileimage4).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                           }

                       }
                   });
               }
           });


       }
    }

    private void CheckForProject(){
        RootRef.child("Groups").child(GroupName).child("project").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    manage_card.setVisibility(View.VISIBLE);
                    add_project_card.setVisibility(View.GONE);
                    manage_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ManageProjectActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    manage_card.setVisibility(View.GONE);
                    add_project_card.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void sendWhatsAppMsg(){
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+"&text=" + URLEncoder.encode(" Join us on the Project Management app", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }



}