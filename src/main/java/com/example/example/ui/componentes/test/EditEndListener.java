package com.example.example.ui.componentes.test;

import java.io.Serializable;

public interface EditEndListener<T> extends Serializable {

    public void editEnd(T item);

}