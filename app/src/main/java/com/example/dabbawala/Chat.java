package com.example.dabbawala;


public class Chat  {

    private String mName;
    private String mMessage;
    private String mUid;
    private String tUid;
    private String time;
    private String file;
    private Boolean isSender;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }

    public Chat() {
        // Needed for Firebase
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String gettUid() {
        return tUid;
    }

    public void settUid(String tUid) {
        this.tUid = tUid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Chat(String name, String message, String uid, String tUid, String time,String file) {
        mName = name;
        mMessage = message;
        mUid = uid;
        this.tUid = tUid;
        this.time = time;
        this.file = file;
    }


}
