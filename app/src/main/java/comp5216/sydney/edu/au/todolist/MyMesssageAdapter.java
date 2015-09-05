package comp5216.sydney.edu.au.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import comp5216.sydney.edu.au.todolist.model.MesssageModel;

/**
 * Created by Barry on 2015/8/16.
 */
public class MyMesssageAdapter extends ArrayAdapter<MesssageModel> {

    private static class ViewHolder {
        TextView createdTime;
        TextView content;
    }

    public MyMesssageAdapter(Context context, ArrayList<MesssageModel> messages){
        super(context,R.layout.item_mess,messages);
    }
    public MyMesssageAdapter(Context context){
        super(context,R.layout.item_mess);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        MesssageModel mess=getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mess, parent, false);
            viewHolder.createdTime=(TextView) convertView.findViewById(R.id.tvCreatedTime);
            viewHolder.content=(TextView) convertView.findViewById(R.id.tvContent);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data population
//        TextView tvCreatedTime = (TextView) convertView.findViewById(R.id.tvCreatedTime);
//        TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
        // Populate the data into the template view using the data object
//        tvCreatedTime.setText(mess.getCreatedTime());
//        tvContent.setText(mess.getContent());
        viewHolder.createdTime.setText(mess.getCreatedTime());
        viewHolder.content.setText(mess.getContent());
        // Return the completed view to render on screen
        return convertView;
    }
}
