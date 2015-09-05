package comp5216.sydney.edu.au.todolist.DTO;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Barry on 2015/8/25.
 */
@Table(name = "Items")
public class Item extends Model{

    @Column(name="remote_id",unique = true)
    public String id;
    @Column(name = "Name")
    public String name;
    @Column(name="created_time")
    public String createdTime;

    // Make sure to have a default constructor for every ActiveAndroid model!!!!!!
    public Item(){
        super();
    }

}
