package comp5216.sydney.edu.au.todolist.service;

import android.os.AsyncTask;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.todolist.DTO.Message;
import comp5216.sydney.edu.au.todolist.MyMesssageAdapter;
import comp5216.sydney.edu.au.todolist.model.MesssageModel;

/**
 * Created by Barry on 2015/9/7.
 */
public class remoteService {

    private MobileServiceClient mClient;
    private MobileServiceTable<Message> mMessageTable;
    private ArrayList<MesssageModel> messesages;
    private MyMesssageAdapter messAdapter;

    public void refreshItemsFromTable() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Message> results = mMessageTable.where().execute().get();
                    if (results!=null){
                        messesages.clear();
                        for (Message item : results) {
                            messesages.add(new MesssageModel(item.getId(),item.getCreatedTime(),item.getContent()));
                        }
                        messAdapter.notifyDataSetChanged();
                    }
                } catch (final Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void updateMessage(final Message newMessage){
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

    public void delMessage(final Message message){
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

    public void addNewMessage(final Message message){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Message result = mMessageTable.insert(message).get();
                    if(result!=null){
                        messAdapter.add(new MesssageModel(result.getId(),result.getCreatedTime(),result.getContent()));
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void setmClient(MobileServiceClient mClient) {
        this.mClient = mClient;
    }

    public void setmMessageTable(MobileServiceTable<Message> mMessageTable) {
        this.mMessageTable = mMessageTable;
    }

    public void setMessesages(ArrayList<MesssageModel> messesages) {
        this.messesages = messesages;
    }

    public void setMessAdapter(MyMesssageAdapter messAdapter) {
        this.messAdapter = messAdapter;
    }
}
