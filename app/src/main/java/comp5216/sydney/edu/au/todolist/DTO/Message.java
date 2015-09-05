package comp5216.sydney.edu.au.todolist.DTO;

/**
 * Created by Barry on 2015/8/31.
 */
public class Message {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("content")
    private String mContent;

    @com.google.gson.annotations.SerializedName("createdtime")
    private String mCreatedTime;

    public Message(){}

    public Message(String id,String content,String createdTime){
        mId=id;
        mContent=content;
        mCreatedTime=createdTime;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
