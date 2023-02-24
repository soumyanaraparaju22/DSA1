package genericnode;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;
import genericnode.RMI.Hello;

public class Operations extends UnicastRemoteObject implements Hello {
    public Operations() throws RemoteException {
        super();
    }

    public Hashtable < String, String > data = new Hashtable < > ();

    public synchronized String put(String key, String value) {
        data.put(key, value);
        return "server response:put key=" + key;

    }
    public synchronized String get(String key) {

        if (data.containsKey(key)) {
            return "server response:get key=" + key + "get val=" + data.get(key);
        } else {
            return " Key Does not found";
        }
    }
    public synchronized String del(String key) {
        if (data.containsKey(key)) {
            data.remove(key);
            return "server response:delete key=" + key;
        } else {
            return "Key Does not found";
        }

    }
    public synchronized String store() {
        String str = "server response:";
        for (Map.Entry < String, String > map: data.entrySet()) {
            str += "\n" + "key:" + map.getKey() + "value:" + map.getValue();

        }
        return str;


    }

    public synchronized void exit() {
        try {
            Naming.unbind("rmi://localhost:6600/Add");
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception e) {
            System.out.println("Error in exit");
        }



    }



}