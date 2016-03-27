/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.touk.throwing;

import pl.touk.throwing.exception.WrappedException;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a function that accepts one argument and does not return any value;
 * Function might throw a checked exception instance.
 *
 * @param <T> the type of the input to the function
 * @param <E> the type of the thrown checked exception
 *
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    void accept(T t) throws E;

    /**
     * Chains given ThrowingConsumer instance
     * @param after - consumer that is chained after this instance
     * @return chained Consumer instance
     */
    default ThrowingConsumer<T, E> andThenConsume(final ThrowingConsumer<? super T, E> after) {
        Objects.requireNonNull(after);

        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * @return this consumer instance as a Function instance
     */
    default ThrowingFunction<T, Void, E> asFunction() {
        return arg -> {
            this.accept(arg);
            return null;
        };
    }

    static <T, E extends Throwable> Consumer<T> unchecked(ThrowingConsumer<T, E> consumer) {
        Objects.requireNonNull(consumer);

        return consumer.unchecked();
    }

    /**
     * @return a Consumer instance which wraps thrown checked exception instance into a RuntimeException
     */
    default Consumer<T> unchecked() {
        return t -> {
            try {
                accept(t);
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }
}
