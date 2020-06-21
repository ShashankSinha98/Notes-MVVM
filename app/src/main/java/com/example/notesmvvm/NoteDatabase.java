package com.example.notesmvvm;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/*
*  3. We have our Note Entity and Note Dao, we create another class - NoteDatabase which will connect both of them and
*     create actual instance of database. This is called ROOM DATABASE containing both Entity and its DAO.
*
* */

/*
* @Database()- Room Annotation for creating Database. We can mention all entity/table this database will contain inside {}.
* Version no is updated whenever we make structural change in table/database - relevant in production mode
 * */

/*
* This class is going to be a Singleton class - can't create multiple instance of NoteDatabase class - use same instance everywhere
* */

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    // Used to access database operations(INSERT,DELETE,UPDATE) defined in NoteDao - Code automatically added by Room
    public abstract NoteDao noteDao();

    /*
    *  synchronized - means that only one thread can access it at one time, so that we don't create multiple instance of class when two diff
    *  thread try to access it at same time.(can happen in multi-thread android environment)
    *
    *  fallbackToDestructiveMigration - When we increment Version No of DB, we need to tell Room how to migrate to new schema. If we don't tell it,
    *  and try to increase version no, our app will crash due to "illegalStateException". fallbackToDestructiveMigration prevents it by creating
    *  a deleting all old database and tables and creating a new database and table from scratch.
    * */
    public static synchronized NoteDatabase getInstance(Context context){

        // only create instance when we don't have one
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),  // Notice- NoteDatabase instance = new NoteDatabase(...) is not used
                    NoteDatabase.class,"note_database")               // as this class is abstract, so can't create its object.
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)  // Attaching Callback to our database.
                    .build();
        }
        return instance;
    }


    /*
    * HOW TO POPULATE DATA IN BEGINNING BEFORE MANUAL INSERTION
    *
    * 1. In SQLite OpenHelper class we did it in onCreate method - bcz this method is called the first time we create the DB, but not anytime after that.
    * 2. We use RoomDatabase.Callback to get into this onCreate() method. (ctrl+O) to get onCreate method in RoomDatabase.Callback(){___};
    *  -> onCreate() = only called first time when database is created.
    *  -> onOpen() = called each-time when database is opened.
    *  -> onDestructiveMigration() = Called after the database was destructively migrated.
    *
    *  we need to perform insert operation in onCreate but not in Main Thread, in background Thread. So, we'll use Async Task
    *
    * At end, Attach Callback to our database using addCallback() in  Room.databaseBuilder()
    * */

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // instance - NoteDatabase
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{

        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase noteDatabase){ // Since, we don't have noteDao member variable in this class, we pass NoteDatabase
            noteDao = noteDatabase.noteDao(); // onCreate is called after DB is created, so we can access noteDao from NoteDatabase without any error
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // Insert Notes
            noteDao.Insert(new Note("Title 1","Description 1",1));
            noteDao.Insert(new Note("Title 2","Description 2",2));
            noteDao.Insert(new Note("Title 3","Description 3",3));

            return null;
        }
    }

}
