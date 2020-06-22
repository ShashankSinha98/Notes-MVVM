package com.example.notesmvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ListAdapter<Note,NoteAdapter.NoteHolder> {


    // Imp to set it to New ArrayList<>(), else it will be null before we get first LiveDataUpdate and we don't want
    // to pass null list to adapter.
    //private List<Note> notes = new ArrayList<>(); // 10. Instead of keeping list in noteAdapter, we'll pass the list to superclass to ListAdapter
    private OnItemClickListener listener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {

        // Do the comparison Logic

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // Two Note item are same if there ID are same - uniquely identify each entry
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // Same content means title, description and priority are same.
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        //Note currentNote = notes.get(position); - used in RecyclerView.Adapter
        Note currentNote = getItem(position); //accessing list from superclass

        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    // Done by List Adapter now
   // public void setNotes(List<Note> notes) {
     //   this.notes = notes;

        // 10. notifyDataSetChanged() tells the adapter that the whole list on scree is invalid, so it has to drop it and redraw it from scratch.
        // This is inefficient bcz even if we make change in single item, we have to redraw the list again.
        /* To get rid of it, Recycler view provide these methods which enable the default animation on item changed-
        * * @see #notifyItemChanged(int)
         * @see #notifyItemInserted(int)
         * @see #notifyItemRemoved(int)
         * @see #notifyItemRangeChanged(int, int)
         * @see #notifyItemRangeInserted(int, int)
         * @see #notifyItemRangeRemoved(int, int)
         *
         * But they accepts int position of item going changes that we need to pass. But whenever any change occur in our list,
         * Observable returns whole list of notes without ay position info of which item has changed. So, we need a way to compare
         * old list to new list and calculate at which position changes happens.
         *
         * This is done by DiffUtil, which helps to compare two lists and give positional info to our adapter. We just need to provide
         * how we want to compare two items.
         *
         * We use ListAdapter (subclass of RecyclerView) to implement DiffUtil. It does List comparison logic on background thread, thus not freezing app
         * while comparing big lists.
        * */

    //    notifyDataSetChanged();
    //}


    public Note getNoteAt(int position) {
        return getItem(position);
    }

    // Done by List Adapter now
    /*@Override
    public int getItemCount() {
        return notes.size();
    }*/

    class NoteHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewDescription, textViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            // we want to catch click of item in RV, and the call and pass note obj in OnItemClick
            // itemView is the whole cardView, so we set a onClickListener on it.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Implement our listener and pass Note obj to it
                    int position = getAdapterPosition();

                    // Check if listener!=null bcz it is not guarantee that we'll call setOnItemClickListener
                    // RecyclerView.NO_POSITION - Constant for -1, so that we don't click item at Invalid position (safety measure)
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        //listener.onItemClick(notes.get(position)); - used in RecyclerView.Adapter
                        listener.onItemClick(getItem(position)); // getting data from superclass
                    }
                }
            });
        }
    }


    // We want to get the onClick of Recycler view items to Main Activity for processing, So we use Interface for it.
    public interface OnItemClickListener {

        // In interface, we only declare method, not give implementation
        // Whatever class later implements this interface, also has to implement this method
        void onItemClick(Note note);
    }

    // To call methods from adapter onto OnItemClickListener, we need a reference of OnItemClickListener
    // so we create a public method that ca be accessed from MainActivity
    // We'll use listener in arg to call OnItemClickListener, and thus forward ourNote obj to whatever class implements this interface(Main Activity)
    // to do it, we'll save listener to member var in adapter class
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
