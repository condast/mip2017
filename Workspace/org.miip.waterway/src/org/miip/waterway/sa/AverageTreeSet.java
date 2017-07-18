package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class AverageTreeSet<T extends Object> implements Set<T>{

	private LinkedList<Node> nodes;
	private Node root, current;
	private Comparator<T> comparator;
	
	public AverageTreeSet() {
		nodes = new LinkedList<Node>();
	}

	public AverageTreeSet( Comparator<T> comparator) {
		this();
		this.comparator = comparator;
	}
	
	public boolean add( T item ){
		Node node = new Node( item );
		this.nodes.push( node );
		if( root == null ){
			root = node;
			return true;
		}
		if( current != null ){
			updateTree(current, node);
		}else
			this.current = node;	
		return true;
	}
	
	protected void updateTree( Node last, Node newValue ){
		Node parent = new Node( last.getValue(), newValue.getValue() );
		if( !last.isRoot())
			updateTree( last.getParent(), parent );
		else
			root = parent;
	}
	
	/**
	 * Get the values at the given depth of the tree
	 * @param depth
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T[] getValues( int depth ){
		Collection<T> results = new ArrayList<>();
		getValues( results, nodes.getFirst(), 0, depth );
		return (T[]) results.toArray( new Object[ results.size()]);
	}

	protected void getValues( Collection<T> results, Node node, int index, int depth ){
		if( index == depth ){
			results.add(node.getValue());
		}else{
			for( Node child: node.children )
				getValues( results, child, index++, depth);
		}
	}

	public T getValue( int index ){
		Node node = nodes.get(index);
		return node.getValue();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for( T item: c)
			this.add(item);
		return true;
	}

	@Override
	public void clear() {
		this.nodes.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		for( Node node: nodes )
			if( node.getValue().equals(arg0))
				return true;
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		Collection<?> temp = new ArrayList<Object>(arg0);
		for( Node node: nodes ){
			if( arg0.contains( node.getValue())){
				temp.remove(arg0);
			if( temp.size() == 0)
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		Collection<T> temp = new ArrayList<T>();
		for( Node node: nodes ){
			temp.add( node.getValue());
		}
		return temp.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		Node temp = null;
		for( Node node: nodes ){
			if( node.getValue().equals(arg0)){
				temp = node;
				break;
			}
		}
		return (temp == null )? false: nodes.remove( temp );
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		Collection<Node> temp = new ArrayList<Node>();
		for( Node node: nodes ){
			if( node.getValue().equals(arg0)){
				temp.add(node );
			}
		}
		return nodes.removeAll(temp);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		Collection<Node> temp = new ArrayList<Node>();
		for( Node node: nodes ){
			if( arg0.contains( node.getValue())){
				temp.add(node );
			}
		}
		return nodes.retainAll(temp);
	}

	@Override
	public Object[] toArray() {
		Collection<T> temp = new ArrayList<>();
		for( Node node: nodes ){
			temp.add(node.getValue() );
		}
		return temp.toArray();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] arg0) {
		Collection<T> temp = new ArrayList<>();
		for( Node node: nodes ){
			temp.add((T) node.getValue() );
		}
		return (T[]) temp.toArray( new Object[ temp.size()]);
	}

	public int size(){
		return nodes.size();
	}
	
	private class Node{
		private Collection<Node> children;
		private Node parent;
		private T value;

		public Node( T value ){
			children = new ArrayList<Node>();
			this.value = value;
		}

		public Node( Node parent, T value ){
			this( value );
			this.parent = parent;
		}
				
		public Node( T first, T second) {
			children.add(new Node( this, first));
			children.add( new Node( this, second ));
		}
		
		private boolean isRoot(){
			return parent == null;
		}
		
		private Node getParent(){
			return parent;
		}
		
		public T getValue() {
			return value;
		}
	}
}
