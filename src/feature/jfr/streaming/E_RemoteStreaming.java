package feature.jfr.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import jdk.management.jfr.RemoteRecordingStream;

public class E_RemoteStreaming implements Runnable {

    public static void main(String[] args) throws IOException {
        spawnJavaProcessStartingClass("feature.jfr.streaming.A_TestApp");
        pause(1);
        new Thread(new E_RemoteStreaming()).start();
    }

    public static void spawnJavaProcessStartingClass(String classname) {
        new Thread(
                () -> {
                    try {executeCommand(classname);} catch (IOException e) {e.printStackTrace();}
                    }
                ).start();
    }

    @Override
    public void run() {
        try {
            String host = "localhost";
            int port = 9010;
            String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

            JMXServiceURL u = new JMXServiceURL(url);
            JMXConnector c = JMXConnectorFactory.connect(u);
            MBeanServerConnection connection = c.getMBeanServerConnection();

            try (RemoteRecordingStream stream = new RemoteRecordingStream(connection)) {
                stream.enable("jdk.GarbageCollection");
                stream.onEvent("jdk.GarbageCollection", System.out::println);
                stream.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeCommand(String classname) throws IOException {
        String classpath = System.getProperty("java.class.path");
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-classpath", classpath, 
                "-Dcom.sun.management.jmxremote", 
                "-Dcom.sun.management.jmxremote.port=9010", 
                "-Dcom.sun.management.jmxremote.authenticate=false", 
                "-Dcom.sun.management.jmxremote.ssl=false",
                "-Djava.rmi.server.hostname=0.0.0.0",
                classname);
        
        pb.redirectErrorStream(true);
        Process p = pb.start();

        StringBuilder commandOutput = new StringBuilder();

        boolean isAlive = true;
        byte[] buffer = new byte[64 * 1000];
        try (InputStream in = p.getInputStream()) {
            //stop trying if the time elapsed exceeds the timeout
            while (isAlive) {
                while (in.available() > 0) {
                    int lengthRead = in.read(buffer, 0, buffer.length);
                    commandOutput.append(new String(buffer, 0, lengthRead));
                    System.out.print(commandOutput);
                    commandOutput.setLength(0);
                }
                pause(1);
                //if it's not alive but there is still readable input, then continue reading
                isAlive = p.isAlive() || in.available() > 0;
            }
        }

        //Cleanup as well as I can
        boolean exited = false;
        try {exited = p.waitFor(3, TimeUnit.SECONDS);}catch (InterruptedException e) {}
        if (!exited) {
            p.destroy();
            pause(1);
            if (p.isAlive()) {
                p.destroyForcibly();
            }
        }
        System.out.print(commandOutput);
    }

    public static void pause(int seconds) {
        //can be interrupted, so might not be "seconds" long
        try {Thread.sleep(seconds*1000L);} catch (InterruptedException e) {}
    }


}
