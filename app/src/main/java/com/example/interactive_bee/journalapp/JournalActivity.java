package com.example.interactive_bee.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.setnumd.technologies.journalapp.adapter.JournalAdapter;
import com.setnumd.technologies.journalapp.contracts.Journal;
import com.setnumd.technologies.journalapp.database.AppDatabase;
import com.setnumd.technologies.journalapp.executor.AppExecutors;
import com.setnumd.technologies.journalapp.utils.DividerItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ArrayList<Journal> journalArrayList;
    private JournalAdapter journalAdapter;
    private RecyclerView recyclerView;
    private AppDatabase mdb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        recyclerView = findViewById(R.id.recyclerview);

        firebaseAuth = FirebaseAuth.getInstance();

        journalArrayList = new ArrayList<>();



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        journalAdapter = new JournalAdapter(journalArrayList,this);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(journalAdapter);



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(JournalActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        };


  /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(JournalActivity.this, EntryActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mdb = AppDatabase.getInstance(getApplicationContext());
        retrieveJournal();
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    private void retrieveJournal() {
        final LiveData<List<Journal>> task = mdb.diaryDao().loadAllDiaries();
        task.observe(this, new Observer<List<Journal>>() {
            @Override
            public void onChanged(@Nullable List<Journal> journals) {

                journalAdapter.setData(journals);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemClickListener(int itemId) {


        Intent intent = new Intent(JournalActivity.this, EntryActivity.class);
        intent.putExtra(EntryActivity.EXTRA_TASK_ID,itemId);
        startActivity(intent);
    }
}
