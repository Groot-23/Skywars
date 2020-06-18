package me.groot_23.skywars.util;

public class Pair<K, V> implements Comparable<Pair<K, V>> {

    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }

	@Override
	public int compareTo(Pair<K, V> other) {
		if(element0 instanceof Comparable<?>) {
			Comparable<K> com0 = (Comparable<K>) element0;
			if(com0.compareTo(other.element0) != 0) {
				return com0.compareTo(other.element0);
			}
			return ((Comparable<V>)element1).compareTo(other.element1);
		}
		return 0;
	}

}
