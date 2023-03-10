/*
 * This file is part of modlauncher-injector-junit, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://github.com/SpongePowered>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.mij;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A JUnit extension booting ModLauncher and executing tests inside the transforming class loader.
 */
public abstract class ModLauncherExtension implements MethodInvocationInterceptor {
    private final Map<Object, Object> originalToTransformedInstances = new WeakHashMap<>();

    @Override
    public <T> T interceptTestClassConstructor(Invocation<T> invocation, ReflectiveInvocationContext<Constructor<T>> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        final T originalInstance = invocation.proceed();

        final Thread thread = Thread.currentThread();
        final ClassLoader originalClassLoader = thread.getContextClassLoader();

        final Object transformedInstance;
        try {
            thread.setContextClassLoader(getTransformingClassLoader());
            transformedInstance = ReflectionUtils.newInstance(getTransformedConstructor(invocationContext.getExecutable()),
                    invocationContext.getArguments().toArray());
        } finally {
            thread.setContextClassLoader(originalClassLoader);
        }

        this.originalToTransformedInstances.put(originalInstance, transformedInstance);
        return originalInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T interceptMethod(Invocation<T> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        invocation.skip();

        final Thread thread = Thread.currentThread();
        final ClassLoader originalClassLoader = thread.getContextClassLoader();

        try {
            thread.setContextClassLoader(getTransformingClassLoader());
            return (T) ReflectionUtils.invokeMethod(getTransformedMethod(invocationContext.getExecutable()),
                    getTransformedInstance(invocationContext.getTarget().orElse(null)), invocationContext.getArguments().toArray());
        } finally {
            thread.setContextClassLoader(originalClassLoader);
        }
    }

    protected Object getTransformedInstance(Object originalInstance) {
        if (originalInstance == null) {
            return null;
        }

        final Object transformedInstance = this.originalToTransformedInstances.get(originalInstance);
        if (transformedInstance == null) {
            throw new TestInstantiationException("Could not find transformed instance for " + originalInstance);
        }

        return transformedInstance;
    }

    protected Constructor<?> getTransformedConstructor(Constructor<?> originalConstructor) {
        try {
            return getTransformedClass(originalConstructor.getDeclaringClass()).getDeclaredConstructor(originalConstructor.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new TestInstantiationException("Could not create transformed constructor", e);
        }
    }

    protected Method getTransformedMethod(Method originalMethod) {
        try {
            return getTransformedClass(originalMethod.getDeclaringClass()).getDeclaredMethod(originalMethod.getName(), originalMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new TestInstantiationException("Could not create transformed method", e);
        }
    }

    protected Class<?> getTransformedClass(Class<?> originalClass) {
        try {
            return getTransformingClassLoader().loadClass(originalClass.getName());
        } catch (ClassNotFoundException e) {
            throw new TestInstantiationException("Could not create transformed class", e);
        }
    }

    /**
     * Gets the transforming class loader in which tests are executed.
     * A typical implementation of this method will invoke {@link SharedModLauncher#getTransformingClassLoader(String[])}
     * with the launch target set in the arguments.
     *
     * @return The transforming class loader
     */
    protected abstract ClassLoader getTransformingClassLoader();
}
