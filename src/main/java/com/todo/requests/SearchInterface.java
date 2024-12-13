package com.todo.requests;

public interface SearchInterface {
    Object getAll (Integer offset, Integer limit); //params should be optional?
    Object getAll ();
}
