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
    ACTION_EXIT("ACTION_EXIT"),
    ACTION_NOT_IMPLEMENTED("ACTION_NOT_IMPLEMENTED"),
    //Home path
    THEME("THEME"),

    NAME_FILE("trident.properties"),
    DEFAULT_PATH_LINU("/.trident/"),
    DEFAULT_PATH_OTHERS("/trident/");

    private String value;

    Constant(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
