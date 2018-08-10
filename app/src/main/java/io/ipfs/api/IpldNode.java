package io.ipfs.api;

import android.os.Build;
import io.ipfs.api.cbor.CborObject;
import io.ipfs.api.cbor.Cborable;
import io.ipfs.multihash.Multihash;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.dom.Entity;

public interface IpldNode extends Cborable {

    Pair<IpldNode, List<String>> resolve(List<String> path);

    /**
     * Lists all paths within the object under 'path', and up to the given depth.
     * To list the entire object (similar to `find .`) pass "" and -1
     */
    List<String> tree(String path, int depth);

    /**
     * @return calculate this objects Cid
     */
    default Cid cid() {
        byte[] raw = rawData();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(raw);
            byte[] digest = md.digest();
            Multihash h = new Multihash(Multihash.Type.sha2_256, digest);
            return new Cid(1, Cid.Codec.DagCbor, h);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @return size of this object when encoded
     */
    long size();

    /**
     * @return this object's serialization
     */
    byte[] rawData();

    /**
     * @return the merkle links from this object
     */
    List<Link> getLinks();

    static IpldNode fromCBOR(CborObject cbor) {
        return new CborIpldNode(cbor);
    }

    static IpldNode fromJSON(Object json) {
        return new JsonIpldNode(json);
    }

    class CborIpldNode implements IpldNode {
        private final CborObject base;

        public CborIpldNode(CborObject base) {
            this.base = base;
        }

        @Override public CborObject toCbor() {
            return base;
        }

        @Override public Pair<IpldNode, List<String>> resolve(List<String> path) {
            throw new IllegalStateException("Unimplemented!");
        }

        @Override public List<String> tree(String path, int depth) {
            return tree(base, path, depth);
        }

        private List<String> tree(CborObject base, String rawPath, int depth) {
            String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;

            if (depth == 0 || (path.equals("") && depth != -1)) return Collections.singletonList("");

            if (base instanceof CborObject.CborMap) {

                Set<Map.Entry<CborObject, CborObject>> set = ((CborObject.CborMap) base).values.entrySet();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return set.stream().flatMap(e -> {
                        String name = ((CborObject.CborString) e.getKey()).value;
                        if (path.startsWith(name) || depth == -1) {
                            return tree(e.getValue(), path.length() > 0 ? path.substring(name.length()) : path, depth == -1 ? -1 : depth - 1).stream()
                                    .map(p -> "/" + name + p);
                        }
                        return Stream.empty();
                    }).collect(Collectors.toList());
                } else {
                    List<String> list = new ArrayList<>();
                    for (Map.Entry<CborObject, CborObject> entry : set) {
                        if (path.startsWith(((CborObject.CborString) entry.getKey()).value) || depth == -1) {
                            for (String s : tree(entry.getValue(),
                                    path.length() > 0 ? path.substring(((CborObject.CborString) entry.getKey()).value.length()) : path,
                                    depth == -1 ? -1 : depth - 1)) {
                                list.add("/" + ((CborObject.CborString) entry.getKey()).value + s);
                            }
                        }
                    }
                    return list;
                }
            }
            if (depth == -1) return Collections.singletonList("");
            return Collections.emptyList();
        }

        @Override public long size() {
            return rawData().length;
        }

        @Override public byte[] rawData() {
            return base.toByteArray();
        }

        @Override public List<Link> getLinks() {
            return getLinks(base);
        }

        private static List<Link> getLinks(CborObject base) {
            if (base instanceof CborObject.CborMerkleLink) {
                return Collections.singletonList(new Link("", 0, ((CborObject.CborMerkleLink) base).target));
            }
            if (base instanceof CborObject.CborMap) {
                SortedMap<CborObject, CborObject> sortedMap = ((CborObject.CborMap) base).values;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return sortedMap.values().stream().flatMap(cbor -> getLinks(cbor).stream()).collect(Collectors.toList());
                } else {

                    List<Link> linkList = new ArrayList<>();
                    for (SortedMap.Entry<CborObject, CborObject> s : sortedMap.entrySet()) {
                        linkList.addAll(getLinks(s.getValue()));
                    }
                    return linkList;
                }
            }
            if (base instanceof CborObject.CborList) {
                List<CborObject> list = ((CborObject.CborList) base).value;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return list.stream().flatMap(cbor -> getLinks(cbor).stream()).collect(Collectors.toList());
                } else {
                    List<Link> linkList = new ArrayList<>();
                    for (CborObject cborObject : list) {
                        linkList.addAll(getLinks(cborObject));
                    }
                    return linkList;
                }
            }
            return Collections.emptyList();
        }
    }

    class JsonIpldNode implements IpldNode {
        private final Object json;

        public JsonIpldNode(Object json) {
            this.json = json;
        }

        @Override public CborObject toCbor() {
            throw new IllegalStateException("Unimplemented!");
        }

        @Override public Pair<IpldNode, List<String>> resolve(List<String> path) {
            throw new IllegalStateException("Unimplemented!");
        }

        @Override public List<String> tree(String path, int depth) {
            throw new IllegalStateException("Unimplemented!");
        }

        @Override public long size() {
            return rawData().length;
        }

        @Override public byte[] rawData() {
            return JSONParser.toString(json).getBytes();
        }

        @Override public List<Link> getLinks() {
            throw new IllegalStateException("Unimplemented!");
        }
    }

    class Link {
        public final String name;
        // Cumulative size of target
        public final long size;
        public final Multihash target;

        public Link(String name, long size, Multihash target) {
            this.name = name;
            this.size = size;
            this.target = target;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Link link = (Link) o;

            if (size != link.size) return false;
            if (name != null ? !name.equals(link.name) : link.name != null) return false;
            return target != null ? target.equals(link.target) : link.target == null;
        }

        @Override public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (int) (size ^ (size >>> 32));
            result = 31 * result + (target != null ? target.hashCode() : 0);
            return result;
        }
    }
}
