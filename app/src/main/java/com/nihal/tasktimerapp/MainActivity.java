package com.nihal.tasktimerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nihal.tasktimerapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
                                                                AddEditActivityFragment.OnSaveClicked,
                                                                AppDialog.DialogEvents{
    private static final String TAG = "MainActivity";

    //Whether or not the activity is in 2-pane mode
    //i.e. running in landscape on a tablet
    private boolean mTwoPane = false;
   
    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;
    private static final int DIALOG_ID_CANCEL_EDIT_UP = 3;

    private AlertDialog mDialog = null;    // module scope because we need to dismiss it in onStop
                                           // e.g. when orientation changes to avoid memory leaks.

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twopane is" + mTwoPane);

        FragmentManager fragmentManager = getSupportFragmentManager();
        //if the AddEditActivity fragment exists, we're editing
        Boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;
        Log.d(TAG, "onCreate: editing is " + editing);

        //we need references to the containers, so we can show or hide them as necessary.
        //No need to cast them, as we're only calling a method that's available for all views.
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.nav_host_fragment_content_main);

        if(mTwoPane){
            Log.d(TAG, "onCreate: twoPane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        }else if(editing){
            Log.d(TAG, "onCreate: single pane, editing");
            //hide the left hand fragment, to make room for editing
            mainFragment.setVisibility(View.GONE);
        }else {
            Log.d(TAG, "onCreate: single pane, not editing");
            //Show left hand fragment
            mainFragment.setVisibility(View.VISIBLE);
            //Hide the editing frame
            addEditLayout.setVisibility(View.GONE);
        }
//        String[] projection = {TaskContract.Columns._ID,
//                               TaskContract.Columns.TASKS_NAME,
//                               TaskContract.Columns.TASKS_DESCRIPTION,
//                               TaskContract.Columns.TASKS_SORTORDER};
//        ContentResolver contentResolver = getContentResolver();
//
//        ContentValues values = new ContentValues();
////        values.put(TaskContract.Columns.TASKS_SORTORDER,"99");
////        values.put(TaskContract.Columns.TASKS_DESCRIPTION,"Completed");
////        String selection = TaskContract.Columns.TASKS_SORTORDER+ " = " +2;
//
////        int count = contentResolver.update(TaskContract.CONTENT_URI,values,selection,null);
////        Log.d(TAG,"onCreate: " + count + "record(s) updated");
//
////        values.put(TaskContract.Columns.TASKS_DESCRIPTION,"for deletion");
////        String selection = TaskContract.Columns.TASKS_SORTORDER+ " = ? ";
////        String[] args = {"99"};
//
////        int count = contentResolver.delete(TaskContract.buildTaskUri(3),null,null);
////        Log.d(TAG,"onCreate: " + count + "record(s) deleted");
//
//        String selection = TaskContract.Columns.TASKS_DESCRIPTION + " = ? ";
//        String[] args = {"For deletion"};
//        int count = contentResolver.delete(TaskContract.CONTENT_URI,selection,args);
//        Log.d(TAG,"onCreate" + count + "record(s) deleted");
//
////        values.put(TaskContract.Columns.TASKS_NAME,"Content Provider");
////        values.put(TaskContract.Columns.TASKS_DESCRIPTION,"Record content provider video");
////        int count = contentResolver.update(TaskContract.buildTaskUri(4),values,null,null);
////        Log.d(TAG,"onCreate" +count + "record(s) updated");
//
////        values.put(TaskContract.Columns.TASKS_NAME, "New Task 1");
////        values.put(TaskContract.Columns.TASKS_DESCRIPTION, "Description 1");
////        values.put(TaskContract.Columns.TASKS_SORTORDER, 2);
////        Uri uri = contentResolver.insert(TaskContract.CONTENT_URI,values);
//
//        Cursor cursor = contentResolver.query(TaskContract.CONTENT_URI,
////        Cursor cursor = contentResolver.query(TaskContract.buildTaskUri(2),
//                projection,
//                null,
//                null,
//                TaskContract.Columns.TASKS_SORTORDER);
//
//        if(cursor!=null) {
//            Log.d(TAG,"onCreate: number of rows: " + cursor.getCount());
//            while (cursor.moveToNext()){
//                for(int i = 0; i<cursor.getColumnCount();i++){
//                    Log.d(TAG,"onCreate:" + cursor.getColumnName(i) + ":" + cursor.getString(i));
//                }
//                Log.d(TAG,"onCreate: ========================================");
//
//            }
//            cursor.close();
//        }
////        AppDatabase appDatabase = AppDatabase.getInstance(this);
////        final SQLiteDatabase db = appDatabase.getReadableDatabase();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.nav_host_fragment_content_main);


        if(!mTwoPane){
            //we've just removed the editing fragment, so hide the frame
            addEditLayout.setVisibility(View.GONE);

            // and make sure the MainActivityFragment is visible.
            mainFragment.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      switch (id){
          case R.id.menumain_addTask:
              taskEditRequest(null);
              break;
          case R.id.menumain_showDurations:
              break;
          case R.id.menumain_settings:
              break;
          case R.id.menumain_showAbout:
              showAboutDialog();
              break;
          case R.id.menumain_generate:
              break;
          case android.R.id.home:
              Log.d(TAG, "onOptionsItemSelected: home button pressesd");
              AddEditActivityFragment fragment = (AddEditActivityFragment)
                      getSupportFragmentManager().findFragmentById(R.id.task_details_container);
              assert fragment != null;
              if(fragment.canClose()) {
                  return super.onOptionsItemSelected(item);
              }else {
                  showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                  return true;  // indicate we are handling this
              }
      }

        return super.onOptionsItemSelected(item);
    }
     @SuppressLint("SetTextI18n")
     public void showAboutDialog() {
         View messageView = getLayoutInflater().inflate(R.layout.about,null,false);
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(R.string.app_name);
         builder.setIcon(R.drawable.ic_launcher_round);

         builder.setView(messageView);
         builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
//                 Log.d(TAG, "onClick: Entering messageView.onClick, showing = " +mDialog.isShowing());
                 if(mDialog != null && mDialog.isShowing()) {
                     mDialog.dismiss();
                 }
             }
         });

         mDialog = builder.create();
         mDialog.setCanceledOnTouchOutside(true);

         TextView tv = (TextView) messageView.findViewById(R.id.about_version);
         tv.setText("v" + BuildConfig.VERSION_NAME);
         mDialog.show();
     }

    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.deldiag_message,task.getId(),task.getmName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("TaskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);


    }

    private void taskEditRequest(Task task){
        Log.d(TAG, "taskEditRequest: starts");
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);


        Log.d(TAG, "taskEditRequest: twoPaneMode");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_details_container, fragment)
                    .commit();

        if (!mTwoPane){
            Log.d(TAG,"taskEditRequest: in single_pane mode(phone)");
           //Hide the left hand fragment and show the right had frame
            View mainFragment = findViewById(R.id.nav_host_fragment_content_main);
            View addEditLayout = findViewById(R.id.task_details_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "Exiting taskEditRequest");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                Long taskID = args.getLong("TaskId");
                if (BuildConfig.DEBUG && taskID == 0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(TaskContract.buildTaskUri(taskID),null,null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                //no action required
                break;

        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch(dialogId) {
            case DIALOG_ID_DELETE:
                // no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                // If we're editing, remove the fragment. otherwise,close the app
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
                if(fragment !=null){
                    //we were editing
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                    if (mTwoPane) {
                        // in landscape, so quit only if the back button was used
                        if(dialogId == DIALOG_ID_CANCEL_EDIT){
                            finish();
                        }
                    }else {
                        //hide the edit container in single pane mode
                        //and make sure the left-hand container is visible

                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.nav_host_fragment_content_main);
                        //We're just removed the editing fragment, so hide the frame
                        addEditLayout.setVisibility(View.GONE);

                        //and make sure the MainActivityFragment is visible
                        mainFragment.setVisibility(View.VISIBLE);
                    }

                }else {
                    //not editing, so quit regardless of orientation
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if((fragment == null) || fragment.canClose()) {
            super.onBackPressed();
        }else {
            //show dialogue to get confirmation to quit editing
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.d(TAG, "onAttachFragment: called, fragment is " + fragment.toString());
        super.onAttachFragment(fragment);
    }
    private void showConfirmationDialog(int dialogId) {
        //show dialogue to get confirmation to quit editing
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID,dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID,R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);

    }

}