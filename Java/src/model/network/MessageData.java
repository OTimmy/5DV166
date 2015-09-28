package model.network;

public class MessageData {
    private String nickname;
    private String msg;
    private String time;


    public MessageData(String nickname, String msg, String time) {
        this.nickname = nickname;
        this.msg = msg;
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public String getNickname() {
        return nickname;
    }


    public String getTime() {
        return time;
    }
}
