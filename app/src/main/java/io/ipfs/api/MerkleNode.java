package io.ipfs.api;

import android.os.Build;
import com.annimon.stream.Optional;
import io.ipfs.multihash.Multihash;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MerkleNode {

    public final Multihash hash;
    public final Optional<String> name;
    public final Optional<Integer> size;
    public final Optional<String> largeSize;
    public final Optional<Integer> type;
    public final List<MerkleNode> links;
    public final Optional<byte[]> data;

    public MerkleNode(String hash, Optional<String> name, Optional<Integer> size, Optional<String> largeSize, Optional<Integer> type,
            List<MerkleNode> links, Optional<byte[]> data) {
        this.name = name;
        this.hash = Cid.decode(hash);
        this.size = size;
        this.largeSize = largeSize;
        this.type = type;
        this.links = links;
        this.data = data;
    }

    public MerkleNode(String hash) {
        this(hash, Optional.empty());
    }

    public MerkleNode(String hash, Optional<String> name) {
        this(hash, name, Optional.empty(), Optional.empty(), Optional.empty(), Arrays.asList(), Optional.empty());
    }

    @Override public boolean equals(Object b) {
        if (!(b instanceof MerkleNode)) return false;
        MerkleNode other = (MerkleNode) b;
        return hash.equals(other.hash); // ignore name hash says it all
    }

    @Override public int hashCode() {
        return hash.hashCode();
    }

    public static MerkleNode fromJSON(Object rawjson) {
        if (rawjson instanceof String) return new MerkleNode((String) rawjson);
        Map json = (Map) rawjson;
        String hash = (String) json.get("Hash");
        if (hash == null) hash = (String) json.get("Key");
        Optional<String> name = json.containsKey("Name") ? Optional.of((String) json.get("Name")) : Optional.empty();
        Object rawSize = json.get("Size");
        Optional<Integer> size = rawSize instanceof Integer ? Optional.of((Integer) rawSize) : Optional.empty();
        Optional<String> largeSize = rawSize instanceof String ? Optional.of((String) json.get("Size")) : Optional.empty();
        if ("error".equals(json.get("Type"))) throw new IllegalStateException("Remote IPFS error: " + json.get("Message"));
        Optional<Integer> type = json.containsKey("Type") ? Optional.of((Integer) json.get("Type")) : Optional.empty();
        List<Object> linksRaw = (List<Object>) json.get("Links");
        List<MerkleNode> links;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            links = linksRaw == null ? Collections.emptyList() : linksRaw.stream().map(x -> MerkleNode.fromJSON(x)).collect(Collectors.toList());
        } else {
            if (linksRaw != null) {
                List<MerkleNode> list = new ArrayList<>();
                for (Object o : linksRaw) {
                    list.add(MerkleNode.fromJSON(o));
                }
                links = list;
            } else {
                links = Collections.emptyList();
            }
        }
        Optional<byte[]> data = json.containsKey("Data") ? Optional.of(((String) json.get("Data")).getBytes()) : Optional.empty();
        return new MerkleNode(hash, name, size, largeSize, type, links, data);
    }

    public Object toJSON() {
        Map<String, Object> res = new TreeMap<>();
        res.put("Hash", hash);
        List<Multihash> list = new ArrayList<>();
        for (MerkleNode merkleNode : links) {
            list.add(merkleNode.hash);
        }
        res.put("Links", list);
        data.ifPresent(bytes -> res.put("Data", bytes));
        name.ifPresent(s -> res.put("Name", s));
        if (size.isPresent()) {
            res.put("Size", size.get());
        } else {
            largeSize.ifPresent(s -> res.put("Size", s));
        }
        type.ifPresent(integer -> res.put("Type", integer));
        return res;
    }

    public String toJSONString() {
        return JSONParser.toString(toJSON());
    }
}
