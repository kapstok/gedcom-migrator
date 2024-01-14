package be.allersma.gedcom.migrator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folg.gedcom.model.Gedcom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A field marker can be used to mark fields that have been read before.
 * This way you can check whether you have called all fields that are not null.
 */
public class FunctionMarker {
    private static final Logger logger = LogManager.getLogger(FunctionMarker.class);

    /**
     * Same function as {@link FunctionMarker#createMarkerTree(Gedcom, boolean)}, but with ignoreNullFields
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
                logger.warn("More than one match found. Should never happen. Taking first one ...");
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
                        logger.error("Expected only one actual type argument. Got {}.", actualType.length);
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

        /**
         * Marks all leaves of this branch
         */
        public void markAll() {
            logger.info("Using markAll without security hash. Recommended to use hash.");

            String securityHash = getMarkAllHash();
            markAll(securityHash);
        }

        /**
         * Has the same behaviour as {@link Branch#markAll()}, but also requires a security hash,
         * which can be obtained from {@link Branch#getMarkAllHash()}. Once you made an implementation
         * for your migration, you can use this function to mark all leaves that you haven't used.
         * By storing the hash somewhere, for example in a file like a {@link ResourceBundle},
         * the library doesn't mark all fields automatically when, for example due to a new Gedcom specification,
         * the fields have changed.
         */
        public void markAll(String securityHash) {
            if (!getMarkAllHash().equals(securityHash)) {
                logger.error("Hash is different! Fields have changed. Not marking all fields.");
                return;
            }

            leaves.forEach(leaf -> leaf.marked = true);
        }

        /**
         * @see Branch#markAll(String)
         * @return An SHA-512 Hash
         */
        public String getMarkAllHash() {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
                logger.error("Unable to create digest. This should never happen!");
                throw new RuntimeException(e);
            }

            String input = getPath() + leaves.stream().map(leaf -> leaf.name).collect(Collectors.joining());
            byte[] hashedLeaves = digest.digest(input.getBytes());
            BigInteger hashRepresentation = new BigInteger(1, hashedLeaves);
            StringBuilder hash = new StringBuilder(hashRepresentation.toString(16));

            // Add preceding 0s to make it 32 bit
            while (hash.length() < 32) {
                hash.insert(0, "0");
            }

            return hash.toString();
        }

        /**
         * Sometimes you want a function to be ignored while not using its return value.
         * You can ignore individual functions by marking them using this function.
         * @param function The name of the function
         */
        public Branch<T> mark(String function) {
            leaves.stream()
                    .filter(leaf -> function.equals(leaf.name))
                    .forEach(leaf -> leaf.marked = true);

            return this;
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
