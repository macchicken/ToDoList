package comp5216.sydney.edu.au.todolist.service;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.todolist.DTO.Item;
import comp5216.sydney.edu.au.todolist.common.tools;
import comp5216.sydney.edu.au.todolist.model.MesssageModel;

/**
 * Created by Barry on 2015/8/25.
 */
public class SqliteItemsService implements IItemsService{

    private static class SqliteItemsServiceHolder {
        private static final SqliteItemsService INSTANCE = new SqliteItemsService();
    }

    private SqliteItemsService(){}

    public static SqliteItemsService getInstance(){
        return SqliteItemsServiceHolder.INSTANCE;
    }

    @Override
    public ArrayList<MesssageModel> readItems(ArrayList<MesssageModel> messages) {
        //read items from database
        List<Item> itemsFromORM = new Select().from(Item.class).execute();
        ArrayList<MesssageModel> result = new ArrayList<MesssageModel>();
        if(itemsFromORM != null & itemsFromORM.size() > 0){
            for(Item item:itemsFromORM){
                result.add(new MesssageModel(item.id,item.createdTime,item.name));
            }
        }
        return result;
    }

    @Override
    public boolean insertItems(ArrayList<MesssageModel> messages, MesssageModel mess) {
        Item item=new Select().from(Item.class).where("Name = ? ", mess.getContent()).executeSingle();
        if (item!=null){return false;}
        ActiveAndroid.beginTransaction();
        try{
            Item newItme=new Item();
            newItme.id=java.util.UUID.randomUUID().toString();
            newItme.name=mess.getContent();
            newItme.createdTime=mess.getCreatedTime();
            newItme.save();
            ActiveAndroid.setTransactionSuccessful();
        }   finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

    @Override
    public boolean updateItems(ArrayList<MesssageModel> messages, MesssageModel mess,String text) {
        return false;
    }

    @Override
    public boolean updateItems(ArrayList<MesssageModel> messages, MesssageModel mess, MesssageModel old) {
        Item item=new Select().from(Item.class).where("Name = ? ",old.getContent()).executeSingle();
        ActiveAndroid.beginTransaction();
        try{
            item.name=mess.getContent();
            item.save();
            ActiveAndroid.setTransactionSuccessful();
        }   finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

    @Override
    public boolean deleteItems(ArrayList<MesssageModel> messages, MesssageModel mess) {
        Item item=new Select().from(Item.class).where("Name = ? ",mess.getContent()).executeSingle();
        ActiveAndroid.beginTransaction();
        try{
            item.delete();
            ActiveAndroid.setTransactionSuccessful();
        }   finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

}
