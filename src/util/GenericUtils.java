// src/util/GenericUtils.java
package util;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe utilitária contendo métodos genéricos para operações comuns.
 * 
 * Capítulos abordados:
 * 20 - Classes e métodos genéricos
 * 21 - Estruturas de dados genéricas personalizadas (uso de listas, comparações)
 */
public class GenericUtils {

    /**
     * Exibe qualquer lista de elementos (um por linha).
     * @param <T> Tipo dos elementos
     * @param list Lista a ser impressa
     */
    public static <T> void printList(List<T> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Lista vazia.");
            return;
        }
        for (T element : list) {
            System.out.println(element);
        }
    }

    /**
     * Exibe qualquer mapa no formato chave=valor.
     * @param <K> Tipo das chaves
     * @param <V> Tipo dos valores
     * @param map Mapa a ser impresso
     */
    public static <K, V> void printMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("Mapa vazio.");
            return;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    /**
     * Inverte um array de elementos genéricos e retorna um novo array.
     * @param <T> Tipo dos elementos
     * @param array Array original
     * @return Novo array com elementos na ordem inversa
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] reverseArray(T[] array) {
        if (array == null) return null;
        T[] reversed = (T[]) new Object[array.length]; // unchecked cast, mas seguro
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Verifica se um array de elementos comparáveis está ordenado (crescente).
     * @param <T> Tipo que implementa Comparable
     * @param array Array a ser verificado
     * @return true se ordenado, false caso contrário
     */
    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        if (array == null || array.length <= 1) return true;
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna o maior elemento de uma lista de elementos comparáveis.
     * @param <T> Tipo que implementa Comparable
     * @param list Lista não vazia
     * @return Maior elemento
     * @throws IllegalArgumentException se a lista for vazia
     */
    public static <T extends Comparable<T>> T max(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Lista vazia ou nula.");
        }
        T max = list.get(0);
        for (T element : list) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }

    /**
     * Filtra uma lista baseada em um predicado (interface funcional simples).
     * Exemplo de uso: filter(list, e -> e.startsWith("A"))
     * @param <T> Tipo dos elementos
     * @param list Lista original
     * @param predicate Predicado que define o filtro
     * @return Nova lista com os elementos filtrados
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        if (list == null) return result;
        for (T element : list) {
            if (predicate.test(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Interface funcional simples para demonstração de genéricos e lambdas (capítulo 17 opcional).
     * @param <T> Tipo de entrada
     */
    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t);
    }
}