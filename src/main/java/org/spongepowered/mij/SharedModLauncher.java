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

import cpw.mods.modlauncher.Launcher;

/**
 * ModLauncher and Mixin can only be initialized once.
 * Consequently, all tests use a single shared transforming class loader.
 */
public class SharedModLauncher {
    private static ClassLoader transformingClassLoader;
    private static boolean launcherFailed = false;

    /**
     * Returns the shared transforming class loader.
     * If not yet initialized, ModLauncher is bootstrapped using the provided arguments.
     * When ModLauncher is already initialized, the provided arguments are ignored.
     *
     * @param launcherArgs The arguments passed to ModLauncher's entry point
     * @return The transforming class loader
     */
    public static ClassLoader getTransformingClassLoader(String[] launcherArgs) {
        if (SharedModLauncher.transformingClassLoader == null) {
            if (SharedModLauncher.launcherFailed) {
                // Don't attempt to relaunch ModLaunch when it failed
                throw new IllegalStateException("ModLauncher has previously failed to launch");
            }

            final Thread thread = Thread.currentThread();
            final ClassLoader originalClassLoader = thread.getContextClassLoader();

            try {
                Launcher.main(launcherArgs);

                // Capture the context class loader set by ModLauncher
                SharedModLauncher.transformingClassLoader = thread.getContextClassLoader();
            } catch (RuntimeException e) {
                SharedModLauncher.launcherFailed = true;
                throw e;
            } finally {
                // Restore the original context class loader to avoid potential issues with classic tests
                thread.setContextClassLoader(originalClassLoader);
            }
        }

        return SharedModLauncher.transformingClassLoader;
    }
}
