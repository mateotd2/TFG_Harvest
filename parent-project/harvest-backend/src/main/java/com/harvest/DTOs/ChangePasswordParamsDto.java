package com.harvest.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChangePasswordParamsDto {

    /**
     * The old password.
     */
    private String oldPassword;

    /**
     * The new password.
     */
    private String newPassword;

    /**
     * Instantiates a new change password params dto.
     */
    public ChangePasswordParamsDto() {
        super();
    }

    /**
     * Gets the old password.
     *
     * @return the old password
     */
    @NotNull
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets the old password.
     *
     * @param oldPassword the new old password
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Gets the new password.
     *
     * @return the new password
     */
    @NotNull
    @Size(min = 1, max = 60)
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password.
     *
     * @param newPassword the new new password
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}