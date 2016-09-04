package com.masyaman.datapack.annotations.instances;

import java.lang.annotation.Annotation;

public abstract class AbstractAnnotationInstance implements Annotation {

    private final Class<? extends Annotation> annotationType;

    protected AbstractAnnotationInstance() {
        Annotation[] annotations = getClass().getAnnotations();
        if (annotations.length != 1) {
            throw new IllegalArgumentException("Class should be annotates exactly with one annotation");
        }
        annotationType = annotations[0].annotationType();
        if (!annotationType.isAssignableFrom(getClass())) {
            throw new IllegalArgumentException("Class should implement it's annotation");
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }
}
