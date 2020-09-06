package com.jason.myapp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qiuzi on 16/5/4.
 */
public class BeanUtil {

    public static void copy(Object source, Object target) throws Exception {

        Class sourceClz = source.getClass();

        Class targetClz = target.getClass();

        // 得到Class对象所表征的类的所有属性(包括私有属性)
        Field[] fields = sourceClz.getDeclaredFields();

        if (fields.length == 0) {
//            fields = sourceClz.getSuperclass().getDeclaredFields();
            return;
        }
        for (int i = 0; i < fields.length; i++) {

            String fieldName = fields[i].getName();

            Field targetField = null;

            // 得到targetClz对象所表征的类的名为fieldName的属性，不存在就进入下次循环
            try {
                targetField = targetClz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
//                targetField = targetClz.getSuperclass().getDeclaredField(fieldName);
                continue;
            }
            // 判断sourceClz字段类型和targetClz同名字段类型是否相同
            if (fields[i].getType() == targetField.getType()) {

                // 由属性名字得到对应get和set方法的名字
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                String setMethodName = "set"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                // 由方法的名字得到get和set方法的Method对象
                Method getMethod;
                Method setMethod;
                try {

                    try {
                        getMethod = sourceClz.getDeclaredMethod(getMethodName, new Class[]{});

                    } catch (NoSuchMethodException e) {
                        getMethod = sourceClz.getSuperclass().getDeclaredMethod(getMethodName, new Class[]{});
                    }

                    try {
                        setMethod = targetClz.getDeclaredMethod(setMethodName, fields[i].getType());

                    } catch (NoSuchMethodException e) {
                        setMethod = targetClz.getSuperclass().getDeclaredMethod(setMethodName, fields[i].getType());
                    }

                    // 调用source对象的getMethod方法
                    Object result = getMethod.invoke(source, new Object[]{});

                    // 调用target对象的setMethod方法
                    setMethod.invoke(target, result);

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                throw new Exception("同名属性类型不匹配！");
            }

        }

    }
}
