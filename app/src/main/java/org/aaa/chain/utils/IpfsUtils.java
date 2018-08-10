package org.aaa.chain.utils;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import java.io.File;
import java.io.IOException;

public class IpfsUtils {

    private IPFS ipfs;

    private static class Lazyholder {
        private static final IpfsUtils IPFS_UTILS = new IpfsUtils();
    }

    public static IpfsUtils getInstance() {
        return Lazyholder.IPFS_UTILS;
    }

    private void initIPFS() {
        if (ipfs == null) {
            String ADDRESS = "/ip4/47.98.107.96/tcp/22222";
            ipfs = new IPFS(ADDRESS);
        }
    }

    public Multihash addFile(File file) {
        initIPFS();
        NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
        try {
            MerkleNode addResult = ipfs.add(fileWrapper).get(0);
            return addResult.hash;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Multihash addByte(String name, byte[] b) {
        try {
            initIPFS();
            NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(name, b);
            MerkleNode addResult = ipfs.add(byteArrayWrapper).get(0);
            return addResult.hash;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFile(Multihash hash) {
        try {
            byte[] fileContent = ipfs.cat(hash);
            return new String(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //public static void main(String[] args) {
    //    File file = new File("/Users/benson/Documents/workspace3/aaaChain/app/src/main/assets/package.json");
    //    try {
    //        Multihash multihash = addFile(file);
    //        byte[] fileContent = ipfs.cat(multihash);
    //        System.out.print(new String(fileContent));
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //}
}
