package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.server.CentralIndexingServer;
import com.utility.ConstantsUtil;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CentralIndexingServerTest {
    @Test
    public static void clientIndexingServer() {
        boolean exception = false;
        try{
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.
                    parseInt(ConstantsUtil.PORT), ConstantsUtil.CENTRAL_INDEXING_SERVER);
        }
        catch (Exception ex) {
            exception = true;
            System.err.println("EXCEPTION: CentralServer Exception while creating server: " + ex);
            ex.printStackTrace();
        }
        assertEquals(false, exception);
    }
}
