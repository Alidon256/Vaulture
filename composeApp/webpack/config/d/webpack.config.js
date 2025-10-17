// This file adds polyfills for Node.js core modules required by some libraries.
config.resolve = {
    ...config.resolve, // Spread existing resolve configuration
    fallback: {
        ...config.resolve.fallback, // Spread existing fallback configuration
        "path": require.resolve("path-browserify"),
        "os": require.resolve("os-browserify/browser")
    }
};
