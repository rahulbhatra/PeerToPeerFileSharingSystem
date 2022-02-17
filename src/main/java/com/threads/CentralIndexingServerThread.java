package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.server.CentralIndexingServer;
import com.utility.ConstantsUtil;

import java.rmi.Naming;

public class CentralIndexingServerThread extends Thread {
    @Override
    public void run() {
        try {
            System.out.println("Starting the Central Indexing Server Thread");
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(
                    Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);
            Naming.lookup(ConstantsUtil.CENTRAL_INDEXING_SERVER);
        } catch (Exception ex) {
            System.err.println("EXCEPTION: CentralServer Exception while creating server: " + ex.toString());
            ex.printStackTrace();
        }
    }
}
