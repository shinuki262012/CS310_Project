package slp.util;

public class BinaryTreeNode<E extends Comparable<E>> {

    private E value;
    private BinaryTreeNode<E> left;
    private BinaryTreeNode<E> right;
    private int height;
    private int width;

    public BinaryTreeNode(E val) {
        this.value = val;
        this.left = null;
        this.right = null;
        this.height = 1;
        this.width = 1;
    }

    public BinaryTreeNode(E val, BinaryTreeNode<E> left, BinaryTreeNode<E> right) {
        this.value = val;
        this.left = left;
        this.right = right;
        this.height = 1
                + (this.left.getHeight() > this.right.getHeight() ? this.left.getHeight() : this.right.getHeight());
        this.width = this.left.getWidth() + this.right.getWidth();
    }

    public E getValue() {
        return this.value;
    }

    public BinaryTreeNode<E> getLeft() {
        return this.left;
    }

    public BinaryTreeNode<E> getRight() {
        return this.right;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setValue(E v) {
        this.value = v;
    }

    public void setLeft(BinaryTreeNode<E> node) {
        this.left = node;
    }

    public void setRight(BinaryTreeNode<E> node) {
        this.right = node;
    }

    public int updateHeight() {
        if (this.left == null) {
            return 1;
        } else {
            this.height = 1
                    + (this.left.updateHeight() > this.right.updateHeight() ? this.left.updateHeight()
                            : this.right.updateHeight());
            return this.height;
        }
    }

    public int updateWidth() {
        if (this.left == null) {
            return 1;
        } else {
            this.width = this.left.updateWidth() + this.right.updateWidth();
            return this.width;
        }
    }

    public void toString(String prefix, boolean isLeft) {
        if (this.left != null) {
            System.out.println(prefix + (isLeft ? "|-" : "`-") + value);
            left.toString(prefix + (isLeft ? "| " : "  "), true);
            if (this.right != null) {
                right.toString(prefix + (isLeft ? "| " : "  "), false);
            }
        } else {
            // TODO: add new line, space and tab
            if (String.valueOf(value).equals("\n"))
                System.out.println(prefix + (isLeft ? "|--" : "`--") + "\\n");
            else if (String.valueOf(value).equals(" "))
                System.out.println(prefix + (isLeft ? "|--" : "`--") + "' '");
            else if (String.valueOf(value).equals("\t")) {
                System.out.println(prefix + (isLeft ? "|--" : "`--") + "\\t");
            } else if (String.valueOf(value).equals("  ")) {
                System.out.println(prefix + (isLeft ? "|--" : "`--") + "\\t");
            } else
                System.out.println(prefix + (isLeft ? "|--" : "`--") + value);
        }
    }

    // Driver
    public static void main(String[] args) {
        BinaryTreeNode<String> a = new BinaryTreeNode<String>("a");
        System.out.println(a.getHeight());
        BinaryTreeNode<String> b = new BinaryTreeNode<String>("b");
        System.out.println(b.getHeight());
        BinaryTreeNode<String> c = new BinaryTreeNode<String>("c");
        System.out.println(c.getHeight());
        BinaryTreeNode<String> d = new BinaryTreeNode<String>("d", a, b);
        System.out.println(d.getHeight());
        BinaryTreeNode<String> e = new BinaryTreeNode<String>("e", d, c);
        System.out.println(e.getHeight());
        e.toString("", false);
    }
}