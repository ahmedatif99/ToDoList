package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private EditText editText;
    private Button submit;
    private TextView logout;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private SearchView searchView;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        recyclerView = findViewById(R.id.recyclerView);
        Button createList = findViewById(R.id.btn_add);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                } else {
                }
            }
        };
        /*Data SnapShot*/
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Item items = snapshot.getValue(Item.class);
                            itemList.add(items);
                        }
                        recyclerViewAdapter = new RecyclerViewAdapter(ListActivity.this, itemList);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
        /*Data SnapShot*/

        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog();
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ListActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ListActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        searchView = findViewById(R.id.search_bar);
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
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void popUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.pop_up, null);
        editText = view.findViewById(R.id.edit_text);
        submit = view.findViewById(R.id.submit_list);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editText.getText().toString().isEmpty()) {

                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();


                    Item item = new Item();
                    item.setListName(editText.getText().toString());

                    String itemId = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").push().getKey();
                    item.setId(itemId);
                    itemList.add(item);
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(itemId).setValue(item);
                    Snackbar.make(v, "Added Successfully *__^", Snackbar.LENGTH_SHORT).show();

                    recyclerViewAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();

                } else {
                    Snackbar.make(v, "Pleas Enter List name ...", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    Item deletedItem = null;
    Item newItem = null;


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(itemList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    newItem = itemList.get(position);

                    builder = new AlertDialog.Builder(ListActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.pop_up, null);

                    TextView title = view.findViewById(R.id.typing_name);

                    editText = view.findViewById(R.id.edit_text);
                    submit = view.findViewById(R.id.submit_list);

                    title.setText("Edit Item : ");
                    editText.setText(newItem.getListName());
                    submit.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();


                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setListName(editText.getText().toString());
                            newItem.setId(newItem.getId());
                            String uid = firebaseUser.getUid();
                            if (!editText.getText().toString().isEmpty()) {
                                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("items").child(newItem.getId()).child("listName").setValue(editText.getText().toString());
                                recyclerViewAdapter.notifyItemChanged(position);
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

}