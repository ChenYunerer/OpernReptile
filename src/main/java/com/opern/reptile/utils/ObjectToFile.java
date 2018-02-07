package com.opern.reptile.utils;

import java.io.*;

public class ObjectToFile {


    public void saveObjToFile(Object object) {
        try {
            //写对象流的对象
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(""));

            oos.writeObject(object);                 //将Person对象p写入到oos中

            oos.close();                        //关闭文件流
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * 从文件中读出对象，并且返回Person对象
     */
    public Object getObjFromFile() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(""));

            Object object = ois.readObject();              //读出对象

            return object;                                       //返回对象
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
