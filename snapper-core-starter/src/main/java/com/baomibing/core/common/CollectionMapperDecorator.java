/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.common;


import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 集合数据转化
 * 
 * @author zening
 * @since 1.0.0
 */
@AllArgsConstructor
public class CollectionMapperDecorator {
	private Mapper baseMapper;


	public <T> Collection<T> mapCollection(Object[] source, Class<T> destinationClass) {
		return mapCollection(Arrays.asList(source), destinationClass);
    }
	public <T> Collection<T> mapCollection(Object[] source, Collection<T> destination, Class<T> destinationClass) {
		return mapCollection(Arrays.asList(source), destination, destinationClass);
    }

	public <T> Collection<T> mapCollection(Collection<? extends Object> source, Class<T> destinationClass) {      
		return mapCollection(source, null, destinationClass);
    }   

	public <T> Collection<T> mapCollection(Collection<? extends Object> source, Collection<T> destination, Class<T> destinationClass) {
      if(destination == null) {
         destination = new ArrayList<T>();
      }
      for(Object sourceObj : source) {
         destination.add(map(sourceObj, destinationClass));
      }

      return destination;      
    }

	public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException {
		return baseMapper.map(source, destinationClass, mapId);
    }

	public <T> T map(Object source, Class<T> destinationClass) throws MappingException {

		return baseMapper.map(source, destinationClass);
    }

	public void map(Object source, Object destination, String mapId) throws MappingException {
		baseMapper.map(source, destination, mapId);
    }

	public void map(Object source, Object destination) throws MappingException {
		baseMapper.map(source, destination);
    }

}