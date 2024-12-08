/**
 * Author : rzr1331
 * Date : 08/04/20
 */
package com.outleap.demo.utils;

import java.lang.annotation.*;

/**
 * Just pass devKey={key} in request parameter of the api.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DeveloperAPI {
    boolean basicAuth() default false;
    String username() default "";
    String password() default "";
}