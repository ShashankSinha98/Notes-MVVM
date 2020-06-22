package com.example.notesmvvm;

/*
*  5. ViewModel is a part of android architecture components library. It's job is to store and process data for user interface and communicate
*     with the model. It request data from Repository so that Activity can draw data on screen and it forwards user interaction from UI back to
*     repository.
*
*     It is recommended to put UI related data into viewModel instead of UI Controller(UI - Activity/Fragment), bcz ViewModel survive configuration
*     changes.(Screen Rotation, Language Change- Activity is destroyed and recreated, state of member variables is lost). View Model keeps that
*     data and new activity gets same ViewModel instance and can immediately access same variable.
*
*     ViewModel is only removed from memory when lifecycle of activity/fragment is over(Activity is finished or fragment is detached).
*
* */


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    /*
    *   AndroidViewModel is a subclass of ViewModel. In AndroidViewModel, we pass Application in constructor which is used whenever application
    *   context is needed. We should never store context of activity or view that references an activity in viewModel bcz viewModel is designed to
    *   outlive an activity after it is destroyed and if we hold references to already destroyed activity, we can have memory leak. We need to pass
    *   context to repository in order to instantiate the database instance, so we extend AndroidViewModel bcz then we pass application context which
    *   can be passed further to database.
    * */

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;



    public NoteViewModel(@NonNull Application application) {
        super(application);

        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    /*
    * Activity has only reference to ViewModel not to repository, so we need to have wrapper methods for database operation methods of our
    * repository.
    * */

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void  delete(Note note){
        repository.delete(note);
    }

    public  void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
