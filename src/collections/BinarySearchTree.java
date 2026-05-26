// src/collections/BinarySearchTree.java
package collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Árvore Binária de Busca genérica (BST).
 * 
 * Utilizada para busca eficiente de elementos ordenáveis, como clientes por CPF
 * ou contas por identificador.
 *
 * Características:
 * - Estrutura genérica usando Comparable
 * - Inserção recursiva
 * - Busca recursiva
 * - Percurso in-order
 * - Não permite elementos duplicados
 * - Implementa Serializable
 *
 * Capítulos abordados:
 * 15 - Serialização
 * 18 - Recursão
 * 20 - Genéricos
 * 21 - Estruturas de dados personalizadas
 *
 * @param <T> Tipo armazenado na árvore
 */
public class BinarySearchTree<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Nó interno da árvore.
     */
    private static class Node<T> implements Serializable {

        private static final long serialVersionUID = 1L;

        private T data;
        private Node<T> left;
        private Node<T> right;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private int size;

    /**
     * Cria uma árvore vazia.
     */
    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Insere um elemento na árvore.
     * 
     * Não permite valores nulos nem duplicados.
     *
     * @param value valor a inserir
     * @return true se inserido, false se já existir
     */
    public boolean insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor nulo não permitido.");
        }

        if (contains(value)) {
            return false;
        }

        root = insertRec(root, value);
        size++;
        return true;
    }

    /**
     * Inserção recursiva.
     */
    private Node<T> insertRec(Node<T> current, T value) {

        if (current == null) {
            return new Node<>(value);
        }

        int comparison = value.compareTo(current.data);

        if (comparison < 0) {
            current.left = insertRec(current.left, value);
        } else if (comparison > 0) {
            current.right = insertRec(current.right, value);
        }

        return current;
    }

    /**
     * Busca um elemento na árvore.
     *
     * @param value valor buscado
     * @return elemento encontrado ou null
     */
    public T search(T value) {

        if (value == null) {
            return null;
        }

        Node<T> result = searchRec(root, value);

        return result != null ? result.data : null;
    }

    /**
     * Busca recursiva.
     */
    private Node<T> searchRec(Node<T> current, T value) {

        if (current == null) {
            return null;
        }

        int comparison = value.compareTo(current.data);

        if (comparison == 0) {
            return current;
        }

        if (comparison < 0) {
            return searchRec(current.left, value);
        }

        return searchRec(current.right, value);
    }

    /**
     * Verifica se o valor existe na árvore.
     *
     * @param value valor procurado
     * @return true se existir
     */
    public boolean contains(T value) {
        return search(value) != null;
    }

    /**
     * Retorna os elementos em ordem crescente.
     *
     * @return lista ordenada
     */
    public List<T> inOrder() {

        List<T> elements = new ArrayList<>();

        inOrderRec(root, elements);

        return elements;
    }

    /**
     * Percurso recursivo in-order.
     */
    private void inOrderRec(Node<T> current, List<T> elements) {

        if (current == null) {
            return;
        }

        inOrderRec(current.left, elements);

        elements.add(current.data);

        inOrderRec(current.right, elements);
    }

    /**
     * Exibe a árvore de forma hierárquica.
     * Método útil para depuração.
     */
    public void printTree() {

        if (isEmpty()) {
            System.out.println("(árvore vazia)");
            return;
        }

        printTreeRec(root, 0);
    }

    /**
     * Impressão recursiva da árvore.
     */
    private void printTreeRec(Node<T> current, int level) {

        if (current == null) {
            return;
        }

        printTreeRec(current.right, level + 1);

        for (int i = 0; i < level; i++) {
            System.out.print("   ");
        }

        System.out.println(current.data);

        printTreeRec(current.left, level + 1);
    }

    /**
     * Retorna a quantidade de elementos.
     *
     * @return tamanho da árvore
     */
    public int size() {
        return size;
    }

    /**
     * Verifica se a árvore está vazia.
     *
     * @return true se vazia
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Remove todos os elementos da árvore.
     */
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public String toString() {
        return inOrder().toString();
    }
}