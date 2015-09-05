package comp5216.sydney.edu.au.todolist.service;

import java.util.ArrayList;

import comp5216.sydney.edu.au.todolist.model.MesssageModel;

/**
 * Created by Barry on 2015/8/25.
 */
public interface IItemsService {

    public ArrayList<MesssageModel> readItems(ArrayList<MesssageModel> messages);
    public boolean insertItems(ArrayList<MesssageModel> messages,MesssageModel mess);
    public boolean updateItems(ArrayList<MesssageModel> messages,MesssageModel mess,String text);
    public boolean updateItems(ArrayList<MesssageModel> messages,MesssageModel mess,MesssageModel old);
    public boolean deleteItems(ArrayList<MesssageModel> messages,MesssageModel mess);
}
