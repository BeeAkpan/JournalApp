package com.example.interactive_bee.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.setnumd.technologies.journalapp.contracts.Journal;
import com.setnumd.technologies.journalapp.database.AppDatabase;
import com.setnumd.technologies.journalapp.executor.AppExecutors;

public class EntryActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private TextView textViewUser;
    private EditText editTextTitle,editTextContent;
    private String email;
    public static final String DEFAULT_JOURNAL_VALUE = "journal_id";
    private String EXTRA_DIARY_ID = "extra_diary_id";
    private final static int DEFAULT_TASK_ID = -1;


    private ImageButton saveButton;
    private int mTaskId = DEFAULT_TASK_ID;

    private String INSTANCE_TASK_ID = "instance_task_id";
    private AppDatabase database;
    public static String EXTRA_TASK_ID = "extra_task_id";
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        viewConfig();

        editTextTitle.setSelection(editTextTitle.getText().length());
        editTextContent.setSelection(editTextContent.getText().length());
        database = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            (saveButton).setImageResource(R.drawable.update);


            if (mTaskId == DEFAULT_TASK_ID) {

                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                final LiveData<Journal> task = database.diaryDao().loadDiaryById(mTaskId);
                task.observe(this, new Observer<Journal>() {
                    @Override
                    public void onChanged(@Nullable Journal journal) {
                        populateUI(journal);
                        task.removeObserver(this);
                    }
                });
            }} }




    private void viewConfig() {
        editTextTitle = findViewById(R.id.edt_title);
        editTextContent = findViewById(R.id.edt_content);
        saveButton = findViewById(R.id.btn_Save);
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(this);


    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }



    private void populateUI(Journal task) {
        // COMPLETED (7) return if the task is null
        if (task == null) {
            return;
        }

        // COMPLETED (8) use the variable task to populate the UI
        editTextTitle.setText(task.getTitle());
        editTextContent.setText(task.getContent());
    }


    private void insertToDb() {

        String title, content;
        title = editTextTitle.getText().toString();
        content = editTextContent.getText().toString();

        if (!title.matches("") && !content.matches("")) {
            final Journal journal = new Journal( title, content);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    if (mTaskId == DEFAULT_TASK_ID) {
                        // insert new task
                        database.diaryDao().insertDiary(journal);
                    } else {
                        //update task
                        journal.setId(mTaskId);
                        database.diaryDao().updateDiary(journal);
                    }
                    finish();

                }
            });


        } else {
            Toast.makeText(EntryActivity.this, "Fill neccessary fields..",Toast.LENGTH_SHORT).show();

        }
    }
    public void saveToDatabaseButton(View view) {
        insertToDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.relativeLayout){

            InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==event.ACTION_DOWN)
            saveToDatabaseButton(v);

        return false;
    }
}