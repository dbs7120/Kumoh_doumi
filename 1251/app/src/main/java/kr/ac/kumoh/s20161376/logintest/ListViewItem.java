package kr.ac.kumoh.s20161376.logintest;

public class ListViewItem {

    private String locationStr;
    private String taskStr;


    public void setLocation(String location) {
        locationStr = location ;
    }
    public void setTask(String task) {
        taskStr = task ;
    }


    public String getLocation() {
        return this.locationStr;
    }
    public String getTask() {
        return this.taskStr;
    }
}