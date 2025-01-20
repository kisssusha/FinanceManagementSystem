package entities;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;
    private final String password;
    private final Wallet wallet;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.wallet = new Wallet();
    }

    public String getPassword() {
        return password;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getLogin() {
        return login;
    }
}
