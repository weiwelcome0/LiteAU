package com.couragechallenge.liteau.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("serial")
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

	private final Locale locale;


	/**
	 * Create a new LinkedCaseInsensitiveMap for the default Locale.
	 * @see java.lang.String#toLowerCase()
	 */
	public CaseInsensitiveMap() {
		this(null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that stores lower-case keys
	 * according to the given Locale.
	 * @param locale the Locale to use for lower-case conversion
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	public CaseInsensitiveMap(Locale locale) {
		super();
		this.locale = (locale != null ? locale : Locale.getDefault());
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity and stores lower-case keys according
	 * to the default Locale.
	 * @param initialCapacity the initial capacity
	 * @see java.lang.String#toLowerCase()
	 */
	public CaseInsensitiveMap(int initialCapacity) {
		this(initialCapacity, null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity and stores lower-case keys according
	 * to the given Locale.
	 * @param initialCapacity the initial capacity
	 * @param locale the Locale to use for lower-case conversion
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	public CaseInsensitiveMap(int initialCapacity, Locale locale) {
		super(initialCapacity);
		this.locale = (locale != null ? locale : Locale.getDefault());
	}


	@Override
	public V put(String key, V value) {
		return super.put(convertKey(key), value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		if (map.isEmpty()) {
			return;
		}
		for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return (key instanceof String && super.containsKey(convertKey((String) key)));
	}

	@Override
	public V get(Object key) {
		return super.get(convertKey((String) key));
	}

	@Override
	public V remove(Object key) {
		if (key instanceof String ) {
			return super.remove(convertKey((String) key));
		}
		else {
			return null;
		}
	}

	/**
	 * Convert the given key to a case-insensitive key.
	 * <p>The default implementation converts the key
	 * to lower-case according to this Map's Locale.
	 * @param key the user-specified key
	 * @return the key to use for storing
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	protected String convertKey(String key) {
		return null==key?key:key.toLowerCase(this.locale);
	}

}
