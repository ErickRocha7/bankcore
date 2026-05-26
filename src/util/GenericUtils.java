package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitária contendo operações genéricas reutilizáveis.
 *
 * Funcionalidades:
 * - Impressão de listas e mapas
 * - Manipulação de arrays
 * - Ordenação
 * - Busca de máximos e mínimos
 * - Filtragem genérica
 * - Operações funcionais simples
 *
 * Capítulos abordados:
 * 14 - Arrays e Strings
 * 16 - Collections Framework
 * 20 - Generics
 * 21 - Estruturas genéricas
 */
public final class GenericUtils {

    /**
     * Construtor privado.
     * Classe utilitária não deve ser instanciada.
     */
    private GenericUtils() {
        throw new UnsupportedOperationException(
                "Classe utilitária não pode ser instanciada.");
    }

    /**
     * Imprime elementos de uma lista.
     *
     * @param list lista a ser exibida
     * @param <T>  tipo dos elementos
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
     * Imprime um mapa no formato chave = valor.
     *
     * @param map mapa a ser exibido
     * @param <K> tipo da chave
     * @param <V> tipo do valor
     */
    public static <K, V> void printMap(Map<K, V> map) {

        if (map == null || map.isEmpty()) {
            System.out.println("Mapa vazio.");
            return;
        }

        for (Map.Entry<K, V> entry : map.entrySet()) {

            System.out.println(
                    entry.getKey()
                            + " = "
                            + entry.getValue());
        }
    }

    /**
     * Retorna novo array invertido.
     *
     * Implementação corrigida:
     * utiliza reflexão para preservar o tipo original.
     *
     * @param array array original
     * @param <T>   tipo dos elementos
     * @return novo array invertido
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] reverseArray(T[] array) {

        if (array == null) {
            return null;
        }

        Class<?> componentType = array.getClass().getComponentType();

        T[] reversed = (T[]) Array.newInstance(
                componentType,
                array.length);

        for (int i = 0; i < array.length; i++) {

            reversed[i] = array[array.length - 1 - i];
        }

        return reversed;
    }

    /**
     * Verifica se array está ordenado
     * em ordem crescente.
     *
     * @param array array analisado
     * @param <T>   tipo comparável
     * @return true se ordenado
     */
    public static <T extends Comparable<T>> boolean isSorted(T[] array) {

        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {

            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retorna maior elemento de uma lista.
     *
     * @param list lista
     * @param <T>  tipo comparável
     * @return maior elemento
     */
    public static <T extends Comparable<T>> T max(List<T> list) {

        validateList(list);

        T max = list.get(0);

        for (T element : list) {

            if (element.compareTo(max) > 0) {
                max = element;
            }
        }

        return max;
    }

    /**
     * Retorna menor elemento da lista.
     *
     * @param list lista
     * @param <T>  tipo comparável
     * @return menor elemento
     */
    public static <T extends Comparable<T>> T min(List<T> list) {

        validateList(list);

        T min = list.get(0);

        for (T element : list) {

            if (element.compareTo(min) < 0) {
                min = element;
            }
        }

        return min;
    }

    /**
     * Ordena lista em ordem natural.
     *
     * Retorna nova lista defensiva.
     *
     * @param list lista original
     * @param <T>  tipo comparável
     * @return lista ordenada
     */
    public static <T extends Comparable<T>> List<T> sort(List<T> list) {

        validateList(list);

        List<T> sorted = new ArrayList<>(list);

        Collections.sort(sorted);

        return sorted;
    }

    /**
     * Ordena lista usando Comparator customizado.
     *
     * @param list       lista original
     * @param comparator comparador
     * @param <T>        tipo
     * @return lista ordenada
     */
    public static <T> List<T> sort(
            List<T> list,
            Comparator<T> comparator) {

        validateList(list);

        if (comparator == null) {

            throw new IllegalArgumentException(
                    "Comparator não pode ser nulo.");
        }

        List<T> sorted = new ArrayList<>(list);

        sorted.sort(comparator);

        return sorted;
    }

    /**
     * Filtra lista usando predicado funcional.
     *
     * @param list      lista original
     * @param predicate condição
     * @param <T>       tipo
     * @return lista filtrada
     */
    public static <T> List<T> filter(
            List<T> list,
            Predicate<T> predicate) {

        List<T> result = new ArrayList<>();

        if (list == null || predicate == null) {
            return result;
        }

        for (T element : list) {

            if (predicate.test(element)) {
                result.add(element);
            }
        }

        return result;
    }

    /**
     * Conta quantos elementos satisfazem condição.
     *
     * @param list      lista
     * @param predicate condição
     * @param <T>       tipo
     * @return quantidade
     */
    public static <T> int countMatches(
            List<T> list,
            Predicate<T> predicate) {

        if (list == null || predicate == null) {
            return 0;
        }

        int count = 0;

        for (T element : list) {

            if (predicate.test(element)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Verifica se lista contém elemento.
     *
     * @param list   lista
     * @param target elemento
     * @param <T>    tipo
     * @return true se encontrado
     */
    public static <T> boolean contains(
            List<T> list,
            T target) {

        if (list == null || list.isEmpty()) {
            return false;
        }

        return list.contains(target);
    }

    /**
     * Cria cópia defensiva da lista.
     *
     * @param list lista original
     * @param <T>  tipo
     * @return nova lista
     */
    public static <T> List<T> copy(List<T> list) {

        if (list == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(list);
    }

    /**
     * Validação padrão de listas.
     */
    private static <T> void validateList(List<T> list) {

        if (list == null || list.isEmpty()) {

            throw new IllegalArgumentException(
                    "Lista não pode ser nula ou vazia.");
        }
    }

    /**
     * Interface funcional simples.
     *
     * Demonstração didática de:
     * - Interfaces funcionais
     * - Generics
     * - Lambdas
     *
     * @param <T> tipo analisado
     */
    @FunctionalInterface
    public interface Predicate<T> {

        /**
         * Executa teste lógico.
         *
         * @param t elemento
         * @return true se válido
         */
        boolean test(T t);
    }
}