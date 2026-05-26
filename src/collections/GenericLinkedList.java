// src/collections/GenericLinkedList.java
package collections;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista encadeada genérica (tipo T).
 * Utilizada para armazenar histórico de transações (Transaction) e LedgerEntry.
 *
 * Cobre:
 * Cap 20 - Classes e métodos genéricos
 * Cap 21 - Estruturas de dados genéricas personalizadas
 * Cap 15 - Serialização (implementa Serializable)
 *
 * @param <T> tipo dos elementos da lista
 */
public class GenericLinkedList<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private Node<T> head;
    private Node<T> tail;
    private int size;

    // Classe interna estática para os nós
    private static class Node<T> implements Serializable {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public GenericLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Adiciona um elemento ao final da lista (O(1))
     * 
     * @param item elemento a ser inserido
     */
    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++; // operador de incremento (cap 4)
    }

    /**
     * Adiciona um elemento em um índice específico (O(n))
     * 
     * @param index posição (0..size)
     * @param item  elemento a inserir
     * @throws IndexOutOfBoundsException se índice inválido
     */
    public void add(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamanho: " + size);
        }
        Node<T> newNode = new Node<>(item);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            if (tail == null)
                tail = newNode;
        } else {
            Node<T> prev = getNode(index - 1);
            newNode.next = prev.next;
            prev.next = newNode;
            if (newNode.next == null)
                tail = newNode;
        }
        size++;
    }

    /**
     * Obtém o elemento no índice especificado (O(n))
     * 
     * @param index posição do elemento (0..size-1)
     * @return elemento na posição
     * @throws IndexOutOfBoundsException se índice inválido
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamanho: " + size);
        }
        return getNode(index).data;
    }

    /**
     * Remove e retorna o elemento no índice (O(n))
     * 
     * @param index posição a remover
     * @return elemento removido
     * @throws IndexOutOfBoundsException se índice inválido
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", Tamanho: " + size);
        }
        T removed;
        if (index == 0) {
            removed = head.data;
            head = head.next;
            if (head == null)
                tail = null;
        } else {
            Node<T> prev = getNode(index - 1);
            Node<T> current = prev.next;
            removed = current.data;
            prev.next = current.next;
            if (prev.next == null)
                tail = prev;
        }
        size--;
        return removed;
    }

    /**
     * Remove a primeira ocorrência do objeto (igualdade via equals)
     * 
     * @param item objeto a remover
     * @return true se encontrado e removido
     */
    public boolean remove(T item) {
        if (head == null)
            return false;
        if (head.data.equals(item)) {
            head = head.next;
            if (head == null)
                tail = null;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(item)) {
                current.next = current.next.next;
                if (current.next == null)
                    tail = current;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Verifica se a lista contém o elemento (O(n))
     * 
     * @param item elemento a procurar
     * @return true se presente
     */
    public boolean contains(T item) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(item))
                return true;
            current = current.next;
        }
        return false;
    }

    /**
     * Retorna o tamanho da lista
     * 
     * @return número de elementos
     */
    public int size() {
        return size;
    }

    /**
     * Verifica se a lista está vazia
     * 
     * @return true se vazia
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Limpa a lista, removendo todos os elementos
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Retorna um iterador para percorrer os elementos (ordem de inserção)
     * 
     * @return Iterator<T>
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    // Iterador interno
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            T data = current.data;
            current = current.next;
            return data;
        }
    }

    // Método auxiliar para acessar o nó em determinado índice
    private Node<T> getNode(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    /**
     * Representação textual da lista (útil para depuração)
     * 
     * @return string no formato [elem1, elem2, ...]
     */
    @Override
    public String toString() {
        if (size == 0)
            return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null)
                sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}