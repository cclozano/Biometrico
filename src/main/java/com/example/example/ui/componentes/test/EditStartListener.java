package com.example.example.ui.componentes.test;

import java.io.Serializable;


public interface EditStartListener<T> extends Serializable {

    //public static final Method EDIT_START_METHOD = ReflectTools.findMethod(EditStartListener.class, "editStart", EditStartEvent.class);

    public void editStart(T item);

}