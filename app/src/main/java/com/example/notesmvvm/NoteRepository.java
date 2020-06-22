package com.example.notesmvvm;

/*
*  4. Repository class - not a part of Android Architecture Components but Provides an extra abstraction between different data sources and rest of app.
*   - In our app, we have only one data source - SQLite DB.
*   - In case we want to fetch data from server and store in locally in SQLite DB, we want to intermediate btw both data source.
*   - This class will take care of it and decide when to fetch data from where. It'll provide data to ViewModel and thus ViewModel will not worry
*       from where data is coming, it just calls methods of repository directly for data.
*
*  Repository uses DAO methods (from NoteDao) to retrieve all the entries from Note table as a list wrapped into Live Data. The same Live Data is given
*  to ViewModel by repository and saves it for Main Activity. Main Activity doesn't store data itself but it observes LiveData
*  stored in ViewModel and automatically receives updated list of notes whenever data in corresponding SQLite table changes.
*
*
* */

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    // Application is a subclass of Context - used as context to create database instance
    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        /*
        *  noteDao is a abstract class in NoteDatabase. Normally, we can't call abstract method bcz they don't have body.
        *  Since we built NoteDatabase instance using Room.databaseBuilder()- Room auto generates code for NoteDao (Room Subclasses our abstract class)
        * */
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }


    /*
    * These Methods- (insert/update/delete/deleteAllNNotes) are the API that repository exposes to outside.
    * So ViewModel only knows these methods but don't know exactly what is happening under the hood(Java  Abstraction!!!)
    * */


    public void insert(Note note){
        // create instance of InsertNoteAsyncTask and pass note to it. Need to perform it on background thread.
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    /*
    * Room will automatically execute database operations that returns LiveData on Background Thread.(No need to taken care by us)
    * But on other DB operations (insert/update/delete/deleteAllNNotes), we have to do it our self, bcz Room doesn't allow DB operations
    * on Main Thread (Since it could freeze the app). we'll use Async Task for performing these operations on background thread.
    * */
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    /*
    * Three things needed by AsyncTask<_,_,_> =
    * 1. What you need to pass
    * 2. Progress updates - if not needed = Void
    * 3. Return result - if not needing anything back = Void
    *
    * AsyncTask is declared static so that it doesn't have a reference to repository, else it could cause Memory Leaks.
    */
    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao; // InsertNoteAsyncTask class is static, so can't access NoteDao of NoteRepository class.

        // Constructor
        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.Insert(notes[0]); // notes is varargs and we want to insert only one note - like an array list having only one item.
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao; // InsertNoteAsyncTask class is static, so can't access NoteDao of NoteRepository class.

        // Constructor
        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.Update(notes[0]); // notes is varargs and we want to update only one note - like an array list having only one item.
            return null;
        }
    }


    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void>{

        private NoteDao noteDao; // InsertNoteAsyncTask class is static, so can't access NoteDao of NoteRepository class.

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.Delete(notes[0]); // notes is varargs and we want to delete only one note - like an array list having only one item.
            return null;
        }
    }

    // No need to pass any Note to it. so - Void as 1st parameter.
    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void,Void,Void>{

        private NoteDao noteDao; // InsertNoteAsyncTask class is static, so can't access NoteDao of NoteRepository class.

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.DeleteAllNotes();
            return null;
        }
    }

}
