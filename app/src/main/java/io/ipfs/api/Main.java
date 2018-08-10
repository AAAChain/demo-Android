package io.ipfs.api;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        List<String> strings = new ArrayList<>();
        strings.add("123");
        strings.add("23456");
        strings.add("3456789");

        List<String> list =Stream.of(strings).filter(id->id.equals("123")).collect(Collectors.toList());
        List<String> list1 =strings.stream().filter(id->id.equals("4")).collect(java.util.stream.Collectors.toList());
        List<String> list2 = strings.stream().filter(l->l.length()>4).collect(java.util.stream.Collectors.toList());
        List<String> list3 = Stream.of(strings).filter(l->l.length()>4).collect(Collectors.toList());
        System.out.println(list);
        System.out.println(list1);
        System.out.println(list2);
        System.out.println(list3);

        List<Integer> list4 = Stream.of(strings).map(Integer::valueOf).collect(Collectors.toList());
        System.out.println(list4);
        List<Integer> list5 = strings.stream().map(Integer::valueOf).collect(java.util.stream.Collectors.toList());
        System.out.println(list5);

        strings.add("");
        List<String> list6 = Stream.of(strings).filter(s->!s.isEmpty()).collect(Collectors.toList());
        System.out.println("list6:"+list6);

        boolean result = strings.stream().anyMatch(a->a.equals("121"));
        System.out.println(result);

        Optional<String> optional = strings.stream().findAny();
        com.annimon.stream.Optional<String> optional2 = Stream.of(strings).findFirst();
        System.out.println(optional.get());
        System.out.println(optional2);



        List<String> lists = Arrays.asList("hello welcome", "world hello", "hello world",
                "hello world welcome");

        List<java.util.stream.Stream<String>> listResult = lists.stream().map(item -> Arrays.stream(item.split(" "))).distinct().collect(java.util.stream.Collectors.toList());
        List<String> listResult2 = lists.stream().flatMap(item -> Arrays.stream(item.split(" "))).distinct().collect(java.util.stream.Collectors.toList());
        System.out.println(listResult);
        System.out.println(listResult2);
    }
}
