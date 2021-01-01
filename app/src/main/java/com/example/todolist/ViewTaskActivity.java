package com.example.todolist;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewTaskActivity extends AppCompatActivity {
    private EditText dets_description;
    private TextView textview_edit, back_viewTask, timeAdd, deleteTask, save;
    private String listId, taskId;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private ViewTask viewTask;
    private List<ViewTask> viewTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        viewTask = new ViewTask();
        timeAdd = findViewById(R.id.dateAdded);
        TextView detsTaskName = findViewById(R.id.dets_taskName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String taskName = bundle.getString("taskName");
            String time = bundle.getString("timeAdd");
            timeAdd.setText(time);

            listId = bundle.getString("listId");
            taskId = bundle.getString("taskId");

            detsTaskName.setText(taskName);
        }

        /*Data SnapShot*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items")
                .child(listId).child("Tasks").child(taskId).child("Description").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            dets_description.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
        /*Data SnapShot*/
        dets_description = findViewById(R.id.dets_description);
        textview_edit = findViewById(R.id.textview_edit);
        back_viewTask = findViewById(R.id.back_viewTask);


        back_viewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        textview_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dets_description.setEnabled(true);
                dets_description.setFocusable(true);
                Toast.makeText(ViewTaskActivity.this, "Now You can edit Description", Toast.LENGTH_SHORT).show();
            }
        });


        save = findViewById(R.id.saveDesc);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = dets_description.getText().toString().trim();
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                String uid = user.getUid();
                viewTask.setDesc(desc);
                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items")
                        .child(listId).child("Tasks").child(taskId).child("Description")
                        .child("desc").setValue(viewTask.getDesc().toString());
                finish();
            }
        });

        deleteTask = findViewById(R.id.deleteTask);
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = user.getUid();
                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(listId).child("Tasks").child(taskId).removeValue();
                finish();
            }
        });
    }
}