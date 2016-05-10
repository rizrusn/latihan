package actors;

/**
 * Created by rizrusn on 12/04/16.
 */
public class FileReaderProtocol implements java.io.Serializable {
    public final String filename;
    public FileReaderProtocol(String filename) { this.filename = filename; }
}
