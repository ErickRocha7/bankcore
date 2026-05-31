package collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Árvore Binária de Busca genérica.
 * Agora com métodos adicionais: remove, addAll, getAll.
 */
public class BinarySearchTree<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static class Node<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    public boolean insert(T value) {
        if (value == null)
            throw new IllegalArgumentException("Valor nulo não permitido.");
        if (contains(value))
            return false;
        root = insertRec(root, value);
        size++;
        return true;
    }

    private Node<T> insertRec(Node<T> current, T value) {
        if (current == null)
            return new Node<>(value);
        int cmp = value.compareTo(current.data);
        if (cmp < 0)
            current.left = insertRec(current.left, value);
        else if (cmp > 0)
            current.right = insertRec(current.right, value);
        return current;
    }

    public boolean contains(T value) {
        return search(value) != null;
    }

    public T search(T value) {
        if (value == null)
            return null;
        Node<T> result = searchRec(root, value);
        return result != null ? result.data : null;
    }

    private Node<T> searchRec(Node<T> current, T value) {
        if (current == null)
            return null;
        int cmp = value.compareTo(current.data);
        if (cmp == 0)
            return current;
        return cmp < 0 ? searchRec(current.left, value) : searchRec(current.right, value);
    }

    public boolean remove(T value) {
        if (value == null || !contains(value))
            return false;
        root = removeRec(root, value);
        size--;
        return true;
    }

    private Node<T> removeRec(Node<T> current, T value) {
        if (current == null)
            return null;
        int cmp = value.compareTo(current.data);
        if (cmp < 0) {
            current.left = removeRec(current.left, value);
        } else if (cmp > 0) {
            current.right = removeRec(current.right, value);
        } else {
            if (current.left == null)
                return current.right;
            if (current.right == null)
                return current.left;
            Node<T> minNode = findMin(current.right);
            current.data = minNode.data;
            current.right = removeRec(current.right, minNode.data);
        }
        return current;
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    public List<T> inOrder() {
        List<T> elements = new ArrayList<>();
        inOrderRec(root, elements);
        return elements;
    }

    private void inOrderRec(Node<T> current, List<T> elements) {
        if (current == null)
            return;
        inOrderRec(current.left, elements);
        elements.add(current.data);
        inOrderRec(current.right, elements);
    }

    public void addAll(Collection<T> collection) {
        if (collection == null)
            return;
        for (T item : collection)
            insert(item);
    }

    public List<T> getAll() {
        return inOrder();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public void printTree() {
        printTreeRec(root, 0);
    }

    private void printTreeRec(Node<T> current, int level) {
        if (current == null)
            return;
        printTreeRec(current.right, level + 1);
        for (int i = 0; i < level; i++)
            System.out.print("   ");
        System.out.println(current.data);
        printTreeRec(current.left, level + 1);
    }

    @Override
    public String toString() {
        return inOrder().toString();
    }
}