package br.example.com.brapolar.Utils;

public class UserSingleton {
    private static UserSingleton mInstance;

    private String userName;
    private String email;

    private UserSingleton() {
    }

    public static synchronized UserSingleton getInstance() {
        if (null == mInstance) {
            mInstance = new UserSingleton();
        }
        return mInstance;
    }

    public void setUserName(String d) {
        this.userName = capitalizeFirstLetter(d);
    }

    public String getUserName() {
        return this.userName;
    }

    public void setEmail(String d) {
        this.email = d;
    }

    public String getEmail() {
        return this.email;
    }

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
