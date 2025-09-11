package com.civrobotics.inertia;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.FIELD})
public @interface Savable {
    String elementName() default "";

    LoadBehavior loadBehavior() default LoadBehavior.KEEP;

    SaveBehavior saveBehavior() default SaveBehavior.SAVE;
}

