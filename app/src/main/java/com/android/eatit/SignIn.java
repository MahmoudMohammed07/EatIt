package com.android.eatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.eatit.Common.Common;
import com.android.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText editPhone, editPassword;
    Button btnSignIn;
    CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editPassword = (MaterialEditText) findViewById(R.id.editPassword);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        cbRemember = (CheckBox) findViewById(R.id.cb_remember);

        Paper.init(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tableUser = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    if (cbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, editPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, editPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();

                    tableUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(editPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);
                                user.setPhone(editPhone.getText().toString());
                                if (user.getPassword().equals(editPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    Toast.makeText(SignIn.this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, R.string.user_doesnt_exist, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
