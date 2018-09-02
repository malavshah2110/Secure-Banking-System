package org.asu.group8.model;

public class CreateAccountResponse {

    private Boolean created;

    private Boolean isInvalidUser;

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }


    public Boolean getIsInvalidUser(){
        return isInvalidUser;
    }

    public void setIsInvalidUser(Boolean isInvalidUser){
        this.isInvalidUser = isInvalidUser;
    }



}
