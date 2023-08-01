package be.allersma.gedcom.migrator;

import org.folg.gedcom.model.Gedcom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A field marker can be used to mark fields that have been read before.
 * This way you can check whether you have called all fields that are not null.
 */
public class FieldMarker {

    /**
     * Same function as {@link FieldMarker#createMarkerTree(Gedcom, boolean)}, but with ignoreNullFields
     * set to false.
     */
    public static Branch<Gedcom> createMarkerTree(Gedcom gedcom) {
        return createMarkerTree(gedcom, false);
    }

    /**
     * Setting ignoreNullFields to true is more costly performance-wise.
     * @param ignoreNullFields When looking for unmarked items, ignore check on all getters that return
     *                         null. Also ignores empty {@link List}.
     */
    public static Branch<Gedcom> createMarkerTree(Gedcom gedcom, boolean ignoreNullFields) {
        return new Branch<>(gedcom, "", ignoreNullFields);
    }

    public static class Branch<T> {
        T value;
        private final boolean ignoreNullFields;
        private final List<Leaf> leaves;
        private final String path;

        private Branch(T value, String path, boolean ignoreNullFields) {
            this.value = value;
            this.path = path.isEmpty() ? "" : "/" + path;
            this.ignoreNullFields = ignoreNullFields;
            this.leaves = Arrays.stream(value.getClass().getMethods())
                    .map(Leaf::new)
                    .collect(Collectors.toList());
        }

        public Optional<Object> invoke(String name, Object... args) {
            List<Leaf> match = leaves.stream()
                    .filter(leaf -> name.equals(leaf.name))
                    .collect(Collectors.toList());

            if (match.isEmpty()) {
                return Optional.empty();
            }

            if (match.size() > 1) {
                System.err.println("WARNING: More than one match found. Should never happen. Taking first one ...");
            }

            try {
                Object result = match.get(0).method.invoke(value, args);
                match.get(0).marked = true;
                if ("org.folg.gedcom.model".equals(match.get(0).method.getReturnType().getPackageName())) {
                    return Optional.of(new Branch<>(result, getPath() + match.get(0).name, ignoreNullFields));
                } else if ("java.util.List".equals(match.get(0).method.getReturnType().getName())) {
                    Type[] actualType = ((ParameterizedType)match.get(0).method.getGenericReturnType()).getActualTypeArguments();
                    if (actualType.length == 1 && actualType[0].getTypeName() != null) {
                        if (actualType[0].getTypeName().startsWith("org.folg.gedcom.model")) {
                            return Optional.of(
                                    ((List)result).stream()
                                            .map(entry -> new Branch<>(entry, getPath() + match.get(0).name, ignoreNullFields))
                                            .collect(Collectors.toList())
                            );
                        } else {
                            return Optional.of(result);
                        }
                    } else {
                        System.err.printf("ERROR: Expected only one actual type argument. Got %d.%n", actualType.length);
                        return Optional.empty();
                    }
                } else {
                    return Optional.of(result);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        }

        public List<String> getUnmarkedItems() {
            if (!ignoreNullFields) {
                return leaves.stream()
                        .filter(leaf -> !leaf.marked)
                        .map(leaf -> leaf.name)
                        .collect(Collectors.toList());
            } else {
                return leaves.stream()
                        .filter(leaf -> !leaf.marked)
                        .filter(leaf -> leaf.method.getParameterCount() == 0)
                        .filter(getter -> {
                            try {
                                return getter.method.invoke(value) != null;
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                return false;
                            }
                        })
                        .filter(getter -> {
                            if ("java.util.List".equals(getter.method.getReturnType().getName())) {
                                try {
                                    return !((List<?>)getter.method.invoke(value)).isEmpty();
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    return false;
                                }
                            } else {
                                return true;
                            }
                        })
                        .map(getter -> getter.name)
                        .collect(Collectors.toList());
            }
        }

        public String getPath() {
            return this.path;
        }
    }

    public static class Leaf {
        Method method;
        String name;
        boolean marked;

        private Leaf(Method method) {
            this.method = method;
            this.name = method.getName();
            this.marked = false;
        }
    }
}
