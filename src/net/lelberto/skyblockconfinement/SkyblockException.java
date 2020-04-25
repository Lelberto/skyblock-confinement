package net.lelberto.skyblockconfinement;

public class SkyblockException extends Exception {

    public SkyblockException(String msg) {
        super(msg);
    }

    public SkyblockException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
