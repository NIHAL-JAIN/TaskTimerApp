package com.nihal.tasktimerapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nihal.tasktimerapp.databinding.FragmentFirstBinding;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */

public class FirstFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "FirstFragment";

    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter mAdapter; // add adapter reference


    public FirstFragment(){
        Log.d(TAG,"FirstFragment: start");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG,"onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);
       // getLoaderManager().initLoader(LOADER_ID,null,this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

   // private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_first,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding = FragmentFirstBinding.inflate(inflater, container, false);

        mAdapter = new CursorRecyclerViewAdapter(null,
                (CursorRecyclerViewAdapter.OnTaskClickListener)getActivity());
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG,"onCreateView: returning");
        return view;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args) {
        Log.d(TAG,"onCreateLoader: start with id " + id);
        String[] projection = {TaskContract.Columns._ID,TaskContract.Columns.TASKS_NAME,
                                TaskContract.Columns.TASKS_DESCRIPTION,TaskContract.Columns.TASKS_SORTORDER};
        // <order by> Task.SortOrder, Task.Name COLLATE NOCASE
        String sortOrder = TaskContract.Columns.TASKS_SORTORDER + "," + TaskContract.Columns.TASKS_NAME;
        switch (id){
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TaskContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id" + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"Entering onLoadFinished:");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished count is" + count);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG,"onLoaderReset: starts");
        mAdapter.swapCursor(null);


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

}