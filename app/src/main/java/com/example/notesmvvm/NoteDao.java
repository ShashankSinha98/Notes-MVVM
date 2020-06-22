package com.example.notesmvvm;


/*
*  2. DAO (Data Access Object) - define all database operation that we want to make on Note Entity / Table.
*   DAO can be abstract class or interface - Only defining, no method body - Room will automatically generate necessary code,
*   we just annotate the methods of what they are supposed to do.
*
*   General rule - 1 DAO per Entity.
* */

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao // Signifying this interface is a dao
public interface NoteDao {

    @Insert   // Signifying this method is used for Insertion
    void Insert(Note note); // we can have 1 note, multiple notes, list of notes or varargs of notes(Note ...note) in argument,
                            // -  we only want to deal with one note here, so only one argument

    @Update   // Signifying this method is used for Updating data
    void Update(Note note);

    @Delete   // Signifying this method is used for Deletion
    void Delete(Note note);

    /*
    * Room doesn't have annotation for all database operation.
    * So, we use @Query("") annotation where we pass Database Query as string in it.
    * */


    @Query("DELETE FROM note_table") // Custom query for deleting all rows from table - any typo will lead to compile time error
    void DeleteAllNotes();            // which was not case in SQLite OpenHelper class, we can still open app - leading to crash



    /*
    * Room can also return LiveData which helps to observe the obj inside it (List<Note>)
    * - If there is any change in note_table, List<Note> will automatically be updated and activity will be notified.
    * - Room takes care of updating LiveData Object
    * */

    @Query("SELECT * FROM note_table ORDER BY priority DESC") // Custom query for getting all rows from table in desc order of their priority
    LiveData<List<Note>> getAllNotes(); // Room also check if columns of note_table fit to Note java obj during compile time, if table has column
                            //   not present in Note class, we get compile time error.

}
