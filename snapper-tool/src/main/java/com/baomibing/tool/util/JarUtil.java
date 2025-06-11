/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * JarUtil
 *
 * @author zening 2023/7/19 21:15
 * @version 1.0.0
 **/
public  abstract class JarUtil {

    private static String byGetProtectionDomain(Class clazz) throws URISyntaxException {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        return Paths.get(url.toURI()).toString();
    }

    private static String byGetResource(Class clazz) {
        URL classResource = clazz.getResource(clazz.getSimpleName() + ".class");
        if (classResource == null) {
            throw new RuntimeException("class resource is null");
        }
        String url = classResource.toString();
        if (url.startsWith("jar:file:")) {
            // extract 'file:......jarName.jar' part from the url string
            String path = url.replaceAll("^jar:(file:.*[.]jar)!/.*", "$1");
            try {
                return Paths.get(new URL(path).toURI()).toString();
            } catch (Exception e) {
                throw new RuntimeException("Invalid Jar File URL String");
            }
        }
        throw new RuntimeException("Invalid Jar File URL String");
    }

    public static String getJarFilePath(Class clazz) {
        try {
            return byGetProtectionDomain(clazz);
        } catch (Exception e) {
            // cannot get jar file path using byGetProtectionDomain
            // Exception handling omitted
        }
        return byGetResource(clazz);
    }
}
