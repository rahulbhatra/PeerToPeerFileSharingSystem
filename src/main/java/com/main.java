package com;

import com.test.CentralIndexingServerTest;
import com.test.ClientServerTest;
import com.test.PeerTest;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;

import static com.utility.FileUtil.createFile;

public class main {
    public static void main(String[] args) {
//        CentralIndexingServerTest.clientIndexingServer();
//        ClientServerTest.clientServerTest();
//        PeerTest.parallelPeerTesting();
                FileUtil.createFile(1, "abcd", ConstantsUtil.shared, 5);
    }
}
