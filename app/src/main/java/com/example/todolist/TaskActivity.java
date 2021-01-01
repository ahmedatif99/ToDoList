package com.example.todolist;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private RecyclerViewAdapterTask recyclerViewAdapterTask;
    private RecyclerView recyclerViewTask;

    private EditText itemTaskName;
    private List<Task> taskList;
    private Button btn_saveTask;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String itemId;
    private TextView deleteList;
    private SearchView searchView;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        TextView detsListName = findViewById(R.id.dets_listName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String listName = bundle.getString("ListName");
            itemId = bundle.getString("ItemId");

            detsListName.setText(listName + " List");
        }

        recyclerViewTask = findViewById(R.id.recyclerViewTask);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();



        /*Data SnapShot*/
         mAuth = FirebaseAuth.getInstance();
         user = mAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        taskList.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Task task = snapshot.getValue(Task.class);
                            taskList.add(task);
                        }
                        recyclerViewAdapterTask = new RecyclerViewAdapterTask(TaskActivity.this, taskList);
                        recyclerViewTask.setAdapter(recyclerViewAdapterTask);
                        recyclerViewAdapterTask.notifyItemInserted(0);
                        recyclerViewAdapterTask.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
        /*Data SnapShot*/


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTask);

        Button btn_createTask = findViewById(R.id.btn_createTask);
        btn_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });

        TextView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteList = findViewById(R.id.textview_deleteList);
        deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = user.getUid();
                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).removeValue();
                finish();
            }
        });

        searchView = findViewById(R.id.tasks_search);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search" + "</font>"));
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView)searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapterTask.getFilter().filter(newText);
                return false;
            }
        });
    }



    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.pop_up, null);
        itemTaskName = view.findViewById(R.id.edit_text);
        btn_saveTask = view.findViewById(R.id.submit_list);

        TextView title = view.findViewById(R.id.typing_name);
        title.setText("Enter Task Name : ");
        itemTaskName.setHint("Task Name");

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemTaskName.getText().toString().isEmpty()) {
                     mAuth = FirebaseAuth.getInstance();
                     user = mAuth.getCurrentUser();
                    String uid = user.getUid();

                    Task task = new Task();
                    task.setTaskName(itemTaskName.getText().toString());
                    task.setIsChecked(false);
                    task.setListId(itemId);
                    DateFormat dateFormat = DateFormat.getDateTimeInstance();
                    String date = dateFormat.format(new Date().getTime());
                    task.setTimeAdd(date);
                    String tasksId = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").push().getKey();

                    taskList.add(task);
                    task.setTaskId(tasksId);
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").child(tasksId).setValue(task);
                    Snackbar.make(v, "Added Successfully *__^", Snackbar.LENGTH_SHORT).show();

                    recyclerViewAdapterTask.notifyDataSetChanged();
                    alertDialog.dismiss();
//                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void saveItem(View v) {
//
//        String newItemNOT = itemTaskName.getText().toString().trim();
//
//        Task task = new Task(newItemNOT, false);
//        taskList.add(0, task);
//        recyclerViewAdapterTask.notifyItemInserted(0);
//        recyclerViewTask.smoothScrollToPosition(0);
//
//        alertDialog.dismiss();
//    }

    public void onCheckboxClicked(View view) {
    }

    Task deletedItem = null;
    Task newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(taskList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    newItem = taskList.get(position);

                    builder = new AlertDialog.Builder(TaskActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.pop_up, null);

                    TextView title = view.findViewById(R.id.typing_name);

                    itemTaskName = view.findViewById(R.id.edit_text);
                    btn_saveTask = view.findViewById(R.id.submit_list);

                    title.setText("Edit Tasks : ");
                    itemTaskName.setText(newItem.getTaskName());
                    btn_saveTask.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();

                    btn_saveTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setTaskName(itemTaskName.getText().toString());
                            newItem.setTaskId(newItem.getTaskId());
                            String uid = user.getUid();
                            if (!itemTaskName.getText().toString().isEmpty()) {
                                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).child("Tasks").child(newItem.getTaskId()).child("taskName").setValue(itemTaskName.getText().toString());
                                recyclerViewAdapterTask.notifyItemChanged(position);
                                alertDialog.dismiss();
                            } else {
                                Snackbar.make(view, "Field Empty", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };
}