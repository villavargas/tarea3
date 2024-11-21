/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.request.models;

import javax.validation.constraints.NotNull;

public class UserPreregisterRequest {

    @NotNull
    private String userID;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmNewPassword;
    private String phoneEmail;
    //private String completeName;
    private String token;
    private String firstName;
    private String lastName;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneEmail() {
        return phoneEmail;
    }

    public void setPhoneEmail(String phoneEmail) {
        this.phoneEmail = phoneEmail;
    }

    /*public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }*/

    public String getNewPassword() {return newPassword;}

    public void setNewPassword(String newPassword) {this.newPassword = newPassword;}

    public String getConfirmNewPassword() {return confirmNewPassword;}

    public void setConfirmNewPassword(String confirmNewPassword) {this.confirmNewPassword = confirmNewPassword;}

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}
}
