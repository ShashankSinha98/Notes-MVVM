package com.example.notesmvvm;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/*
*  3. We have our Note Entity and Note Dao, we create another class - NoteDatabase which will connect both of them and
*     create actual instance of database.
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
                    .build();
        }
        return instance;
    }


}
