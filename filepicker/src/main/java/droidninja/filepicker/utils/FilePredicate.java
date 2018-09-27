package droidninja.filepicker.utils;

/**
 * Created by Peter.Helmy on 9/27/2018.
 */

@FunctionalInterface
public interface FilePredicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T t);
}