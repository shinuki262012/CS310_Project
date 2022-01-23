
import javax.swing.plaf.synth.SynthSpinnerUI;

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

    public void toString(String prefix, boolean isLeft) {
        if (this.left != null) {
            if (this.right != null) {
                System.out.println(prefix + (isLeft ? "|--" : "`--") + value);
                left.toString(prefix + (isLeft ? "| " : "  "), true);
                right.toString(prefix + (isLeft ? "| " : "  "), false);
            } else {
                System.out.println(prefix + (isLeft ? "|--" : "`--") + value);
                left.toString(prefix + (isLeft ? "| " : "  "), true);
            }
        } else {
            if (String.valueOf(value).equals("\n"))
                System.out.println(prefix + (isLeft ? "|____" : "`--") + "\\n");
            else if (String.valueOf(value).equals(" "))
                System.out.println(prefix + (isLeft ? "|____" : "`--") + "' '");
            else
                System.out.println(prefix + (isLeft ? "|____" : "`--") + value);
        }
    }
}