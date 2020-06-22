package com.example.notesmvvm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.dmoral.toasty.Toasty;


/*
 *  6. First we need to get reference of viewModel in activity to get data from it.
 * */
public class MainActivity extends AppCompatActivity {

    //psfi
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton buttonAddNNote = findViewById(R.id.button_add_note);
        buttonAddNNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);

                // To get I/p back to MainActivity and add it to DB. MainActivity has reference to ViewModel.
                startActivityForResult(intent, ADD_NOTE_REQUEST); // request code is needed bcz startActivityForResult can be called multiple
                // times from MainActivity from other parts. So to distinguish them, we need it.
            }
        });


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
               // adapter.setNotes(notes);
                adapter.submitList(notes); // method of ListAdapter
            }
        });


        // Swipe To Delete Functionality, ItemTouchHelper - make our Recycler View Swipable
        // ItemTouchHelper.SimpleCallback(__,__) - need to know direction of movement,
        // 1st arg is for drag and drop, Since we are not implementing it, thus - 0
        // 2nd arg is for swiping, we are adding both left and right.

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // onMove is drag and drop functionality
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                // viewHolder.getAdapterPosition() - tells which item you swiped, position
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toasty.success(MainActivity.this, "Note Deleted !", Toasty.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);


        // Implementing Interface
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Here note obj is the one which is being passed from ViewHolder
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                // we actually don't need ID in AddEditNoteActivity, bcz we do update operation in MainActivity only, but we still have to pass
                // it there to get back from AddEditNoteActivity in onActivityResult and do update Operation in MainActivity.
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking if we get RESULT_OK from ADD_NOTE_REQUEST Activity, then it contains data
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toasty.success(this, "Note Saved", Toasty.LENGTH_LONG).show();

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {

            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1);

            if(id == -1){
                Toasty.error(this,"Note can't be updated !",Toasty.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title,description,priority);
            note.setId(id); // So that we can perform update operation on same note

            noteViewModel.update(note);
            Toasty.success(this,"Note updated",Toasty.LENGTH_SHORT).show();




        } else {
            // If resultCode == RESULT_CANCEL, user pressed back in AddNoteActivity
            Toasty.warning(this, "Note not saved !", Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toasty.success(MainActivity.this, "All notes deleted", Toasty.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}