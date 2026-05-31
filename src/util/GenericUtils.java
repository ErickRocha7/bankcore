package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class GenericUtils {

    private GenericUtils() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada.");
    }

    public static <T> void printList(List<T> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Lista vazia.");
            return;
        }
        for (T element : list) {
            System.out.println(element);
        }
    }

    public static <K, V> void printMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("Mapa vazio.");
            return;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] reverseArray(T[] array) {
        if (array == null)
            return null;
        Class<?> componentType = array.getClass().getComponentType();
        T[] reversed = (T[]) Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        if (array == null || array.length <= 1)
            return true;
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0)
                return false;
        }
        return true;
    }

    public static <T extends Comparable<T>> T max(List<T> list) {
        validateList(list);
        T max = list.get(0);
        for (T element : list) {
            if (element.compareTo(max) > 0)
                max = element;
        }
        return max;
    }

    public static <T extends Comparable<T>> T min(List<T> list) {
        validateList(list);
        T min = list.get(0);
        for (T element : list) {
            if (element.compareTo(min) < 0)
                min = element;
        }
        return min;
    }

    public static <T extends Comparable<T>> List<T> sort(List<T> list) {
        validateList(list);
        List<T> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        return sorted;
    }

    public static <T> List<T> sort(List<T> list, Comparator<T> comparator) {
        validateList(list);
        if (comparator == null)
            throw new IllegalArgumentException("Comparator não pode ser nulo.");
        List<T> sorted = new ArrayList<>(list);
        sorted.sort(comparator);
        return sorted;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        if (list == null || predicate == null)
            return result;
        for (T element : list) {
            if (predicate.test(element))
                result.add(element);
        }
        return result;
    }

    public static <T> int countMatches(List<T> list, Predicate<T> predicate) {
        if (list == null || predicate == null)
            return 0;
        int count = 0;
        for (T element : list) {
            if (predicate.test(element))
                count++;
        }
        return count;
    }

    public static <T> boolean contains(List<T> list, T target) {
        return list != null && list.contains(target);
    }

    public static <T> List<T> copy(List<T> list) {
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    private static <T> void validateList(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Lista não pode ser nula ou vazia.");
        }
    }

    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t);
    }
}