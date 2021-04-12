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
 * An object that executes submitted {@link Runnable} tasks.
 * This interface provides a way of decoupling(解藕) task submission
 * from the mechanics of how each task will be run, including details
 * of thread use, scheduling, etc. An {@code Executor} is normally used
 * instead of explicitly(明确的) creating threads. For example, rather than
 * invoking {@code new Thread(new RunnableTask()).start()} for each
 * of a set of tasks, you might use:
 *
 * <pre> {@code
 * Executor executor = anExecutor();
 * executor.execute(new RunnableTask1());
 * executor.execute(new RunnableTask2());
 * ...}</pre>
 *
 * fixme 一个用来执行提交的任务的类。
 *       该类解藕了任务的提交和执行，包括怎么管理、调度线程来执行这些任务。
 *       线程池经常替代直接创建线程。
 *
 * However, the {@code Executor} interface does not strictly require
 * that execution be asynchronous. In the simplest case, an executor
 * can run the submitted task immediately in the caller's thread:
 * <pre>
 *     demo_1
 *     {@code
 * class DirectExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     r.run();
 *   }
 * }}</pre>
 *
 * More typically, tasks are executed in some thread other than the
 * caller's thread.  The executor below spawns(繁衍) a new thread for each
 * task.
 *
 * <pre>
 *     demo_2
 *     {@code
 * class ThreadPerTaskExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     new Thread(r).start();
 *   }
 * }}</pre>
 *
 *  * fixme
 *  *      线程池也不一定在另一个线程中异步执行任务，例如demo_1、是在调用线程中执行任务的；
 *         但一般还是像demo_2一样，在新建的线程中执行任务。
 *  *
 *
 * Many {@code Executor} implementations impose some sort of
 * limitation on how and when tasks are scheduled.  The executor below
 * serializes the submission of tasks to a second executor,
 * illustrating a composite executor.
 *
 * fixme
 *      很多线程池的实现都实现了 任务什么时候可以被调度 的功能，
 *      如下线程池。
 *
 * <pre> {@code
 * class SerialExecutor implements Executor {
 *   final Queue<Runnable> tasks = new ArrayDeque<>();
 *   final Executor executor;
 *   Runnable active;
 *
 *   SerialExecutor(Executor executor) {
 *     this.executor = executor;
 *   }
 *
 *   public synchronized void execute(Runnable r) {
 *     tasks.add(() -> {
 *       try {
 *         r.run();
 *       } finally {
 *         // fixme 执行完任务还在检查下任务队列
 *         scheduleNext();
 *       }
 *     });
 *     if (active == null) {
 *       scheduleNext();
 *     }
 *   }
 *
 *   protected synchronized void scheduleNext() {
 *     if ((active = tasks.poll()) != null) {
 *       executor.execute(active);
 *     }
 *   }
 * }}</pre>
 *
 * The {@code Executor} implementations provided in this package
 * implement {@link ExecutorService}, which is a more extensive
 * interface.  The {@link ThreadPoolExecutor} class provides an
 * extensible thread pool implementation. The {@link Executors} class
 * provides convenient factory methods for these Executors.
 *
 * <p>Memory consistency effects: Actions in a thread prior to
 * submitting a {@code Runnable} object to an {@code Executor}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * its execution begins, perhaps in another thread.
 * fixme
 *      该包下实现该接口的方法都是通过实现 ExecutorService 接口、该接口
 *      提供了更丰富的功能。 {@link ThreadPoolExecutor} 提供了可扩展的线程池实现。
 *      The {@link Executors} 类提供了方便的工厂方法来创建线程池。
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Executor {

    /**
     * Executes the given command at some time in the future.
     * The command may execute in a new thread, in a pooled thread, or in the calling thread,
     * at the discretion of the {@code Executor} implementation.
     * fixme
     *      执行提交的任务。任务可能被一个新建的线程、当前线程值线程或者 任务提交线程执行。
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
