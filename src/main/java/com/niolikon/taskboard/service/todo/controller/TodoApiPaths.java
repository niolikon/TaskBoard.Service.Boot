package com.niolikon.taskboard.service.todo.controller;

public final class TodoApiPaths {
    private TodoApiPaths() {}

    public static final String PATH_VARIABLE_TODO_ID = "id";

    public static final String MAPPING_PATH_TODO_BASE = "/api/Todos";
    public static final String MAPPING_PATH_TODO_BY_ID = "/{" + PATH_VARIABLE_TODO_ID + "}";
    public static final String MAPPING_PATH_TODO_PENDING = "/pending";
    public static final String MAPPING_PATH_TODO_COMPLETED = "/completed";

    public static final String API_PATH_TODO_BASE = MAPPING_PATH_TODO_BASE;
    public static final String API_PATH_TODO_BY_ID = MAPPING_PATH_TODO_BASE + MAPPING_PATH_TODO_BY_ID;
    public static final String API_PATH_TODO_PENDING = MAPPING_PATH_TODO_BASE + MAPPING_PATH_TODO_PENDING;
    public static final String API_PATH_TODO_COMPLETED = MAPPING_PATH_TODO_BASE + MAPPING_PATH_TODO_COMPLETED;

    public static final String SECURITY_PATTERN_TODO_EXACT = MAPPING_PATH_TODO_BASE;
    public static final String SECURITY_PATTER_TODO_ALL = MAPPING_PATH_TODO_BASE + "/**";
}
