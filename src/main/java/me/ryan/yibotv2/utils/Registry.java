package me.ryan.yibotv2.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Registry<V> {
    private List<V> elements;

    public Registry() {
        this.elements = new LinkedList<>();
    }

    public List<V> getElements() {
        return this.elements;
    }

    public <E> List<E> cachedMap(Function<V, E> function) {
        final List<E> copy = new LinkedList<>();

        for (V element : this.elements) {
            copy.add(function.apply(element));
        }
        return copy;
    }

    public boolean contains(V element) {
        return this.elements.contains(element);
    }

    public void register(V element) {
        this.elements.add(element);
    }

    @SafeVarargs
    public final void register(V... elementArg) {
        this.elements.addAll(Arrays.asList(elementArg));
    }

    public void unregister(V element) {
        this.elements.remove(element);
    }

    @SafeVarargs
    public final boolean unregister(V... elementArg) {
        return this.elements.removeAll(Arrays.asList(elementArg));
    }

    public V getByIndex(int index) {
        return this.elements.get(index);
    }

    public V get(Predicate<V> predicate) {
        if (this.elements == null) this.elements = new LinkedList<>();

        for (V element : this.elements) {
            if (predicate.test(element)) return element;
        }
        return null;
    }

    public List<V> getElements(Predicate<V> predicate) {
        final List<V> array = new LinkedList<>();
        for (V element : this.elements)
            if (predicate.test(element)) array.add(element);

        return array;
    }

    public Optional<V> find(Predicate<V> predicate) {
        return Optional.ofNullable(get(predicate));
    }

    public Optional<V> findAndRemove(Predicate<V> predicate) {
        final Optional<V> optional = find(predicate);
        optional.ifPresent(this::unregister);
        return optional;
    }

    public Iterator<V> iterator() {
        return this.elements.iterator();
    }

    public int size() {
        return this.elements.size();
    }

    public void removeIf(Predicate<V> predicate) {
        for (V element : elements) {
            if (predicate.test(element)) unregister(element);
        }
    }
}