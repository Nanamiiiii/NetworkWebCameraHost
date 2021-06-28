package nanami.networkwebcamerahost;

public class StreamReadingException extends Exception{
    public StreamReadingException(){}
    public StreamReadingException(String str){
        super(str);
    }
    public StreamReadingException(Throwable cause){
        super(cause);
    }
    public StreamReadingException(String str, Throwable cause){
        super(str, cause);
    }
}
