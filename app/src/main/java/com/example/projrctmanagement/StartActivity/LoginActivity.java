package com.example.projrctmanagement.StartActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projrctmanagement.HelperClasses.LoadingDialog;
import com.example.projrctmanagement.MainActivity;
import com.example.projrctmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail,password;
    TextView loginRegisterBtn;
    MaterialButton loginBtn;
    FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;

    // Handler
    Handler handler = new Handler();
    Runnable runnable;
    int delay =50;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        password = findViewById(R.id.password);
        loginRegisterBtn = findViewById(R.id.loginRegisterBtn);
        loginBtn = findViewById(R.id.loginBtn);

        mAuth=FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = userEmail.getText().toString();
                String mPassword = password.getText().toString();

                if(TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)){
                    Toast.makeText(LoginActivity.this, "Please Enter the Right Credential", Toast.LENGTH_SHORT).show();
                }else{
                    loadingDialog.show();
                    loginuser(mEmail,mPassword);
                }

            }
        });

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

            }
        });




    }

    private void loginuser(String mEmail, String mPassword) {

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(  new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentuser = mAuth.getCurrentUser();
                            loadingDialog.hide();
                            Intent newintent=new Intent(LoginActivity.this, MainActivity.class);
                            newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(newintent);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
}