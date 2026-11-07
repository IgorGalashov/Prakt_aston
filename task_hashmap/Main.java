package org.example;

public class Main {
    public static void main(String[] args) {

        MyHashMap<String, Integer> map = new MyHashMap<>();


        map.put("cat", 1);
        map.put("dog", 2);
        System.out.println("После вставки: cat=" + map.get("cat") + ", dog=" + map.get("dog"));



        System.out.println("Размер карты: " + map.size());



        Integer oldValue = map.put("cat", 99);
        System.out.println("Старое значение для cat: " + oldValue);
        System.out.println("Новое значение для cat: " + map.get("cat"));



        System.out.println("Есть ли ключ 'dog'? " + map.containsKey("dog"));
        System.out.println("Есть ли ключ 'bird'? " + map.containsKey("bird"));



        Integer removed = map.remove("dog");
        System.out.println("Удалено значение: " + removed);
        System.out.println("Теперь есть ли 'dog'? " + map.containsKey("dog"));
        System.out.println("Новый размер карты: " + map.size());


        map.put(null, 0);
        System.out.println("Значение для null-ключа: " + map.get(null));
        System.out.println("Размер с null-ключом: " + map.size());



        System.out.println("Вся карта: " + map);

    }
}
