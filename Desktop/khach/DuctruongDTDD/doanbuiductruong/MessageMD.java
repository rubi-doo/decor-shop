package chatapplication;

import java.sql.Timestamp;

public class MessageMD {
    private int id;
    private final String sender;
    private final String content;
    private final Timestamp timestamp;

    public MessageMD(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
