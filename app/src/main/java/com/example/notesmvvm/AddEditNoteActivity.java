package com.example.notesmvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;

public class AddEditNoteActivity extends AppCompatActivity {

    // psfs
    public static final String EXTRA_ID  = "com.example.notesmvvm.EXTRA_ID";
    public static final String EXTRA_TITLE  = "com.example.notesmvvm.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION  = "com.example.notesmvvm.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.notesmvvm.EXTRA_PRIORITY";

    private TextInputLayout textInputLayoutTitle;
    private TextInputLayout textInputLayoutDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        textInputLayoutTitle = findViewById(R.id.text_input_layout_title);
        textInputLayoutDescription = findViewById(R.id.text_input_layout_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            // Update
            setTitle("Edit Note");
            textInputLayoutTitle.getEditText().setText(intent.getStringExtra(EXTRA_TITLE));
            textInputLayoutDescription.getEditText().setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY,1));
        } else {
            // Add
            setTitle("Add Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_note:
                SaveNote();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveNote() {

        String title = textInputLayoutTitle.getEditText().getText().toString();
        String description = textInputLayoutDescription.getEditText().getText().toString();
        int priority  = numberPickerPriority.getValue();

        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toasty.error(this,"Title/Description can't be Empty !",Toasty.LENGTH_SHORT).show();
            return;
        }

        // Send data back to Main Activity via Intent
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE,title);
        data.putExtra(EXTRA_DESCRIPTION,description);
        data.putExtra(EXTRA_PRIORITY,priority);

        int id = getIntent().getIntExtra(EXTRA_ID,-1); // No entry in table will have id = -1
        if(id != -1){
            data.putExtra(EXTRA_ID,id);
        }

        // RESULT_OK - integer constant indicating that data is given back by this activity
        setResult(RESULT_OK,data);
        finish();




    }
}