package com.example.communitask.util;

/**
 * Generic holder for screen state that later ViewModels can expose.
 */
public class UiState<T> {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String errorMessage;

    private UiState(Status status, T data, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> UiState<T> idle() {
        return new UiState<>(Status.IDLE, null, null);
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> error(String errorMessage) {
        return new UiState<>(Status.ERROR, null, errorMessage);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
