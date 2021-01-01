package com.example.todolist;

public class Task {
    private String taskName;
    private Boolean isChecked;
    private String taskId;
    private String timeAdd;

    public String getTimeAdd() {
        return timeAdd;
    }

    public void setTimeAdd(String timeAdd) {
        this.timeAdd = timeAdd;
    }

    private String listId;

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Task() {
    }

//    public Task(String taskName, Boolean isChecked) {
//        this.taskName = taskName;
//        this.isChecked = isChecked;
//    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
    }


}
