package ru.itmo.java;


public class HashTable {

    private final double loadFactor;

    private int threshold;
    private int count;
    private int capacity;

    private Entry[] HshTable;


    private static class Entry {
        private Object key;
        private Object value;
        private boolean deleted = false;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public HashTable(int size, double loadFactor) {
        this.capacity = size;
        int twoDegreeCapacity = 1;
        while (true) {
            if (twoDegreeCapacity >= capacity) {
                capacity = twoDegreeCapacity;
                break;
            }
            twoDegreeCapacity *= 2;
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.count = 0;
        HshTable = new Entry[capacity];
    }

    public HashTable(int size) {
        this(size, 0.5);
    }

    public HashTable() {
        this(1024, 0.5);
    }

    private int HashFunc(Object key) {
        return Math.abs(key.hashCode());
    }

    private int Search(Entry[] array, Object key, boolean checkExisting) {
        int Hash = HashFunc(key);
        int searchInterval = 0;
        while (true) {
            searchInterval++;
            int currentHash = (Hash + searchInterval * searchInterval) % array.length;
            if (array[currentHash] == null) {
                if (checkExisting) {
                    return -1;
                } else {
                    return currentHash;
                }
            } else if (array[currentHash].key.equals(key)) {
                if (checkExisting) {
                    if (!array[currentHash].deleted) {
                        return currentHash;
                    } else {
                        return -1;
                    }
                } else {
                    return currentHash;
                }
            } else if (!checkExisting && array[currentHash].deleted) {
                return currentHash;
            }
        }
    }

    private void resize() {
        capacity *= 2;
        threshold = (int) (capacity * loadFactor);
        count = 0;
        Entry[] temp = new Entry[capacity];
        for (int i = 0; i < HshTable.length; ++i) {
            Entry entry = HshTable[i];
            if (entry == null || entry.deleted) {
                HshTable[i] = null;
                continue;
            }
            int index = Search(temp, entry.key, false);
            if (index != -1) {
                temp[index] = entry;
                count++;
            }
            HshTable[i] = null;
        }
        HshTable = temp;
    }

    public Object put(Object key, Object value) {
        Object returnRef = null;
        int index = Search(HshTable, key, true);
        if (index != -1) {
            returnRef = HshTable[index].value;
            HshTable[index].value = value;
        } else {
            index = Search(HshTable, key, false);
            HshTable[index] = new Entry(key, value);
            count++;
        }
        if (count >= threshold) {
            resize();
        }
        return returnRef;
    }

    public Object get(Object key) {
        int index = Search(HshTable, key, true);
        if (index != -1) {
            return HshTable[index].value;
        } else {
            return null;
        }
    }

    public Object remove(Object key) {
        int index = Search(HshTable, key, true);
        if (index != -1) {
            HshTable[index].deleted = true;
            count--;
            return HshTable[index].value;
        } else {
            return null;
        }
    }

    public int size() {
        return count;
    }
}
