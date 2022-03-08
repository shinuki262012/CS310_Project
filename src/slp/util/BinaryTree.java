package slp.util;

/**
 * Represent a context free grammar in CNF.
 * 
 * @author Tianlong Zhong
 */
public class BinaryTree<E extends Comparable<E>> {
    BinaryTreeNode<E> root;

    public BinaryTree() {
        root = null;
    }

    private void addToSubTree(BinaryTreeNode<E> n, E v) {
        if (n != null) {
            E nValue = n.getValue();
            if (v.compareTo(nValue) <= 0) {
                System.out.println("Adding " + v + " to left sub-tree of " + nValue);
                if (n.getLeft() == null)
                    n.setLeft(new BinaryTreeNode<>(v));
                else
                    addToSubTree(n.getLeft(), v);
            } else {
                System.out.println("Adding " + v + " to right sub-tree of " + nValue);
                if (n.getRight() == null)
                    n.setRight(new BinaryTreeNode<>(v));
                else
                    addToSubTree(n.getRight(), v);
            }
        }
    }

    public void add(E v) {
        if (root == null) {
            System.out.println("Adding " + v + " to root.");
            root = new BinaryTreeNode<>(v);
        } else
            addToSubTree(root, v);
    }

    private void inOrder(BinaryTreeNode<E> n) {
        if (n != null) {
            inOrder(n.getLeft());
            System.out.print(((Integer) n.getValue()).intValue() + " ");
            inOrder(n.getRight());
        }
    }

    private void preOrder(BinaryTreeNode<E> n) {
        if (n != null) {
            System.out.print(((Integer) n.getValue()).intValue() + " ");
            preOrder(n.getLeft());
            preOrder(n.getRight());
        }
    }

    private void postOrder(BinaryTreeNode<E> n) {
        if (n != null) {
            postOrder(n.getLeft());
            postOrder(n.getRight());
            System.out.print(((Integer) n.getValue()).intValue() + " ");
        }
    }

}
