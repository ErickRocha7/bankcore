// src/collections/GenericQueue.java
package collections;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Fila genérica (FIFO) implementada com lista encadeada.
 * Será usada para processamento em lote de transações futuras.
 *
 * Cobre:
 * Cap 20 - Genéricos
 * Cap 21 - Estruturas de dados genéricas personalizadas
 * Cap 15 - Serialização
 *
 * @param <T> tipo dos elementos da fila
 */
public class GenericQueue<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class Node<T> implements Serializable {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head; // início da fila (remoção)
    private Node<T> tail; // fim da fila (inserção)
    private int size;

    public GenericQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Insere um elemento no final da fila (enqueue)
     * 
     * @param item elemento a inserir
     */
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    /**
     * Remove e retorna o elemento do início da fila (dequeue)
     * 
     * @return elemento removido
     * @throws NoSuchElementException se a fila estiver vazia
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return data;
    }

    /**
     * Espia o elemento do início sem remover (peek)
     * 
     * @return elemento do início
     * @throws NoSuchElementException se vazia
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        return head.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Head -> ");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null)
                sb.append(" <- ");
            current = current.next;
        }
        return sb.toString();
    }
}