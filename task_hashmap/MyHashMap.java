package org.example;

public class MyHashMap <K,V>{
    private static class Node<K,V>{
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        Node(int hash, K key, V value, Node<K, V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAX_CAPACITY = 1 << 30;
    private Node<K,V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public MyHashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Ёмкость должна быть > 0: " + initialCapacity);
        }
        int capacity = tableSizeFor(initialCapacity);   
        table = (Node<K, V>[]) new Node[capacity];
        threshold = (int) (capacity * LOAD_FACTOR);
    }
    private int hash(K key){
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    private int indexFor(int hash, int capacity){
        return hash & (capacity-1);
    }
    private static int tableSizeFor(int cap){
        int n = -1>>> Integer.numberOfLeadingZeros(cap-1);
        return (n >= MAX_CAPACITY) ? MAX_CAPACITY : n+1;
    }
    public V put(K key, V value){
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for(Node<K,V> node = table[index];node != null; node = node.next){
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))){
                V oldValue = node.value;
                node.value = value;
                return oldValue;

            }
        }
        table[index] = new Node<>(hash, key, value, table[index]);
        if(++size > threshold){
            resize();
        }
        return null;
    }
    public V get(K key){
        Node<K,V> node = findNode(key);
        return node == null ? null: node.value;
    }
    public Boolean containsKey(K key){
       return findNode(key) != null;
    }
    private Node<K,V> findNode(K key){
        int hash = hash(key);
        int index = indexFor(hash,table.length);
        for (Node<K,V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))) {
                return node;
            }
        }
        return null;
    }
    public V remove(K key){
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Node<K,V> prev = null;
        Node<K,V> node =table[index];
        while (node != null) {
            if (node.hash == hash && (node.key == key || (key != null && key.equals(node.key)))) {
                if(prev == null){
                    table[index] = node.next;
                }
                else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }

            prev = node;
            node = node.next;
        }
        return null;

    }
    private void resize(){
        int oldCapacity = table.length;
        int newCapacity = oldCapacity << 1;
        if(oldCapacity >= MAX_CAPACITY){
            threshold = Integer.MAX_VALUE;
            return;
        }
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        threshold = (int)(newCapacity*LOAD_FACTOR);
        for(int i = 0; i < oldCapacity; i++){
            Node<K,V> node = table[i];
            if(node == null){
                continue;
            }
            Node<K, V> loHead = null, loTail = null;
            Node<K, V> hiHead = null, hiTail = null;

            while (node != null) {
                Node<K, V> next = node.next;
                if ((node.hash & oldCapacity) == 0) {
                    if (loTail == null) loHead = node; else loTail.next = node;
                    loTail = node;
                } else {
                    if (hiTail == null) hiHead = node; else hiTail.next = node;
                    hiTail = node;
                }
                node = next;
            }

            if (loHead != null) {
                loTail.next = null; newTable[i] = loHead;
            }
            if (hiHead != null) {
                hiTail.next = null; newTable[i + oldCapacity] = hiHead;
            }


        }
        table = newTable;
    }
    public int size(){
        return size;
    }
    @Override
    public String toString(){
        if (size == 0){
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Node<K,V> bucket : table){
            for (Node<K, V> node = bucket; node != null; node = node.next){
                sb.append(node.key).append('=').append(node.value).append(",");
            }
        }
        return sb.substring(0,sb.length() - 2)+"}";
    }
}
