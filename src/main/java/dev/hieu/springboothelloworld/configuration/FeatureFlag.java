package dev.hieu.springboothelloworld.configuration;

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
     * Controls write operations on todos (create, update, delete).
     */
    TODO_WRITE_API,

    /**
     * Controls the search API (/api/todos/search) and search functionality in the UI.
     */
    TODO_SEARCH_API
}


