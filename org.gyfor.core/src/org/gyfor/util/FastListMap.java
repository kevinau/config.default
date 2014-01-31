/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** An implementation of Map specifically designed for 
 *  when there are a small number of entries which
 *  are often added and removed.  For example, tracking 
 *  errors associated with data entry fields.  It has 
 *  a very small footprint when it contains no entries.
 *  <p>
 *  The methods returning sets and collections have not
 *  been implemented.  Use the implementation specific
 *  method "iterator" instead.
 */
public class FastListMap<K, V> implements Map<K, V> {

  private static class ListMapNode<K, V> implements Map.Entry<K, V> {
    ListMapNode<K, V> next = null;
    K key;
    V value;
  
    ListMapNode (K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return key;
    }

    public V setValue (V value) {
      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public V getValue() {
      return value;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals (Object obj) {
      if (obj == null || !(obj instanceof ListMapNode)) {
        return false;
      }
      ListMapNode<K, V> x = (ListMapNode<K, V>)obj;
      return this.key.equals(x.key) && this.value.equals(x.value);
    }
    
    @Override
    public int hashCode () {
      return key.hashCode() + value.hashCode();
    }
    
    @Override
    public String toString() {
      return "ListMapNode[" + key + "," + value + "]";
    }
  }
  
  
  ListMapNode<K, V> nodeList = null;


  public void clear() {
    nodeList = null;
  }


  public boolean containsKey(Object key) {
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      if (node.key.equals(key)) {
        return true;
      }
    }
    return false;
  }


  public boolean containsValue(Object value) {
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      if (node.value.equals(value)) {
        return true;
      }
    }
    return false;
  }


  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> set = new HashSet<Map.Entry<K, V>>();
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      set.add(node);
    }
    return set;
  }


  public V get(Object key) {
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      if (node.key.equals(key)) {
        return node.value;
      }
    }
    return null;
  }


  public boolean isEmpty() {
    return nodeList == null;
  }


  public Set<K> keySet() {
    throw new UnsupportedOperationException();
  }


  public V put(K key, V value) {
    if (nodeList == null) {
      nodeList = new ListMapNode<K, V>(key, value);
      return null;
    } else {
      ListMapNode<K, V> node = nodeList;
      for (;;) {
        if (node.key.equals(key)) {
          V oldValue = node.value;
          node.value = value;
          return oldValue;
        }
        if (node.next == null) {
          node.next = new ListMapNode<K, V>(key, value);
          return null;
        }
        node = node.next;
      }
    }
  }


  public void putAll(Map<? extends K, ? extends V> map) {
    Iterator<? extends K> keyIterator = map.keySet().iterator();
    while (keyIterator.hasNext()) {
      K key = keyIterator.next();
      put(key, map.get(key));
    }
  }


  public V remove(Object key) {
    ListMapNode<K, V> lastNode = null;
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      if (node.key.equals(key)) {
        if (lastNode == null) {
          nodeList = node.next;
        } else {
          lastNode.next = node.next;
        }
        return node.value;
      }
      lastNode = node;
    }
    return null;
  }


  public int size() {
    int n = 0;
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      n++;
    }
    return n;      
  }

  
  @Override
  public int hashCode () {
    int hash = 0;
    for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
      hash += node.hashCode();
    }
    return hash;
  }
  

  @Override
  public boolean equals (Object obj) {
    throw new UnsupportedOperationException();
  }


  public Collection<V> values() {
  	List<V> vx = new ArrayList<V>(size());
		for (ListMapNode<K, V> node = nodeList; node != null; node = node.next) {
			vx.add(node.value);
		}
		return vx;
  }


  public Iterator<V> iterator() {
    return new ListMapIterator();
  }
  
  
  private class ListMapIterator implements Iterator<V> {
    private ListMapNode<K, V> index = null;

    ListMapIterator() {
      index = nodeList;
    }

    public boolean hasNext() {
      return index != null;
    }

    public V next() {
      if (index != null) {
        ListMapNode<K, V> currentIndex = index;
        index = index.next;
        return currentIndex.value;
      } else {
        return null;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

//  public static void main (String[] args) {
//    FastListMap<String, String> listMap = new FastListMap<String, String>();
//    String[] keys = {"a", "b", "c", "d", "e"};
//    String[] values = {"AAA", "BBB", "CCC", "DDD", "EEE"};
//    
//    for (int i = 0; i < keys.length; i++) {
//      listMap.put(keys[i], values[i]);
//      for (Iterator<String> x = listMap.iterator(); x.hasNext(); ) {
//        System.out.println(x.next() + " " + listMap.size());
//      }
//      System.out.println();      
//    }
//
//    for (int i = 0; i < keys.length - 1; i++) {
//      String oldValue = listMap.put(keys[i], values[i + 1]);
//      System.out.println(keys[i] + " " + oldValue);
//      for (Iterator<String> x = listMap.iterator(); x.hasNext(); ) {
//        System.out.println(x.next());
//      }
//      System.out.println();      
//    }
//
//    for (int i = 0; i < keys.length; i++) {
//      Object oldValue = listMap.remove(keys[i]);
//      System.out.println(keys[i] + " " + oldValue);
//      for (Iterator<String> x = listMap.iterator(); x.hasNext(); ) {
//        System.out.println(x.next());
//      }
//      System.out.println(listMap.size() + " " + listMap.isEmpty());      
//    }
//  }
}
