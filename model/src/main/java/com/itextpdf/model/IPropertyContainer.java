package com.itextpdf.model;

public interface IPropertyContainer<Type extends IPropertyContainer> {
    boolean hasProperty(Property property);
    <T> T getProperty(Property property);
    <T> T getDefaultProperty(Property property);
    <T extends Type> T setProperty(Property property, Object value);
    void deleteProperty(Property property);
}
