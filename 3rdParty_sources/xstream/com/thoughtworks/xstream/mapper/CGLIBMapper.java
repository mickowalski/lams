/*
 * Copyright (C) 2006, 2007, 2008, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import net.sf.cglib.proxy.Enhancer;


/**
 * Mapper that detects proxies generated by the CGLIB enhancer. The implementation modifies the name, so that it can
 * identify these types. Note, that this mapper relies on the CGLIB converters:
 * <ul>
 * <li>CGLIBEnhancedConverter</li>
 * </ul>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CGLIBMapper extends MapperWrapper {

    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
    private final String alias;

    public interface Marker {}

    public CGLIBMapper(final Mapper wrapped) {
        this(wrapped, "CGLIB-enhanced-proxy");
    }

    public CGLIBMapper(final Mapper wrapped, final String alias) {
        super(wrapped);
        this.alias = alias;
    }

    @Override
    public String serializedClass(final Class<?> type) {
        final String serializedName = super.serializedClass(type);
        if (type == null) {
            return serializedName;
        }
        final String typeName = type.getName();
        return typeName.equals(serializedName)
            && typeName.indexOf(DEFAULT_NAMING_MARKER) > 0
            && Enhancer.isEnhanced(type) ? alias : serializedName;
    }

    @Override
    public Class<?> realClass(final String elementName) {
        return elementName.equals(alias) ? Marker.class : super.realClass(elementName);
    }
}
