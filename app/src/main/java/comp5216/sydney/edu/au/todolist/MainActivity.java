package comp5216.sydney.edu.au.todolist;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import comp5216.sydney.edu.au.todolist.DTO.Message;
import comp5216.sydney.edu.au.todolist.common.tools;
import comp5216.sydney.edu.au.todolist.model.MesssageModel;
import comp5216.sydney.edu.au.todolist.service.RemoteService;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class MainActivity extends ActionBarActivity {

    ListView listview;
    ArrayList<MesssageModel> messesages;
    MyMesssageAdapter messAdapter;
    EditText addItemEditText;
    public final int EDIT_ITEM_REQUEST_CODE = 647;
//    private IItemsService storeService= SqliteItemsService.getInstance();
    private MobileServiceClient mClient;
    private MobileServiceTable<Message> mMessageTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mClient = new MobileServiceClient("https://macchickentodolist.azure-mobile.net/", "EBpItddmmIqYlzvAljjFRSRowGQPtZ73", this);
            mMessageTable = mClient.getTable(Message.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
        //reference the "listview" variable to the id-"listview" in the layout
        listview = (ListView) findViewById(R.id.listView);
        addItemEditText = (EditText) findViewById(R.id.txtNewItem);
        //create an ArrayList of String
        messesages = new ArrayList<MesssageModel>();
//        storeService= FileItemsService.getInstance(new File(getFilesDir(), "todo.txt"));
//        messesages=storeService.readItems(null);
        messAdapter=new MyMesssageAdapter(this,messesages);
        listview.setAdapter(messAdapter);
        refreshItemsFromTable();
        setupListViewListener();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddItemClick(View view) {
        String toAddString = addItemEditText.getText().toString();
        if (toAddString != null && toAddString.length() > 0) {
//            MesssageModel temp=new MesssageModel(java.util.UUID.randomUUID().toString(),tools.localCreatedTime(), toAddString);
            Message temp=new Message(null,toAddString,tools.localCreatedTime());
            addNewMessage(temp);
            addItemEditText.setText(""); // Reset the edittext
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
                    }.execute();
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
                    }.execute();
            }

            private void delMessage(final Message message){
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                                try {
                                        mMessageTable.delete(message);
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                return null;
                            }
                    }.execute();
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
                                                        if(result!=null){
                                                                messAdapter.add(new MesssageModel(result.getId(),result.getCreatedTime(),result.getContent()));
                                                            }
                                                    }
                                            });
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                return null;
                            }
                    }.execute();
            }
}
