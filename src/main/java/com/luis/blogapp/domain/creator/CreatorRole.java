package com.luis.blogapp.domain.creator;

public enum CreatorRole {
    ADMIN("admin"),
    USER("user");

    private String role;

    CreatorRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
