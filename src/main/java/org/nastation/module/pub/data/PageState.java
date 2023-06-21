package org.nastation.module.pub.data;

public enum PageState {

    UNKNOWN_ERROR("Unknown error", 0),
    SESSION_TIMEOUT("Session timeout", 1),
    PAGE_NOT_FOUND("Page not found", 2),
    SYSTEM_ERROR("System error", 3);

    public String name;

    public int value;

    PageState(String name, int index) {
        this.name = name;
        this.value = index;
    }

    public static String getNameByValue(int value) {
        for (PageState c : PageState.values()) {
            if (c.value == value) {
                return c.name;
            }
        }
        return UNKNOWN_ERROR.name;
    }
}

