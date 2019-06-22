package JCoreFX.core.linkConstruction;

public class DataLink<T> {
    private T data;
    private String sender;

    public T getData() { return data; }

    public String getSender() { return sender; }

   public DataLink(T data, String sender)
    {
        this.data = data;
        this.sender = sender;
    }
}
