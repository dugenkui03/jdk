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

package java.util.concurrent;

/**
 * A {@code Future} represents the result of an asynchronous computation.
 * Methods are provided to check if the computation is complete,
 * to wait for its completion, and to retrieve the result of the computation.
 * The result can only be retrieved using method {@code get} when the computation has completed,
 * blocking if necessary until it is ready.
 * fixme
 *      Future/将来 代表异步任务的结果，可进行等待获取、取消、查看异步任务是否已经结束等操作。
 *
 *
 * Cancellation is performed by the {@code cancel} method.
 * Additional methods are provided to determine if the task completed normally
 * or was cancelled. Once a computation has completed, the computation cannot be cancelled.
 * If you would like to use a {@code Future} for the sake
 * of cancellability but not provide a usable result, you can
 * declare types of the form {@code Future<?>} and
 * return {@code null} as a result of the underlying task.
 *
 * <p><b>Sample Usage</b> (Note that the following classes are all
 * made-up.)
 *
 * <pre> {@code
 * interface ArchiveSearcher { String search(String target); }
 * class App {
 *   ExecutorService executor = ...
 *   ArchiveSearcher searcher = ...
 *   void showSearch(String target) throws InterruptedException {
 *     Callable<String> task = () -> searcher.search(target);
 *     Future<String> future = executor.submit(task);
 *     displayOtherThings(); // do other things while searching
 *     try {
 *       displayText(future.get()); // use future
 *     } catch (ExecutionException ex) { cleanup(); return; }
 *   }
 * }}</pre>
 *
 * The {@link FutureTask} class is an implementation of {@code Future} that
 * implements {@code Runnable}, and so may be executed by an {@code Executor}.
 * For example, the above construction with {@code submit} could be replaced by:
 * <pre> {@code
 * FutureTask<String> future = new FutureTask<>(task);
 * executor.execute(future);}</pre>
 *
 * <p>Memory consistency effects: Actions taken by the asynchronous computation
 * <a href="package-summary.html#MemoryVisibility"> <i>happen-before</i></a>
 * actions following the corresponding {@code Future.get()} in another thread.
 *
 * @see FutureTask
 * @see Executor
 * @since 1.5
 * @author Doug Lea
 * @param <V> The result type returned by this Future's {@code get} method
 */
public interface Future<V> {

    /**
     * Attempts to cancel execution of this task.
     * This attempt will fail if
     *      1. the task has already completed,
     *      2. has already been cancelled,
     *      3. or could not be cancelled for some other reason.
     * fixme
     *      尝试取消任务的执行，如果以下情况将取消失败：
     *          1. 任务已经执行完毕并可以获取结果;
     *          2. 任务已经被取消；
     *          3. 任务因为某些原因不能被取消。
     *
     * If successful, and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     * fixme
     *      如果取消成功，并且当 cancel 被调用的时候任务还没有开始，则该任务将运用不会运行。
     *      如果 cancel 的时候热舞已经运行，则参数 mayInterruptIfRunning 将决定是否中断该线程来取消该任务。
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     * fixme
     *      该任务成功执行返回true后，随后 isDone() 和 isCancelled() 都将返回true。
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     * task should be interrupted; otherwise, in-progress tasks are allowed
     * to complete
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns {@code true} if this task was cancelled before it completed normally.
     * fixme 该任务正常结束前是否被取消了。
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    boolean isCancelled();

    /**
     * Returns {@code true} if this task completed.
     * fixme 结束、异常和 cancel 的情况：任务是否结束
     *
     * Completion may be due to normal termination, an exception, or cancellation
     * -- in all of these cases, this method will return {@code true}.
     *
     * @return {@code true} if this task completed
     */
    boolean isDone();

    /**
     * Waits if necessary for the computation to complete,
     * and then retrieves its result.
     * fixme 阻塞获取结果。
     *
     * @return the computed result
     *         fixme 计算的结果
     *
     * @throws CancellationException if the computation was cancelled
     *                               fixme 如果任务被取消，则调用的时候一直返回该异常
     *
     * @throws ExecutionException if the computation threw an exception
     *                            fixme 如果执行遇到异常
     *
     * @throws InterruptedException if the current thread was interrupted while waiting
     *                              fixme 如果当前线程被中断
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     * fixme 等待指定的时间、获取其值。
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     *                               fixme 被调用 {@link #cancel(boolean)} }  取消
     *
     * @throws ExecutionException if the computation threw an exception
     *                            fixme 如果计算抛异常
     *
     * @throws InterruptedException if the current thread was interrupted while waiting
     *                              fixme 在等待过程中、该线程被中断
     *
     * @throws TimeoutException if the wait timed out
     *                          fixme 如果在指定的时间内没有执行结束、则抛该异常
     */
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
