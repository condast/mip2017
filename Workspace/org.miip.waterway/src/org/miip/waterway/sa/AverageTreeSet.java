package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.condast.commons.Utils;

public class AverageTreeSet<T extends Object> implements Set<T>{

	private Node<T> root;
	private Comparator<T> comparator;
	
	public AverageTreeSet() {
	}

	public AverageTreeSet( Comparator<T> comparator) {
		this();
		this.comparator = comparator;
	}
	
	public boolean add( T item ){
		if( root == null ){
			root = new Node<T>( item );
			return true;
		}else
			root.add( item );
		return true;
	}
		
	/**
	 * Get the values at the given depth of the tree
	 * @param depth
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T[] getValues( int depth ){
		Collection<T> results = new ArrayList<>();
		getValues( results, root, 0, depth );
		return (T[]) results.toArray( new Object[ results.size()]);
	}

	protected void getValues( Collection<T> results, Node<T> node, int index, int depth ){
		if( index == depth ){
			results.add(node.getValue());
		}else{
			for( Node<T> child: node.getChildren() )
				getValues( results, child, index++, depth);
		}
	}

	protected void getValues( Collection<T> results, Node<T> node ){
		results.add(node.getValue());
		for( Node<T> child: node.getChildren() )
			getValues( results, child);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for( T item: c)
			this.add(item);
		return true;
	}

	@Override
	public void clear() {
		this.root = null;
	}

	@Override
	public boolean contains(Object arg0) {
		return contains( this.root, arg0 );
	}

	protected boolean contains( Node<T> node, Object arg0 ){
		if( node.getValue().equals( arg0 ))
			return true;
		else{
			for( Node<T> child: node.getChildren() )
				if( contains( child, arg0))
					return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return containsAll( this.root, arg0 );
	}

	protected boolean containsAll( Node<T> node, Collection<?> arg0 ){
		arg0.remove( node.getValue());
		if( arg0.isEmpty())
			return true;
		else{
			for( Node<T> child: node.getChildren() )
				if( containsAll( child, arg0))
					return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return ( root == null );
	}

	@Override
	public Iterator<T> iterator() {
		Collection<T> results = new ArrayList<>();
		getValues( results, root );
		return results.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return (root == null )? false: root.remove( arg0 );
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		boolean retval = true;
		for( Object arg:arg0 )
			retval &= remove(arg);
		return retval;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		retainAll( this.root, arg0 );
		return true;
	}

	protected void retainAll( Node<T> node, Collection<?> arg0 ){
		if( !arg0.contains( node.left.getValue() ))
			remove( node.left.getValue());
		if( !arg0.contains( node.right.getValue() ))
			remove( node.right.getValue());
		for( Node<T> child: node.getChildren() )
			retainAll( child, arg0);
	}

	@Override
	public Object[] toArray() {
		Collection<T> results = new ArrayList<>();
		getValues(results, this.root);
		return results.toArray();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray( T[] arg0){
		Collection<T> results = new ArrayList<>();
		//getValues(results, this.root);
		return (T[]) results.toArray( arg0 );
	}

	public int size(){
		return root.size();
	}
	
	private static class Node<T extends Object>{
		private Node<T> left, right;
		
		private T value;
		private int size;

		private Node( T value ){
			this( value, 0 );
		}

		private Node( T value, int size ){
			this.value = value;
			this.size = size;
		}

		private boolean add(T value) {
			if (left == null) {
				left = new Node<T>( value, size++);
				size = left.size;
				return true;
			} else if( right == null ){
				right = new Node<T>( value);
				size = right.size;
				return true;
			}else{
				right.add( value);
			}
			return true;
		}	

		private boolean remove( Object arg0) {
			if (left.equals( arg0 )) {
				left = right;
				right = right.left;
				remove( right.left.getValue() );
				size--;
				return true;
			}else if( right.equals( arg0 )){
				right = right.left;
				remove( right.left.getValue() );
				size--;
				return true;
			}else{
				if( remove( left.getValue() )){
					size--;
					return true;
				}
				if( remove( right.getValue() )){
					size--;
					return true;
				}
			}
			return false;
		}	

		public T getValue() {
			return value;
		}
		
		private int size(){
			return size;
		}
		
		public Node<T>[] getChildren(){
			@SuppressWarnings("unchecked")
			Node<T>[] nodes = new Node[2];
			nodes[0] = left;
			nodes[1] = right;
			return nodes;
		}
	}
}
