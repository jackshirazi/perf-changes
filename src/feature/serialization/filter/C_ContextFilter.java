package feature.serialization.filter;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Status;
import java.util.function.BinaryOperator;

import feature.serialization.filter.A_Serialize.BigClass;


public class C_ContextFilter {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        //JEP 415 from Java 17, context filters
        //same as -Djdk.serialFilterFactory=talk.filter.C_ContextFilter.FilterOverride
        ObjectInputFilter.Config.setSerialFilterFactory(new FilterOverride());

        B_GlobalFilter.main(null);
        A_Serialize.streamInstance("BigClass2 object", new BigClass2());
    }

    public static class FilterOverride implements BinaryOperator<ObjectInputFilter> {
        private long allowedSize = 1000;

        @Override
        public ObjectInputFilter apply(ObjectInputFilter t, ObjectInputFilter u) {
            ObjectInputFilter filter = 
                    (info) -> {
                        //only change on a new top-level object being deserialized
                        if (info.depth() == 1) {
                            allowedSize = info.serialClass() == A_Serialize.BigClass.class ?
                                           2000000 : 1000;
                        }
                        return info.streamBytes() > allowedSize ? Status.REJECTED : Status.UNDECIDED;
                    };
            return filter;
        }
    }

    public static class BigClass2 extends BigClass {
    }
}
