package com.example.notesmvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;


/*
*  6. First we need to get reference of viewModel in activity to get data from it.
* */
public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter(); // No need to pass data, as we are not storing data in Activity.
        recyclerView.setAdapter(adapter); // By default, list in adapter is empty

        /*
        * We don't instantiate noteViewModel as noteViewModel = new NoteViewModel(), bcz doing so we are creating new instance of viewModel
        * with every new activity being created due to configuration change. Instead we ask android system for viewModel bcz system knows when it
        * has to create new viewModel instance and when to provide already existing instance.  We use ViewModelProvider for getting this instance.
        * */

        // By providing getApplication(), ViewModel knows whose lifecycle it has to scope to. Android system will destroy this viewModel when Activity
        // is finished.
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);

        // noteViewModel.getAllNotes() - return LiveData, can be observed for changes. LiveData is aware of Lifecycle and it will only
        // update activity if it is in foreground. When activity is destroyed, it'll clean the reference to activity which avoid memory
        // leaks and crashes
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // Called whenever data in LiveData is changed. - Update Recycler view
                adapter.setNotes(notes);
            }
        });
    }
}