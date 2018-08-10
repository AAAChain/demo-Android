package io.ipfs.api;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import io.ipfs.multihash.Multihash;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.HashMap;
import java.util.Map;

public class Protocol {
    public static int LENGTH_PREFIXED_VAR_SIZE = -1;
    private static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
    public final Type type;
    private static Map<String, Protocol> byName = new HashMap();
    private static Map<Integer, Protocol> byCode = new HashMap();

    public Protocol(Type type) {
        this.type = type;
    }

    public void appendCode(OutputStream out) throws IOException {
        out.write(this.type.encoded);
    }

    public int size() {
        return this.type.size;
    }

    public String name() {
        return this.type.name;
    }

    public int code() {
        return this.type.code;
    }

    public String toString() {
        return this.name();
    }

    public byte[] addressToBytes(String addr) {
        try {
            switch(this.type) {
                case IP4:
                    if (!addr.matches("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z")) {
                        throw new IllegalStateException("Invalid IPv4 address: " + addr);
                    }

                    return Inet4Address.getByName(addr).getAddress();
                case IP6:
                    return Inet6Address.getByName(addr).getAddress();
                case TCP:
                case UDP:
                case DCCP:
                case SCTP:
                    int x = Integer.parseInt(addr);
                    if (x > 65535) {
                        throw new IllegalStateException("Failed to parse " + this.type.name + " address " + addr + " (> 65535");
                    }

                    return new byte[]{(byte)(x >> 8), (byte)x};
                case IPFS:
                    Multihash hash = Cid.decode(addr);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    byte[] hashBytes = hash.toBytes();
                    byte[] varint = new byte[(32 - Integer.numberOfLeadingZeros(hashBytes.length) + 6) / 7];
                    putUvarint(varint, (long)hashBytes.length);
                    bout.write(varint);
                    bout.write(hashBytes);
                    return bout.toByteArray();
                case ONION:
                    String[] split = addr.split(":");
                    if (split.length != 2) {
                        throw new IllegalStateException("Onion address needs a port: " + addr);
                    }

                    if (split[0].length() != 16) {
                        throw new IllegalStateException("failed to parse " + this.name() + " addr: " + addr + " not a Tor onion address.");
                    }

                    byte[] onionHostBytes = Base32.decode(split[0].toUpperCase());
                    int port = Integer.parseInt(split[1]);
                    if (port > 65535) {
                        throw new IllegalStateException("Port is > 65535: " + port);
                    }

                    if (port < 1) {
                        throw new IllegalStateException("Port is < 1: " + port);
                    }

                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream dout = new DataOutputStream(b);
                    dout.write(onionHostBytes);
                    dout.writeShort(port);
                    dout.flush();
                    return b.toByteArray();
            }
        } catch (IOException var12) {
            throw new RuntimeException(var12);
        }

        throw new IllegalStateException("Failed to parse address: " + addr);
    }

    public String readAddress(InputStream in) throws IOException {
        int sizeForAddress = this.sizeForAddress(in);
        byte[] buf;
        switch(this.type) {
            case IP4:
                buf = new byte[sizeForAddress];
                in.read(buf);
                return Inet4Address.getByAddress(buf).toString().substring(1);
            case IP6:
                buf = new byte[sizeForAddress];
                in.read(buf);
                return Inet6Address.getByAddress(buf).toString().substring(1);
            case TCP:
            case UDP:
            case DCCP:
            case SCTP:
                return Integer.toString(in.read() << 8 | in.read());
            case IPFS:
                buf = new byte[sizeForAddress];
                in.read(buf);
                return Cid.cast(buf).toString();
            case ONION:
                byte[] host = new byte[10];
                in.read(host);
                String port = Integer.toString(in.read() << 8 | in.read());
                return Base32.encode(host) + ":" + port;
            default:
                throw new IllegalStateException("Unimplemented protocl type: " + this.type.name);
        }
    }

    public int sizeForAddress(InputStream in) throws IOException {
        if (this.type.size > 0) {
            return this.type.size / 8;
        } else {
            return this.type.size == 0 ? 0 : (int)readVarint(in);
        }
    }

    static int putUvarint(byte[] buf, long x) {
        int i;
        for(i = 0; x >= 128L; ++i) {
            buf[i] = (byte)((int)(x | 128L));
            x >>= 7;
        }

        buf[i] = (byte)((int)x);
        return i + 1;
    }

    static long readVarint(InputStream in) throws IOException {
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

    public static Protocol get(String name) {
        if (byName.containsKey(name)) {
            return (Protocol)byName.get(name);
        } else {
            throw new IllegalStateException("No protocol with name: " + name);
        }
    }

    public static Protocol get(int code) {
        if (byCode.containsKey(code)) {
            return (Protocol)byCode.get(code);
        } else {
            throw new IllegalStateException("No protocol with code: " + code);
        }
    }

    static {
        Type[] var0 = Type.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            Type t = var0[var2];
            Protocol p = new Protocol(t);
            byName.put(p.name(), p);
            byCode.put(p.code(), p);
        }

    }

    static enum Type {
        IP4(4, 32, "ip4"),
        TCP(6, 16, "tcp"),
        UDP(17, 16, "udp"),
        DCCP(33, 16, "dccp"),
        IP6(41, 128, "ip6"),
        SCTP(132, 16, "sctp"),
        UTP(301, 0, "utp"),
        UDT(302, 0, "udt"),
        IPFS(421, Protocol.LENGTH_PREFIXED_VAR_SIZE, "ipfs"),
        HTTPS(443, 0, "https"),
        HTTP(480, 0, "http"),
        ONION(444, 80, "onion");

        public final int code;
        public final int size;
        public final String name;
        private final byte[] encoded;

        private Type(int code, int size, String name) {
            this.code = code;
            this.size = size;
            this.name = name;
            this.encoded = encode(code);
        }

        static byte[] encode(int code) {
            byte[] varint = new byte[(32 - Integer.numberOfLeadingZeros(code) + 6) / 7];
            Protocol.putUvarint(varint, (long)code);
            return varint;
        }
    }
}
