package feature.jfr.streaming;

import jdk.jfr.consumer.RecordingStream;

public class D_InProcessStreaming implements Runnable {

    public static void main(String[] args) {
        new Thread(new D_InProcessStreaming()).start();
        A_TestApp.main(null);
    }

    @Override
    public void run() {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable("jdk.GarbageCollection");
            stream.onEvent("jdk.GarbageCollection", System.out::println);
            stream.start();
        }
    }

}
