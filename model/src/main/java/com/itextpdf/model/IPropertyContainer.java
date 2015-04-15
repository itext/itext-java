package com.itextpdf.model;

public interface IPropertyContainer {
    <T> T getProperty(Integer propertyKey);
    <T> T getDefaultProperty(Integer propertyKey);
    <T extends IPropertyContainer> T setProperty(Integer propertyKey, Object value);
}
