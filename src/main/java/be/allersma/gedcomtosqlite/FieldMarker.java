package be.allersma.gedcomtosqlite;

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
    public static Branch<Gedcom> createMarkerTree(Gedcom gedcom) {
        return new Branch<>(gedcom);
    }

    public static class Branch<T> {
        private final List<Leaf> leaves;
        T value;

        private Branch(T value) {
            this.value = value;
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
                    return Optional.of(new Branch<>(result));
                } else if ("java.util.List".equals(match.get(0).method.getReturnType().getName())) {
                    Type[] actualType = ((ParameterizedType)match.get(0).method.getGenericReturnType()).getActualTypeArguments();
                    if (actualType.length == 1 && actualType[0].getTypeName() != null) {
                        if (actualType[0].getTypeName().startsWith("org.folg.gedcom.model")) {
                            return Optional.of(
                                    ((List)result).stream().map(Branch::new).collect(Collectors.toList())
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
            return leaves.stream()
                    .filter(leaf -> !leaf.marked)
                    .map(leaf -> leaf.name)
                    .collect(Collectors.toList());
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
