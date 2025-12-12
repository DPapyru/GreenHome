package comdpapyru.greenhome.exception;

public class NewUserException extends Exception{
    public NewUserException(String message) {
        super("创建新用户时异常:" + message);
    }
}
