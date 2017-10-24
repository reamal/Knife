package com.complier.bravo;

import javax.lang.model.type.TypeMirror;

public class FieldViewBinding {
    private String name;//  textview
    private TypeMirror type ;//--->TextView
    private int resId;//--->R.id.textiew

    public FieldViewBinding(String name, TypeMirror type, int resId) {
        this.name = name;
        this.type = type;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public TypeMirror getType() {
        return type;
    }

    public int getResId() {
        return resId;
    }
}
