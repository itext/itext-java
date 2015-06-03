package com.itextpdf.model;

public interface IPropertyContainer<Type extends IPropertyContainer> {
    <T> T getProperty(int propertyKey);
    <T> T getDefaultProperty(int propertyKey);
    <T extends Type> T setProperty(int propertyKey, Object value);
}
