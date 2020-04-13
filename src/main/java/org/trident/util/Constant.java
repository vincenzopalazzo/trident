package org.trident.util;

/**
 * @author https://github.com/vincenzopalazzo
 */
public enum Constant {
    LISTENER_EDIT_MENU("LISTENER_EDIT_MENU"),
    LISTENER_SETTING_MENU("LISTENER_SETTING_MENU"),
    LISTENER_TOOLS_MENU("LISTENER_TOOLS_MENU"),
    LISTENER_ABOUT_MENU("LISTENER_ABOUT_MENU"),
    LISTENER_FILE_MENU("LISTENER_FILE_MENU"),
    ACTION_EXIT("ACTION_EXIT");

    private String value;

    Constant(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
