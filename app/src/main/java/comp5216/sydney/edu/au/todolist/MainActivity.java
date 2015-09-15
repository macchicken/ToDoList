package comp5216.sydney.edu.au.todolist;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import comp5216.sydney.edu.au.todolist.DTO.Message;
import comp5216.sydney.edu.au.todolist.common.tools;
import comp5216.sydney.edu.au.todolist.model.MesssageModel;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutionException;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;

public class MainActivity extends ActionBarActivity {

    ListView listview;
    ArrayList<MesssageModel> messesages;
    MyMesssageAdapter messAdapter;
    EditText addItemEditText;
    private ProgressBar mProgressBar;
    public final int EDIT_ITEM_REQUEST_CODE = 647;
//    private IItemsService storeService= SqliteItemsService.getInstance();
    private MobileServiceClient mClient;
    private MobileServiceTable<Message> mMessageTable;
    private static final String SHAREDPREFFILE = "temp";
    private static final String USERIDPREF = "uid";
    private static final String TOKENPREF = "tkn";
    private boolean bAuthenticating = false;
    private final Object mAuthenticationLock = new Object();
    private Button addBtn;
    private CookieManager myc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
        //reference the "listview" variable to the id-"listview" in the layout
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);
        addBtn= (Button) findViewById(R.id.btnAddItem);
        addBtn.setEnabled(false);
        System.out.println("add button disabled");
        try {
            mClient = new MobileServiceClient("https://macchickentodolist.azure-mobile.net/", "EBpItddmmIqYlzvAljjFRSRowGQPtZ73", this).withFilter(new ProgressFilter()).withFilter(new RefreshTokenCacheFilter());
//            mMessageTable = mClient.getTable(Message.class);
            authenticate(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        listview = (ListView) findViewById(R.id.listView);
//        addItemEditText = (EditText) findViewById(R.id.txtNewItem);
        //create an ArrayList of String
//        messesages = new ArrayList<MesssageModel>();
//        storeService= FileItemsService.getInstance(new File(getFilesDir(), "todo.txt"));
//        messesages=storeService.readItems(null);
//        messAdapter=new MyMesssageAdapter(this,messesages);
//        listview.setAdapter(messAdapter);
//        refreshItemsFromTable();
        myc=android.webkit.CookieManager.getInstance();
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
            case R.id.log_out:
                deleteUserToken(mClient.getCurrentUser());
                myc.removeAllCookie();
                mClient.logout();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TodoListApplication.getContext().startActivity(startMain);
                return true;
            case R.id.action_settings: return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void onAddItemClick(View view) {
        String toAddString = addItemEditText.getText().toString();
        if (toAddString != null && toAddString.length() > 0) {
//            MesssageModel temp=new MesssageModel(java.util.UUID.randomUUID().toString(),tools.localCreatedTime(), toAddString);
            if (containMessage(toAddString)){
                Toast.makeText(this, "there is:'" + toAddString+"' in the list", Toast.LENGTH_SHORT).show();
            }else{
                Message temp=new Message(null,toAddString,tools.localCreatedTime());
                addNewMessage(temp);
                addItemEditText.setText(""); // Reset the edittext
            }
//            if (!storeService.insertItems(messesages,temp)){
//                Toast.makeText(this, "there is:'" + temp.getContent()+"' in the list", Toast.LENGTH_SHORT).show();
//            }else{
//                messAdapter.add(temp);
//                addItemEditText.setText(""); // Reset the edittext
//            }

        }
    }

    private void setupListViewListener() {
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
                Log.i("MainActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final MesssageModel temp=messesages.get(position);
                String content=temp.getContent();
                String header=getResources().getString(R.string.dialog_delete_msg);
                content=header+" '"+content+"'?";
                builder.setTitle(R.string.dialog_delete_title).setMessage(content).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                         //delete the item
                        messesages.remove(position);
                        messAdapter.notifyDataSetChanged();
//                        storeService.deleteItems(messesages,temp);
                        delMessage(new Message(temp.getId(),temp.getContent(),temp.getCreatedTime()));
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    //User cancelled the dialog
                    // nothing happens
                    }
                });
                builder.create().show();
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MesssageModel updateItem = (MesssageModel) messAdapter.getItem(position);
                Log.i("MainActivity", "Clicked item " + position + ": " + updateItem.getContent());
                Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
                if (intent != null) {// put "extras" into the bundle for access in the edit activity
                    intent.putExtra("item",updateItem);
                    intent.putExtra("position", position);             // brings up the second activity
                    startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
                    messAdapter.notifyDataSetChanged();
                }
            }
        }
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {             // Extract name value from result extras
                MesssageModel editedItem= (MesssageModel) data.getSerializableExtra("item");
                MesssageModel oldItem= (MesssageModel) data.getSerializableExtra("oldItem");
                int position = data.getIntExtra("position", -1);
                messesages.set(position, editedItem);
                Log.i("Updated Item in list:", editedItem.getContent() + ",position:" + position);
                Toast.makeText(this, "updated:" + editedItem.getContent(), Toast.LENGTH_SHORT).show();
                messAdapter.notifyDataSetChanged();
//                storeService.updateItems(messesages, editedItem, oldItem);
                updateMessage(new Message(editedItem.getId(),editedItem.getContent(),editedItem.getCreatedTime()));
            }
        }
    }

    private void refreshItemsFromTable() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                        final List<Message> results = mMessageTable.where().execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (results!=null){
                                    messesages.clear();
                                    for (Message item : results) {
                                        messesages.add(new MesssageModel(item.getId(),item.getCreatedTime(),item.getContent()));
                                        }
                                    messAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    } catch (final Exception e){
                        e.printStackTrace();
                    }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private void updateMessage(final Message newMessage){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mMessageTable.update(newMessage);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private void delMessage(final Message message){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mMessageTable.delete(message);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private void addNewMessage(final Message message){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Message result = mMessageTable.insert(message).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null) {
                                messAdapter.add(new MesssageModel(result.getId(), result.getCreatedTime(), result.getContent()));
                            }
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private boolean containMessage(String text){
        for (MesssageModel mm:messesages){
            if (mm.getContent().equals(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    return task.execute();
                }
    }

    private class ProgressFilter implements ServiceFilter {
        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {
            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressBar != null){
                        mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    }
                }
            });
            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);
            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }
                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null){
                                mProgressBar.setVisibility(ProgressBar.GONE);
                                System.out.println("add button enabled");
                                addBtn.setEnabled(true);
                            }
                        }
                    });
                    resultFuture.set(response);
                }
            });
            return resultFuture;
        }
    }

    private void authenticate(boolean bRefreshCache) {
        bAuthenticating = true;
        if (bRefreshCache || !loadUserTokenCache(mClient))
        {
            // New login using the provider and update the token cache.
            mClient.login(MobileServiceAuthenticationProvider.Google, new UserAuthenticationCallback() {
                        @Override
                        public void onCompleted(MobileServiceUser user, Exception exception, ServiceFilterResponse response) {
                            synchronized (mAuthenticationLock) {
                                if (exception == null) {
                                    cacheUserToken(mClient.getCurrentUser());
                                    createTable();
                                } else {
                                    createAndShowDialog(exception.getMessage(), "Login Error");
                                }
                                bAuthenticating = false;
                                mAuthenticationLock.notifyAll();
                            }
                        }
            });
        }
        else
        {
            // Other threads may be blocked waiting to be notified when
            // authentication is complete.
            synchronized(mAuthenticationLock)
            {
                bAuthenticating = false;
                mAuthenticationLock.notifyAll();
            }
            createTable();
        }
    }

    private void createTable() {
        System.out.println("create table");
        // Get the Mobile Service Table instance to use
        mMessageTable = mClient.getTable(Message.class);
        addItemEditText = (EditText) findViewById(R.id.txtNewItem);
        messesages = new ArrayList<MesssageModel>();
        // Create an adapter to bind the items with the view
        messAdapter=new MyMesssageAdapter(this,messesages);
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(messAdapter);
        // Load the items from the Mobile Service
        refreshItemsFromTable();
        setupListViewListener();
    }

    /**
     * Creates a dialog and shows it
     * @param message
     * The dialog message
     * @param title
     * The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void cacheUserToken(MobileServiceUser user)
    {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.commit();
    }
    private void deleteUserToken(MobileServiceUser user){
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.remove(USERIDPREF);
        editor.remove(TOKENPREF);
        editor.commit();
    }

    private boolean loadUserTokenCache(MobileServiceClient client)
    {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, "undefined");
        if (userId == "undefined")
            return false;
        String token = prefs.getString(TOKENPREF, "undefined");
        if (token == "undefined")
            return false;
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);
        return true;
    }

    /**
     * Detects if authentication is in progress and waits for it to complete.
     * Returns true if authentication was detected as in progress. False otherwise.
     */
    private boolean detectAndWaitForAuthentication()
    {
        boolean detected = false;
        synchronized(mAuthenticationLock)
        {
            do
            {
                if (bAuthenticating == true)
                    detected = true;
                try
                {
                    mAuthenticationLock.wait(1000);
                }
                catch(InterruptedException e)
                {}
            } while(bAuthenticating == true);
        }
        if (bAuthenticating == true)
            return true;
        return detected;
    }

    /**
     * Waits for authentication to complete then adds or updates the token
     * in the X-ZUMO-AUTH request header.
     *
     * @param request
     *            The request that receives the updated token.
     */
    private void waitAndUpdateRequestToken(ServiceFilterRequest request)
    {
        MobileServiceUser user = null;
        if (detectAndWaitForAuthentication())
        {
            user = mClient.getCurrentUser();
            if (user != null)
            {
                request.removeHeader("X-ZUMO-AUTH");
                request.addHeader("X-ZUMO-AUTH", user.getAuthenticationToken());
            }
        }
    }

    /**
     * The RefreshTokenCacheFilter class filters responses for HTTP status code 401.
     * When 401 is encountered, the filter calls the authenticate method on the
     * UI thread. Out going requests and retries are blocked during authentication.
     * Once authentication is complete, the token cache is updated and
     * any blocked request will receive the X-ZUMO-AUTH header added or updated to
     * that request.
     */
    private class RefreshTokenCacheFilter implements ServiceFilter {
        AtomicBoolean mAtomicAuthenticatingFlag = new AtomicBoolean();
        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(final ServiceFilterRequest request, final NextServiceFilterCallback nextServiceFilterCallback)
        {
            // In this example, if authentication is already in progress we block the request
            // until authentication is complete to avoid unnecessary authentications as
            // a result of HTTP status code 401.
            // If authentication was detected, add the token to the request.
            waitAndUpdateRequestToken(request);

            // Send the request down the filter chain
            // retrying up to 5 times on 401 response codes.
            ListenableFuture<ServiceFilterResponse> future = null;
            ServiceFilterResponse response = null;
            int responseCode = 401;
            for (int i = 0; (i < 5 ) && (responseCode == 401); i++)
            {
                future = nextServiceFilterCallback.onNext(request);
                try {
                    response = future.get();
                    responseCode = response.getStatus().getStatusCode();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    if (e.getCause().getClass() == MobileServiceException.class)
                    {
                        MobileServiceException mEx = (MobileServiceException) e.getCause();
                        responseCode = mEx.getResponse().getStatus().getStatusCode();
                        if (responseCode == 401)
                        {
                            // Two simultaneous requests from independent threads could get HTTP status 401.
                            // Protecting against that right here so multiple authentication requests are
                            // not setup to run on the UI thread.
                            // We only want to authenticate once. Requests should just wait and retry
                            // with the new token.
                            if (mAtomicAuthenticatingFlag.compareAndSet(false, true))
                            {
                                // Authenticate on UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Force a token refresh during authentication.
                                        authenticate(true);
                                    }
                                });
                            }
                            // Wait for authentication to complete then update the token in the request.
                            waitAndUpdateRequestToken(request);
                            mAtomicAuthenticatingFlag.set(false);
                        }
                    }
                }
            }
            return future;
        }
    }
}
