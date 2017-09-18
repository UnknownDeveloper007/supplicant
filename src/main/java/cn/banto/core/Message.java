package cn.banto.core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by banto on 2017/5/18.
 */
public class Message {

    private int action;
    private HashMap<Integer, byte[]> data = new HashMap<Integer, byte[]>();
    private HashMap<Integer, ArrayList<byte[]>> manyData = new HashMap<Integer, ArrayList<byte[]>>();

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public byte[] getData(int key) {
        return data.get(key);
    }

    public ArrayList<byte[]> getDatas(int key){
        return manyData.get(key);
    }

    public void putDatas(int key, ArrayList<byte[]> value){
        manyData.put(key, value);
    }

    public void putData(int key, byte[] value){
        data.put(key, value);
    }

    public void putData(int key, String value){
        putData(key, value.getBytes());
    }

    public HashMap<Integer, byte[]> getData() {
        return data;
    }

    public void removeData(int key){
        data.remove(key);
    }
}
