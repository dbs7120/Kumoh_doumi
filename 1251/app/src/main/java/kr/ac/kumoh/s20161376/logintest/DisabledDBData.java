package kr.ac.kumoh.s20161376.logintest;

public class DisabledDBData {

    String firebasekey;
    String userAddress;
    String userWork;

    public DisabledDBData() {
    }

    public DisabledDBData(String firebasekey, String userAddress, String userWork) {

        this.firebasekey = firebasekey;
        this.userAddress = userAddress;
        this.userWork = userWork;

    }

    public String getFirebasekey() {
        return firebasekey;
    }

    public void setFirebasekey(String firebasekey) {
        this.firebasekey = firebasekey;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserWork() {
        return userWork;
    }

    public void setUserWork(String userWork) {
        this.userWork = userWork;
    }
}