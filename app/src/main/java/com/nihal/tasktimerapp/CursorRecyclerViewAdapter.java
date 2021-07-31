package com.nihal.tasktimerapp;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by nihal jain on 26/07/2021
 */
class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final  String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnTaskClickListener mListener;


    interface OnTaskClickListener{
        void onEditClick(Task task);
        void onDeleteClick(Task task);

    }

    public CursorRecyclerViewAdapter(Cursor mCursor, OnTaskClickListener listener) {
        Log.d(TAG,"CursorRecyclerViewAdapt: constructor called");
        this.mCursor = mCursor;
        mListener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG,"CursorRecyclerViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
//        Log.d(TAG,"onBindViewHolder: starts");

        if ((mCursor == null) || (mCursor.getCount()==0)){
            Log.d(TAG,"onBindViewHolder : providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }else{
            if(!mCursor.moveToPosition(position)){
                throw new IllegalArgumentException("Couldn't move cursor to position" + position);
            }
            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TaskContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASKS_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TaskContract.Columns.TASKS_SORTORDER)));

            holder.name.setText(task.getmName());
            holder.description.setText(task.getmDescription());
            holder.editButton.setVisibility(View.VISIBLE); //Todo add onClick listener
            holder.deleteButton.setVisibility(View.VISIBLE); //Todo add onClick listener

           View.OnClickListener buttonListener = new View.OnClickListener() {
               @Override
               public void onClick(View view) {
//                   Log.d(TAG,"onClick: starts");
                   switch (view.getId()){
                       case R.id.tli_edit:
                           if (mListener!=null) {
                               mListener.onEditClick(task);
                           }
                           break;
                       case R.id.tli_delete:
                           if (mListener!=null) {
                               mListener.onDeleteClick(task);
                           }
                           break;
                       default:
                           Log.d(TAG,"onClick: found unexpected button id");
                   }
//                   Log.d(TAG,"onClick: button with id " + view.getId() + "clicked");
//                   Log.d(TAG,"onClick: task name is " + task.getmName());
               }
           };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);

        }

    }

    @Override
    public int getItemCount() {
//        Log.d(TAG,"getItemCount: starts");
        if((mCursor == null) || (mCursor.getCount()== 0)) {
            return 1; //fib, because we populate a single ViewHolder with instructions

        }else{
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returner old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Return the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */

    Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            //notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            //notify the observers about the lack of data set
            notifyItemRangeRemoved(0,getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(View itemView) {
            super(itemView);
//            Log.d(TAG, "TaskViewHolder : starts");

            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
