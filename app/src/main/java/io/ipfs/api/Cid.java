package io.ipfs.api;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.annimon.stream.Stream;
import io.ipfs.multibase.Multibase;
import io.ipfs.multibase.Multibase.Base;
import io.ipfs.multihash.Multihash;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Cid extends Multihash {
    public final long version;
    public final Codec codec;
    private static final int MAX_VARINT_LEN64 = 10;

    public Cid(long version, Codec codec, Multihash hash) {
        super(hash);
        this.version = version;
        this.codec = codec;
    }

    public Cid(long version, Codec codec, Type type, byte[] hash) {
        super(type, hash);
        this.version = version;
        this.codec = codec;
    }

    public Cid(Multihash h) {
        this(0L, Codec.DagProtobuf, h);
    }

    private byte[] toBytesV0() {
        return super.toBytes();
    }

    private byte[] toBytesV1() {
        byte[] hashBytes = super.toBytes();
        byte[] res = new byte[20 + hashBytes.length];
        int index = putUvarint(res, 0, this.version);
        index = putUvarint(res, index, this.codec.type);
        System.arraycopy(hashBytes, 0, res, index, hashBytes.length);
        return Arrays.copyOfRange(res, 0, index + hashBytes.length);
    }

    public byte[] toBytes() {
        if (this.version == 0L) {
            return this.toBytesV0();
        } else if (this.version == 1L) {
            return this.toBytesV1();
        } else {
            throw new IllegalStateException("Unknown cid version: " + this.version);
        }
    }

    public String toString() {
        if (this.version == 0L) {
            return super.toString();
        } else if (this.version == 1L) {
            return Multibase.encode(Base.Base58BTC, this.toBytesV1());
        } else {
            throw new IllegalStateException("Unknown Cid version: " + this.version);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Multihash)) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else if (o instanceof Cid) {
            Cid cid = (Cid)o;
            if (this.version != cid.version) {
                return false;
            } else {
                return this.codec == cid.codec;
            }
        } else {
            return this.version == 0L && super.equals(o);
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        if (this.version == 0L) {
            return result;
        } else {
            result = 31 * result + (int)(this.version ^ this.version >>> 32);
            result = 31 * result + (this.codec != null ? this.codec.hashCode() : 0);
            return result;
        }
    }

    public static Cid buildCidV0(Multihash h) {
        return new Cid(h);
    }

    public static Cid buildCidV1(Codec c, Type type, byte[] hash) {
        return new Cid(1L, c, type, hash);
    }

    public static Cid decode(String v) {
        if (v.length() < 2) {
            throw new IllegalStateException("Cid too short!");
        } else if (v.length() == 46 && v.startsWith("Qm")) {
            return buildCidV0(Multihash.fromBase58(v));
        } else {
            byte[] data = Multibase.decode(v);
            return cast(data);
        }
    }

    public static Cid cast(byte[] data) {
        if (data.length == 34 && data[0] == 18 && data[1] == 32) {
            return buildCidV0(new Multihash(data));
        } else {
            ByteArrayInputStream in = new ByteArrayInputStream(data);

            try {
                long version = readVarint(in);
                if (version != 0L && version != 1L) {
                    throw new CidEncodingException("Invalid Cid version number: " + version);
                } else {
                    long codec = readVarint(in);
                    if (version != 0L && version != 1L) {
                        throw new CidEncodingException("Invalid Cid version number: " + version);
                    } else {
                        Multihash hash = Multihash.deserialize(new DataInputStream(in));
                        return new Cid(version, Codec.lookup(codec), hash);
                    }
                }
            } catch (IOException var7) {
                throw new CidEncodingException("Invalid cid bytes: " + (String) Stream.of(data).map((b) -> {
                    return String.format("%02x", b);
                }).reduce("", (a, b) -> {
                    return a + b;
                }));
            }
        }
    }

    private static long readVarint(InputStream in) throws IOException {
        long x = 0L;
        int s = 0;

        for(int i = 0; i < 10; ++i) {
            int b = in.read();
            if (b == -1) {
                throw new EOFException();
            }

            if (b < 128) {
                if (i <= 9 && (i != 9 || b <= 1)) {
                    return x | (long)b << s;
                }

                throw new IllegalStateException("Overflow reading varint" + -(i + 1));
            }

            x |= ((long)b & 127L) << s;
            s += 7;
        }

        throw new IllegalStateException("Varint too long!");
    }

    private static int putUvarint(byte[] buf, int index, long x) {
        while(x >= 128L) {
            buf[index] = (byte)((int)(x | 128L));
            x >>= 7;
            ++index;
        }

        buf[index] = (byte)((int)x);
        return index + 1;
    }

    public static enum Codec {
        Raw(85L),
        DagProtobuf(112L),
        DagCbor(113L),
        EthereumBlock(144L),
        EthereumTx(145L),
        BitcoinBlock(176L),
        BitcoinTx(177L),
        ZcashBlock(192L),
        ZcashTx(193L);

        public long type;
        private static Map<Long, Codec> lookup = new TreeMap();

        private Codec(long type) {
            this.type = type;
        }

        public static Codec lookup(long c) {
            if (!lookup.containsKey(c)) {
                throw new IllegalStateException("Unknown Codec type: " + c);
            } else {
                return lookup.get(c);
            }
        }

        static {
            Codec[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                Codec c = var0[var2];
                lookup.put(c.type, c);
            }

        }
    }

    public static final class CidEncodingException extends RuntimeException {
        public CidEncodingException(String message) {
            super(message);
        }
    }
}
