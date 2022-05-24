package feature.jfr.streaming;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class A_TestApp {

    private static LongAdder TotalCreated = new LongAdder();

    @SuppressWarnings("unused")
    private List<Object> justMemoryUsed = new ArrayList<>(100);
    @SuppressWarnings("unused")
    private Map<Object,Object> moreMemoryUsed = new HashMap<>();

    public static void main(String[] args) {
        try {Thread.sleep(1000L);} catch (InterruptedException e) {}
        DecimalFormat formatter = new DecimalFormat();
        long start = System.nanoTime();
        int stat = 0;
        for (;;) {
            new A_TestApp();
            if (System.nanoTime() - start > 2_000_000_000L) {
                start = System.nanoTime();
                System.out.println("-------------------------" + (++stat)+". Total TestApp instances created since start: "+formatter.format(TotalCreated.longValue()));
            }
        }
    }

    public A_TestApp() {
        TotalCreated.increment();
        if (TotalCreated.longValue() % 1000 == 0) {
            //throttle object creation
            try {Thread.sleep(5L);} catch (InterruptedException e) {}
        }
    }

}
