package com.hf.base.annotations;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface Field {
    boolean required() default false;
    String defaults() default "";
    String alias() default "";
    Type type() default Field.Type.varchar;
    String group() default "";

    enum Type {
        varchar,number,date;
    }
}
