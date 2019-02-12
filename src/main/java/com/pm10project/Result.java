package com.pm10project;

@SuppressWarnings("unused")
public class Result {

    // constants
    static int OK = 0;
    static int INCOMPLETE_DATA = 1;
    static int WRITING_ERROR = 10;
    static int BACKUP_ERROR = 11;
    static int CONNECTION_ERROR = 100;

    private int resultCode;

    Result(int code) {
        this.resultCode = code;
    }

    public int getResultCode() {
        return resultCode;
    }
}
