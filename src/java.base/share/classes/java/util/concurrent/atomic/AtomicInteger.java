/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;

import java.lang.invoke.VarHandle;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import jdk.internal.misc.Unsafe;

/**
 * An {@code int} value that may be updated atomically.
 * See the {@link VarHandle} specification for descriptions
 * of the properties of atomic accesses.
 * fixme
 *      可以原子的更新值的 int值，查看VarHandle规范中、对于 原子获取 的属性描述。
 *      原子类可以用来作为并发计数器，但是并不能用来替代Integer。
 *      但是该类继承 Number，因此一些 xxNumberUtil 可以对其进行数值操作。
 *
 * An {@code AtomicInteger}
 * is used in applications such as atomically incremented counters,
 * and cannot be used as a replacement for an {@link java.lang.Integer}.
 * However, this class does extend {@code Number} to allow uniform access
 * by tools and utilities that deal with numerically-based classes.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicInteger
        // byte short int long float double
        extends Number
        // 可序列化
        implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    /*
     * This class intended to be implemented using VarHandles,
     * but there are unresolved cyclic startup dependencies.
     */

    // 获取 AtomicInteger.class 中，value 属性的偏移量
    private static final Unsafe U = Unsafe.getUnsafe();
    private static final long VALUE_OFFSET = U.objectFieldOffset(AtomicInteger.class, "value");

    // volatile 保证可见性
    private volatile int value;

    /**
     * Creates a new AtomicInteger with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicInteger(int initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicInteger with initial value {@code 0}.
     */
    public AtomicInteger() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final int get() {
        return value;
    }

    /**
     * 为Integer对象指定新的值。
     *
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(int newValue) {
        value = newValue;
    }

    /**
     * Sets the value to {@code newValue}, with memory effects
     * as specified by {@link VarHandle#setRelease}.
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(int newValue) {
        // 具有 volatile 语义的 putInt(object,offSet,newValue)
        U.putIntRelease(this, VALUE_OFFSET, newValue);
    }

    /**
     * 更新指定值、并返回旧值。
     *
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link VarHandle#getAndSet}.
     *
     * @param newValue the new value
     *        新的值。
     * @return the previous value
     *         获取之前的值。
     */
    public final int getAndSet(int newValue) {
        return U.getAndSetInt(this, VALUE_OFFSET, newValue);
    }

    /**
     * cas操作。
     *
     * Atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#compareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int expectedValue, int newValue) {
        return U.compareAndSetInt(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * todo
     *
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#weakCompareAndSetPlain}.
     *
     * @deprecated This method has plain memory effects but the method
     * name implies volatile memory effects (see methods such as
     * {@link #compareAndExchange} and {@link #compareAndSet}).  To avoid
     * confusion over plain or volatile memory effects it is recommended that
     * the method {@link #weakCompareAndSetPlain} be used instead.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @see #weakCompareAndSetPlain
     */
    @Deprecated(since="9")
    public final boolean weakCompareAndSet(int expectedValue, int newValue) {
        return U.weakCompareAndSetIntPlain(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#weakCompareAndSetPlain}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    public final boolean weakCompareAndSetPlain(int expectedValue, int newValue) {
        return U.weakCompareAndSetIntPlain(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * 原子的递增，并返回旧值。
     *
     * Atomically increments the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(1)}.
     *
     * @return the previous value
     *         旧值。
     */
    public final int getAndIncrement() {
        return U.getAndAddInt(this, VALUE_OFFSET, 1);
    }

    /**
     * 原子的递减，并返回旧值。
     *
     * Atomically decrements the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(-1)}.
     *
     * @return the previous value
     */
    public final int getAndDecrement() {
        return U.getAndAddInt(this, VALUE_OFFSET, -1);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final int getAndAdd(int delta) {
        return U.getAndAddInt(this, VALUE_OFFSET, delta);
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(1)}.
     *
     * @return the updated value
     */
    public final int incrementAndGet() {
        return U.getAndAddInt(this, VALUE_OFFSET, 1) + 1;
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(-1)}.
     *
     * @return the updated value
     */
    public final int decrementAndGet() {
        // getAndAddInt返回的是原始值，所以要-1；
        return U.getAndAddInt(this, VALUE_OFFSET, -1) - 1;
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final int addAndGet(int delta) {
        return U.getAndAddInt(this, VALUE_OFFSET, delta) + delta;
    }

    /**
     * todo：挺高级的一个函数。
     *
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function, returning the previous value.
     *
     * The function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention(争用) among threads.
     * 该函数应该是"没有副作用"的，因为可能因为线程间的cas争用、导致函数执行多次。
     *
     * @param updateFunction a side-effect-free function
     *                       无副作用的函数；
     *                       int applyAsInt(int operand);
     *
     * @return the previous value
     * @since 1.8
     */
    public final int getAndUpdate(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            // 如果下一个可用值没获取到，则使用函数进行计算。
            if (!haveNext){
                next = updateFunction.applyAsInt(prev);
            }
            // 此时的next永远是个可用值
            // 如果使用 cas 操作成功更新指定值、则返回旧值
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            // 如果 值没边(a ->b -> a)，
            // 则将haveNext标记为true、不会再执行updateFunction函数
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     *
     * @param updateFunction a side-effect-free function
     * @return the updated value
     * @since 1.8
     */
    public final int updateAndGet(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.applyAsInt(prev);
            if (weakCompareAndSetVolatile(prev, next))
                // 和 getAndUpdate 的唯一区别
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * todo 非常有用。
     *
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function to the current and given values,
     * returning the previous value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function is
     * applied with the current value as its first argument, and the
     * given update as the second argument.
     *
     * @param x the update value
     *          使用当前值和x、计算出下一个值。
     *
     * @param accumulatorFunction a side-effect-free function of two arguments
     *                            int applyAsInt(int left, int right)
     *
     * @return the previous value
     *         返回之前的值。
     */
    public final int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            // 是否计算出类下一个有效值。
            if (!haveNext){
                next = accumulatorFunction.applyAsInt(prev, x);
            }
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function to the current and given values,
     * returning the updated value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function is
     * applied with the current value as its first argument, and the
     * given update as the second argument.
     *
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the updated value
     * @since 1.8
     */
    public final int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext){
                next = accumulatorFunction.applyAsInt(prev, x);
            }
            // 返回新值
            if (weakCompareAndSetVolatile(prev, next)){
                return next;
            }
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Integer.toString(get());
    }

    /**
     * Returns the current value of this {@code AtomicInteger} as an
     * {@code int},
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * Equivalent to {@link #get()}.
     */
    public int intValue() {
        return get();
    }

    /**
     * Returns the current value of this {@code AtomicInteger} as a
     * {@code long} after a widening primitive conversion,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     * @jls 5.1.2 Widening Primitive Conversion
     */
    public long longValue() {
        return (long)get();
    }

    /**
     * Returns the current value of this {@code AtomicInteger} as a
     * {@code float} after a widening primitive conversion,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     * @jls 5.1.2 Widening Primitive Conversion
     */
    public float floatValue() {
        return (float)get();
    }

    /**
     * Returns the current value of this {@code AtomicInteger} as a
     * {@code double} after a widening primitive conversion,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     * @jls 5.1.2 Widening Primitive Conversion
     */
    public double doubleValue() {
        return (double)get();
    }

    // jdk9

    /**
     * Returns the current value, with memory semantics
     * of reading as if the variable was declared non-{@code volatile}.
     *
     * @return the value
     * @since 9
     */
    public final int getPlain() {
        return U.getInt(this, VALUE_OFFSET);
    }

    /**
     * Sets the value to {@code newValue}, with memory semantics
     * of setting as if the variable was declared non-{@code volatile}
     * and non-{@code final}.
     *
     * @param newValue the new value
     * @since 9
     */
    public final void setPlain(int newValue) {
        U.putInt(this, VALUE_OFFSET, newValue);
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getOpaque}.
     *
     * @return the value
     * @since 9
     */
    public final int getOpaque() {
        return U.getIntOpaque(this, VALUE_OFFSET);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setOpaque}.
     *
     * @param newValue the new value
     * @since 9
     */
    public final void setOpaque(int newValue) {
        U.putIntOpaque(this, VALUE_OFFSET, newValue);
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getAcquire}.
     *
     * @return the value
     * @since 9
     */
    public final int getAcquire() {
        return U.getIntAcquire(this, VALUE_OFFSET);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setRelease}.
     *
     * @param newValue the new value
     * @since 9
     */
    public final void setRelease(int newValue) {
        U.putIntRelease(this, VALUE_OFFSET, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchange}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the witness value, which will be the same as the
     * expected value if successful
     * @since 9
     */
    public final int compareAndExchange(int expectedValue, int newValue) {
        return U.compareAndExchangeInt(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchangeAcquire}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the witness value, which will be the same as the
     * expected value if successful
     * @since 9
     */
    public final int compareAndExchangeAcquire(int expectedValue, int newValue) {
        return U.compareAndExchangeIntAcquire(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchangeRelease}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the witness value, which will be the same as the
     * expected value if successful
     * @since 9
     */
    public final int compareAndExchangeRelease(int expectedValue, int newValue) {
        return U.compareAndExchangeIntRelease(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if
     * the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    public final boolean weakCompareAndSetVolatile(int expectedValue, int newValue) {
        return U.weakCompareAndSetInt(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if
     * the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSetAcquire}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    public final boolean weakCompareAndSetAcquire(int expectedValue, int newValue) {
        return U.weakCompareAndSetIntAcquire(this, VALUE_OFFSET, expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if
     * the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSetRelease}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    public final boolean weakCompareAndSetRelease(int expectedValue, int newValue) {
        return U.weakCompareAndSetIntRelease(this, VALUE_OFFSET, expectedValue, newValue);
    }

}
