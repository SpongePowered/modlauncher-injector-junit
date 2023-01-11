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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A JUnit extension booting ModLauncher and executing tests inside the transforming class loader.
 */
public abstract class ModLauncherExtension implements MethodInvocationInterceptor {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T interceptTestClassConstructor(Invocation<T> invocation, ReflectiveInvocationContext<Constructor<T>> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        invocation.skip();
        return (T) ReflectionUtil.makeAccessible(getTransformedConstructor(invocationContext.getExecutable()))
                .newInstance(invocationContext.getArguments().toArray());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T interceptMethod(Invocation<T> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        invocation.skip();
        return (T) ReflectionUtil.makeAccessible(getTransformedMethod(invocationContext.getExecutable())).invoke(
                invocationContext.getTarget().orElse(null), invocationContext.getArguments().toArray());
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
