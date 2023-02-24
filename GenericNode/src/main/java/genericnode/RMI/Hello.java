package genericnode.RMI;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {
    
    public   String put(String key, String value) throws Exception;
    public  String get(String key) throws Exception;
    public  String del(String key) throws Exception;
    public  String store() throws Exception;
    public  void exit() throws Exception;

}