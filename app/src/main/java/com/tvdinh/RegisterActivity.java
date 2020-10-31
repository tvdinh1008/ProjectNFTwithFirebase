package com.tvdinh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText username,email,password, confirmPassword;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirm_password);
        btn_register=findViewById(R.id.btn_register);

        auth=FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=username.getText().toString();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();
                String txt_confirm_password=confirmPassword.getText().toString();

                if(TextUtils.isEmpty(txt_username)||TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)||TextUtils.isEmpty(txt_confirm_password))
                {
                    Toast.makeText(RegisterActivity.this,"All fileds are requirea",Toast.LENGTH_LONG).show();
                }
                else if(txt_password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this,"password mus be at least 6 characters",Toast.LENGTH_LONG).show();
                } else if(! txt_password.equals(txt_confirm_password)){
                    Toast.makeText(RegisterActivity.this,"Confirm Password fail!",Toast.LENGTH_LONG).show();
                } else{
                    register(txt_username,txt_email,txt_password);
                }

            }
        });
    }
    private void register(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=auth.getCurrentUser();

                    String userid=firebaseUser.getUid();
                    //Users là tên database(kiểu json trong firebase) lưu 3 trường là id, username,email, imageURL
                    reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username);
                    hashMap.put("email",email);
                    hashMap.put("imageURL","DEFAULT");

                    //Sau khi tạo xong nó sẽ tự động đăng nhập tức vào main luôn nhé
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,"Create account is success!",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this,"You can't register woth this email or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}