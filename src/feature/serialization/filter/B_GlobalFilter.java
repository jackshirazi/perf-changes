package feature.serialization.filter;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Status;

public class B_GlobalFilter {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        //same as -Djdk.serialFilter=maxbytes=1000
        //static one off setting at startup, JEP 290 from Java 9
        //also allowed per-stream, but not in between, ie not context
        ObjectInputFilter.Config.setSerialFilter(info -> info.streamBytes() > 1000 ? Status.REJECTED : Status.UNDECIDED);
        A_Serialize.main(null);
    }

}
