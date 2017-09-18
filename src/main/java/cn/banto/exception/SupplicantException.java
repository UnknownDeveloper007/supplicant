package cn.banto.exception;

public class SupplicantException extends Exception {

    public SupplicantException(String msg){
        super(msg);
    }

    public SupplicantException(String msg, Throwable e){
        super(msg, e);
    }
}
