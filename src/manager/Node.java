package manager;

public class Node <T> {
    public T data;
    public Node<T> next;
    public Node<T> previous;

    public Node(Node<T> previous, T data, Node <T> next) {
        this.data = data;
        this.next = next ;
        this.previous = previous;
    }
}
