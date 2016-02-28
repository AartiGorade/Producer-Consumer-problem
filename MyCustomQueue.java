class Node<E>{
	E value;
	Node<E> nextNode;
	Node(E value){
		this.value=value;
		nextNode = null;
	}
}

public class MyCustomQueue<E> {
	Node<E> head;
	Node<E> last;
	int nodeCount=0;
	boolean isProcessDone=false;
	public void setProcessDone(){
		isProcessDone=true;
	}
	public boolean isProcessDone(){
		return isProcessDone;
	}
	
	public void add(E value){
		Node<E> newnode = new Node<E>(value);
		if(head == null){
			head = newnode;
			last = newnode;
			nodeCount=1;
			return;
		}
		last.nextNode= newnode;
		last = newnode;
		nodeCount++;
	}
	public E remove(){
		if(head == null){
			System.err.println("Empty queue!");
			return null;
		}
		E value = head.value;
		head= head.nextNode;
		nodeCount--;
		return value;
	}
	
	public int getCount(){
		return nodeCount;
	}

}
