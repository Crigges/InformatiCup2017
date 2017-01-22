package systems.crigges.informaticup.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class ValueTreeMap<K, V extends Comparable<V>> implements Iterable<V> {
	private TreeSet<V> tree = new TreeSet<V>();
	private HashMap<K, V> map = new HashMap<K, V>();
	
	public void put(K key, V value){
		V oldValue = map.get(key);
		if(oldValue != null){
			tree.remove(oldValue);
		}
		tree.add(value);
		map.put(key, value);
	}
	
	public V get(K key){
		return map.get(key);
	}

	@Override
	public Iterator<V> iterator() {
		return tree.iterator();
	}
	
	public TreeSet<V> getBackingTree(){
		return tree;
	}

	public void clear() {
		tree.clear();
		map.clear();
	}
	
	public void remove(V obj) {
		map.remove(obj);
		tree.remove(obj);
	}

}
