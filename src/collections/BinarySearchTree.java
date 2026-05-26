// src/collections/BinarySearchTree.java
package collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Árvore Binária de Busca genérica (BST).
 * Utilizada para busca eficiente de clientes por CPF ou outros identificadores.
 * Implementa inserção, busca e travessia em ordem (in-order) usando recursão.
 *
 * Cobre:
 * Cap 18 - Recursão (métodos recursivos para inserir, buscar, percorrer)
 * Cap 20 - Genéricos
 * Cap 21 - Estruturas de dados genéricas personalizadas
 * Cap 15 - Serialização
 *
 * @param <T> tipo de dado armazenado (deve ser Comparable ou usar Comparator)
 */
public class BinarySearchTree<T extends Comparable<T>> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class Node<T> implements Serializable {
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
            left = null;
            right = null;
        }
    }

    private Node<T> root;
    private int size;

    // Construtor
    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    /**
     * Insere um novo elemento na árvore (recursivo)
     * 
     * @param value elemento a inserir (não nulo)
     */
    public void insert(T value) {
        if (value == null)
            throw new IllegalArgumentException("Valor nulo não permitido.");
        root = insertRec(root, value);
        size++;
    }

    // Inserção recursiva
    private Node<T> insertRec(Node<T> node, T value) {
        if (node == null) {
            return new Node<>(value);
        }
        int cmp = value.compareTo(node.data);
        if (cmp < 0) {
            node.left = insertRec(node.left, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, value);
        } // se igual, não insere duplicata (ou poderia tratar dependendo da política)
        return node;
    }

    /**
     * Busca um elemento na árvore (recursivo)
     * 
     * @param value valor a buscar
     * @return o elemento encontrado ou null se ausente
     */
    public T search(T value) {
        if (value == null)
            return null;
        Node<T> result = searchRec(root, value);
        return (result != null) ? result.data : null;
    }

    private Node<T> searchRec(Node<T> node, T value) {
        if (node == null || value.compareTo(node.data) == 0) {
            return node;
        }
        if (value.compareTo(node.data) < 0) {
            return searchRec(node.left, value);
        } else {
            return searchRec(node.right, value);
        }
    }

    /**
     * Verifica se a árvore contém um valor
     * 
     * @param value valor
     * @return true se existir
     */
    public boolean contains(T value) {
        return search(value) != null;
    }

    /**
     * Retorna uma lista ordenada dos elementos (percurso em ordem simétrica)
     * 
     * @return lista em ordem crescente
     */
    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node<T> node, List<T> list) {
        if (node != null) {
            inOrderRec(node.left, list);
            list.add(node.data);
            inOrderRec(node.right, list);
        }
    }

    /**
     * Exibe a árvore de forma hierárquica (apenas para depuração)
     */
    public void printTree() {
        printTreeRec(root, 0);
    }

    private void printTreeRec(Node<T> node, int level) {
        if (node == null)
            return;
        printTreeRec(node.right, level + 1);
        for (int i = 0; i < level; i++)
            System.out.print("   ");
        System.out.println(node.data);
        printTreeRec(node.left, level + 1);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}