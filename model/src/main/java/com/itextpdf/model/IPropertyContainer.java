package com.itextpdf.model;

public interface IPropertyContainer<Type extends IPropertyContainer> {
    <T> T getProperty(Property property);
    <T> T getDefaultProperty(Property property);
    <T extends Type> T setProperty(Property property, Object value);
}
