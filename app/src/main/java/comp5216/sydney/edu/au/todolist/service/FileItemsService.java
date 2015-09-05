package comp5216.sydney.edu.au.todolist.service;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.todolist.common.tools;
import comp5216.sydney.edu.au.todolist.model.MesssageModel;

/**
 * Created by Barry on 2015/8/25.
 */
public class FileItemsService implements IItemsService{

    private static File todoFile;

    private static class FileItemsServiceHolder {
        private static final FileItemsService INSTANCE = new FileItemsService();
    }

    private FileItemsService(){
    }

    public static FileItemsService getInstance(File file){
        todoFile=file;
        return FileItemsServiceHolder.INSTANCE;
    }

    @Override
    public ArrayList<MesssageModel> readItems(ArrayList<MesssageModel> messages) {
        if(!todoFile.exists()){
            messages = new ArrayList<MesssageModel>();
        }else{
            try{
                messages = new ArrayList<MesssageModel>();
                //read data and put it into the ArrayList
                List<String> temp= FileUtils.readLines(todoFile);
                for (String str:temp){
                    messages.add(new MesssageModel(java.util.UUID.randomUUID().toString(),tools.localCreatedTime(),str));
                }
            }catch(IOException ex){
                messages = new ArrayList<MesssageModel>();
            }
        }
        return messages;
    }

    @Override
    public boolean insertItems(ArrayList<MesssageModel> messages, MesssageModel mess) {
        try {    //write list to file
            if (messages.contains(mess)){return false;}
            FileUtils.writeStringToFile(todoFile, mess.getContent(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            return  false;
        }
        return true;

    }

    @Override
    public boolean updateItems(ArrayList<MesssageModel> messages, MesssageModel mess,String text) {
        try {    //write list to file
            List<String> temp = new ArrayList<String>();
            for (MesssageModel m : messages) {
                temp.add(m.getContent());
            }
            FileUtils.writeLines(todoFile, temp);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateItems(ArrayList<MesssageModel> messages, MesssageModel mess, MesssageModel old) {
        return false;
    }

    @Override
    public boolean deleteItems(ArrayList<MesssageModel> messages,MesssageModel mess) {
        return updateItems(messages, mess,"");
    }

}
