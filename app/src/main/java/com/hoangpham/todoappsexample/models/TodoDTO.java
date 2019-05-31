package com.hoangpham.todoappsexample.models;

public class TodoDTO {
    private boolean isChecked;
    private String work;
    private boolean isFavorite;

    public TodoDTO(boolean isChecked, String work, boolean isFavorite) {
        this.isChecked = isChecked;
        this.work = work;
        this.isFavorite = isFavorite;
    }

    public TodoDTO() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
