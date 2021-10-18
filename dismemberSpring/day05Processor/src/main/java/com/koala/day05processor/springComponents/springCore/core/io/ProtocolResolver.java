package com.koala.day05processor.springComponents.springCore.core.io;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-09-08
 */
@FunctionalInterface
public interface ProtocolResolver {

    /**
     * Resolve the given location against the given resource loader
     * if this implementation's protocol matches.
     * @param location the user-specified resource location
     * @param resourceLoader the associated resource loader
     * @return a corresponding {@code Resource} handle if the given location
     * matches this resolver's protocol, or {@code null} otherwise
     */
    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);

}

