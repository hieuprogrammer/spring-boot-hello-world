package dev.hieu.springboothelloworld.service.feature;

/**
 * Enumerates all feature flags used in the application.
 * Each flag can be enabled/disabled at runtime via the feature-flag UI and API.
 */
public enum FeatureFlag {

    /**
     * Controls the ping endpoint (/ping).
     */
    PING_API,

    /**
     * Controls logging the README.md content on startup.
     */
    README_LOGGER,

    /**
     * Controls write operations on todos (create, update, delete).
     */
    TODO_WRITE_API,

    /**
     * Controls the search API (/api/todos/search) and search functionality in the UI.
     */
    TODO_SEARCH_API
}
