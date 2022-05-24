package feature.serialization.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class A_Serialize {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        streamInstance("Small object", new SmallClass());
        streamInstance("Big object", new BigClass());
    }

    public static void streamInstance(String description, Object objToStream) throws IOException, ClassNotFoundException {
        byte[] object;
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(objToStream);
            object = boas.toByteArray();
        }

        System.out.println(description+" size: "+object.length);
        ByteArrayInputStream in = new ByteArrayInputStream(object);
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            Object objFromStream = ois.readObject();
            System.out.println(description+" : "+objFromStream);
        }
    }

    public static class SmallClass implements Serializable{
        private static final long serialVersionUID = -7512608283769938126L;
        @SuppressWarnings("unused")
        private List<Object> list = new ArrayList<>();
        @SuppressWarnings("unused")
        private Map<Object,Object> map = new HashMap<>();
        @SuppressWarnings("unused")
        private int num = 0;
    }

    public static class BigClass implements Serializable{
        private static final long serialVersionUID = 8443312696364241922L;
        private static int SIZE = 100_000;
        private ArrayList<Object> list = new ArrayList<>(SIZE);
        public BigClass() {
            for (int i = 0; i < SIZE; i++) {
                list.add(Integer.valueOf(i));
            }
        }
    }
}

