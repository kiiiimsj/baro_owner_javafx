package com.baro.JsonParsing;

public class LoginParsing {
    private boolean result;
    private String phone;
    private String store_id;
    private String nick;
    private String store_name;
    private String message;
    private String email;
    private String is_open;

    public boolean isResult() { return result; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setResult(boolean result) { this.result = result; }
    public String getStore_id() { return store_id; }
    public void setStore_id(String store_id) { this.store_id = store_id; }
    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }
    public String getStore_name() { return store_name; }
    public void setStore_name(String store_name) { this.store_name = store_name; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getIs_open() { return is_open; }
    public void setIs_open(String is_open) { this.is_open = is_open; }
}
