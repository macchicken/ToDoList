package comp5216.sydney.edu.au.todolist.model;

import java.io.Serializable;

/**
 * Created by Barry on 2015/8/16.
 */
public class MesssageModel implements Serializable{

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MesssageModel(String id,String createdTime, String content) {
        this.id=id;
        this.createdTime = createdTime;
        this.content = content;
    }

    private String createdTime;
    private String content;
    private String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MesssageModel that = (MesssageModel) o;
        return content.equals(that.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

}
