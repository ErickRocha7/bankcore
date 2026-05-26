// src/collections/GenericStack.java
package collections;

import java.io.Serializable;
import java.util.EmptyStackException;

/**
 * Pilha genérica (LIFO) baseada em lista encadeada.
 * Utilizada para implementar rollback de operações (desfazer última ação).
 *
 * Cobre:
 * Cap 20 - Genéricos
 * Cap 21 - Estruturas de dados genéricas personalizadas
 * Cap 15 - Serialização (implementa Serializable)
 *
 * @param <T> tipo dos elementos da pilha
 */
public class GenericStack<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // Implementação usando nós internos (como uma lista encadeada simples)
    private static class Node<T> implements Serializable {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> top;
    private int size;

    public GenericStack() {
        top = null;
        size = 0;
    }

    /**
     * Empilha um elemento (push)
     * 
     * @param item elemento a adicionar no topo
     */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /**
     * Desempilha e retorna o elemento do topo (pop)
     * 
     * @return elemento removido
     * @throws EmptyStackException se a pilha estiver vazia
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Espia o elemento do topo sem removê-lo (peek)
     * 
     * @return elemento do topo
     * @throws EmptyStackException se a pilha estiver vazia
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    /**
     * Verifica se a pilha está vazia
     * 
     * @return true se vazia
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna o tamanho atual da pilha
     * 
     * @return número de elementos
     */
    public int size() {
        return size;
    }

    /**
     * Remove todos os elementos da pilha
     */
    public void clear() {
        top = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Top -> ");
        Node<T> current = top;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null)
                sb.append(" -> ");
            current = current.next;
        }
        return sb.toString();
    }
}