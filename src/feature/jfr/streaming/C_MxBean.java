package feature.jfr.streaming;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Date;

public class C_MxBean implements Runnable {

    public static void main(String[] args) {
        new Thread(new C_MxBean()).start();
        A_TestApp.main(null);
    }

    public void run() {
        String last=null;
        while(true) {
            String next = getGcTime();
            if (!next.equals(last)) {
                System.out.println(new Date() + " " + next);
            }
            last = next;
            try{Thread.sleep(500L);}catch (Exception e) {};
        }
    }

    public static String getGcTime() {
        StringBuilder sb = new StringBuilder();
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            sb.append(bean.getName());
            sb.append(": Count: ");
            sb.append(bean.getCollectionCount());
            sb.append(" Time: ");
            sb.append(bean.getCollectionTime());
            sb.append(" / ");
        }

        return sb.toString();
    }
}
