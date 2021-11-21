public class BinaryTreeNode<E extends Comparable<E>> {

    private E value;
    private BinaryTreeNode<E> left;
    private BinaryTreeNode<E> right;

    public BinaryTreeNode(E val) {
        value = val;
        left = null;
        right = null;
    }

    public BinaryTreeNode(E val, BinaryTreeNode<E> left, BinaryTreeNode<E> right) {
        this.value = val;
        this.left = left;
        this.right = right;
    }

    public E getValue() {
        return value;
    }

    public BinaryTreeNode<E> getLeft() {
        return left;
    }

    public BinaryTreeNode<E> getRight() {
        return right;
    }

    public void setValue(E v) {
        value = v;
    }

    public void setLeft(BinaryTreeNode<E> p) {
        left = p;
    }

    public void setRight(BinaryTreeNode<E> p) {
        right = p;
    }

}