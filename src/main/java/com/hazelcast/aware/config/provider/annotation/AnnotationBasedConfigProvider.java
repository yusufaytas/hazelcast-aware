/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.aware.config.provider.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.hazelcast.aware.config.provider.ConfigProvider;
import com.hazelcast.aware.config.provider.HazelcastAwareConfigProvider;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareClassConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareFieldConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareListFieldConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareMapFieldConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareQueueFieldConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareSetFieldConfigBuilder;
import com.hazelcast.aware.domain.builder.config.HazelcastAwareTopicFieldConfigBuilder;
import com.hazelcast.aware.domain.model.config.HazelcastAwareClassConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareListFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareMapFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareQueueFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareSetFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareTopicFieldConfig;
import com.hazelcast.aware.initializer.HazelcastAwareInitializer;
import com.hazelcast.aware.injector.HazelcastAwareInjector;
import com.hazelcast.aware.processor.HazelcastAwareProcessor;
import com.hazelcast.aware.scanner.HazelcastAwareScanner;
import com.hazelcast.aware.scanner.HazelcastAwareScannerFactory;
import com.hazelcast.aware.util.ReflectionUtil;
import com.hazelcast.core.ITopic;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
public class AnnotationBasedConfigProvider implements ConfigProvider {

	protected final ILogger logger = Logger.getLogger(getClass());
	
	protected final HazelcastAwareScanner hazelcastAwareScanner = 
			HazelcastAwareScannerFactory.getHazelcastAwareScanner();
	
	protected Map<Field, HazelcastAwareFieldConfig> fieldConfigMap = 
				new ConcurrentHashMap<Field, HazelcastAwareFieldConfig>();
	protected Map<Class<?>, HazelcastAwareClassConfig> classConfigMap = 
				new ConcurrentHashMap<Class<?>, HazelcastAwareClassConfig>();
	
	protected Set<Class<?>> hazelcastAwareClasses;
	protected Set<Class<? extends HazelcastAwareConfigProvider>> hazelcastAwareConfigProviderClasses;
	protected Set<Class<? extends HazelcastAwareProcessor>> hazelcastAwareProcessorClasses;
	protected Set<Class<? extends HazelcastAwareInjector<?>>> hazelcastAwareInjectorClasses;
	protected Set<Class<? extends HazelcastAwareInitializer>> hazelcastAwareInitializerClasses;
	
	public AnnotationBasedConfigProvider() {
		init();
	}
	
	protected void init() {
		scanHazelcastAwareClasses();
		findHazelcastAwareConfigProviderClasses();
		findHazelcastAwareProcessorClasses();
		findHazelcastAwareInjectorClasses();
		findHazelcastAwareInitializerClasses();
	}
	
	protected void scanHazelcastAwareClasses() {
		logger.log(
				Level.INFO, 
				"Scanning started for Hazelcast-Aware classes ..."); 
		long start = System.currentTimeMillis();
		hazelcastAwareClasses = 
				new HashSet<Class<?>>(
						hazelcastAwareScanner.getHazelcastAwareClasses());
		long finish = System.currentTimeMillis();
		logger.log(
				Level.INFO, 
				"Scanning finished for Hazelcast-Aware classes in " + 
						(finish - start) + " milliseconds"); 
	}
	
	@SuppressWarnings("unchecked")
	protected void findHazelcastAwareConfigProviderClasses() {
		if (hazelcastAwareClasses != null) {
			hazelcastAwareConfigProviderClasses = 
					new HashSet<Class<? extends HazelcastAwareConfigProvider>>();
			for (Class<?> hazelcastAwareClass : hazelcastAwareClasses) {
				if (HazelcastAwareConfigProvider.class.isAssignableFrom(hazelcastAwareClass)) {
					hazelcastAwareConfigProviderClasses.add(
							(Class<? extends HazelcastAwareConfigProvider>) hazelcastAwareClass);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void findHazelcastAwareProcessorClasses() {
		if (hazelcastAwareClasses != null) {
			hazelcastAwareProcessorClasses = 
					new HashSet<Class<? extends HazelcastAwareProcessor>>();
			for (Class<?> hazelcastAwareClass : hazelcastAwareClasses) {
				if (HazelcastAwareProcessor.class.isAssignableFrom(hazelcastAwareClass)) {
					hazelcastAwareProcessorClasses.add(
							(Class<? extends HazelcastAwareProcessor>) hazelcastAwareClass);
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	protected void findHazelcastAwareInjectorClasses() {
		if (hazelcastAwareClasses != null) {
			hazelcastAwareInjectorClasses = 
					new HashSet<Class<? extends HazelcastAwareInjector<?>>>();
			for (Class<?> hazelcastAwareClass : hazelcastAwareClasses) {
				if (HazelcastAwareInjector.class.isAssignableFrom(hazelcastAwareClass)) {
					hazelcastAwareInjectorClasses.add(
							(Class<? extends HazelcastAwareInjector<?>>) hazelcastAwareClass);
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	protected void findHazelcastAwareInitializerClasses() {
		if (hazelcastAwareClasses != null) {
			hazelcastAwareInitializerClasses = 
					new HashSet<Class<? extends HazelcastAwareInitializer>>();
			for (Class<?> hazelcastAwareClass : hazelcastAwareClasses) {
				if (HazelcastAwareInitializer.class.isAssignableFrom(hazelcastAwareClass)) {
					hazelcastAwareInitializerClasses.add(
							(Class<? extends HazelcastAwareInitializer>) hazelcastAwareClass);
				}
			}
		}
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public Set<Class<?>> getHazelcastAwareClasses() {
		return hazelcastAwareClasses;
	}
	
	@Override
	public Set<Class<? extends HazelcastAwareConfigProvider>> getHazelcastAwareConfigProviderClasses() {
		return hazelcastAwareConfigProviderClasses;
	}
	
	@Override
	public Set<Class<? extends HazelcastAwareProcessor>> getHazelcastAwareProcessorClasses() {
		return hazelcastAwareProcessorClasses;
	}
	
	@Override
	public Set<Class<? extends HazelcastAwareInjector<?>>> getHazelcastAwareInjectorClasses() {
		return hazelcastAwareInjectorClasses;
	}
	
	@Override
	public Set<Class<? extends HazelcastAwareInitializer>> getHazelcastAwareInitializerClasses() {
		return hazelcastAwareInitializerClasses;
	}
	
	@Override
	public HazelcastAwareFieldConfig getHazelcastAwareFieldConfig(Field field) {
		HazelcastAwareFieldConfig fieldConfig = fieldConfigMap.get(field);
		if (fieldConfig == null) {
			fieldConfig = findHazelcastAwareFieldConfig(field);
			if (fieldConfig != null) {
				fieldConfigMap.put(field, fieldConfig);
			}
			else {
				return null;
			}
		}
		return fieldConfig;
	}
	
	protected HazelcastAwareFieldConfig findHazelcastAwareFieldConfig(Field field) {
		field.setAccessible(true);
		Annotation[] fieldAnnotations = field.getAnnotations();
		boolean isHazelcastAwareField = false;
		for (Annotation a : fieldAnnotations) {
			if (a.annotationType().isAnnotationPresent(HazelcastAwareAnnotation.class)) {
				isHazelcastAwareField = true;
				break;
			}
		}
		HazelcastAwareField haf = field.getAnnotation(HazelcastAwareField.class);
		String instanceName = haf != null ? haf.instanceName() : null;
		if (isHazelcastAwareField) {
			return 
				new HazelcastAwareFieldConfigBuilder().
						ownerClass(field.getDeclaringClass()).
						field(field).
						instanceName(instanceName).
						mapFieldConfig(findHazelcastAwareMapFieldConfig(field)).
						listFieldConfig(findHazelcastAwareListFieldConfig(field)).
						setFieldConfig(findHazelcastAwareSetFieldConfig(field)).
						queueFieldConfig(findHazelcastAwareQueueFieldConfig(field)).
						topicFieldConfig(findHazelcastAwareTopicFieldConfig(field)).
					build();
		}
		else {
			return null;
		}
	}
	
	protected HazelcastAwareMapFieldConfig findHazelcastAwareMapFieldConfig(Field field) {
		field.setAccessible(true);
		HazelcastAwareMapField hamf = field.getAnnotation(HazelcastAwareMapField.class);
		if (hamf != null) {
			if (Map.class.isAssignableFrom(field.getType())) {
				return
					new HazelcastAwareMapFieldConfigBuilder().
							name(hamf.name()).
						build();
			}
			else {
				logger.log(
						Level.WARNING,
						"Since type of field " + field.getName() + 
							" in class " + field.getDeclaringClass().getName() + 
							" is not assignable to Map, map field configuration will be ignored !");
				return null;
			}
		}
		else {
			return null;
		}	
	}
	
	protected HazelcastAwareListFieldConfig findHazelcastAwareListFieldConfig(Field field) {
		field.setAccessible(true);
		HazelcastAwareListField hamf = field.getAnnotation(HazelcastAwareListField.class);
		if (hamf != null) {
			if (List.class.isAssignableFrom(field.getType())) {
				return
					new HazelcastAwareListFieldConfigBuilder().
							name(hamf.name()).
						build();
			}
			else {
				logger.log(
						Level.WARNING,
						"Since type of field " + field.getName() + 
							" in class " + field.getDeclaringClass().getName() + 
							" is not assignable to List, list field configuration will be ignored !");
				return null;
			}	
		}
		else {
			return null;
		}	
	}
	
	protected HazelcastAwareSetFieldConfig findHazelcastAwareSetFieldConfig(Field field) {
		field.setAccessible(true);
		HazelcastAwareSetField hamf = field.getAnnotation(HazelcastAwareSetField.class);
		if (hamf != null) {
			if (Set.class.isAssignableFrom(field.getType())) {
				return
					new HazelcastAwareSetFieldConfigBuilder().
							name(hamf.name()).
						build();
			}
			else {
				logger.log(
						Level.WARNING,
						"Since type of field " + field.getName() + 
							" in class " + field.getDeclaringClass().getName() + 
							" is not assignable to Set, set field configuration will be ignored !");
				return null;
			}		
		}
		else {
			return null;
		}	
	}
	
	protected HazelcastAwareQueueFieldConfig findHazelcastAwareQueueFieldConfig(Field field) {
		field.setAccessible(true);
		HazelcastAwareQueueField hamf = field.getAnnotation(HazelcastAwareQueueField.class);
		if (hamf != null) {
			if (Queue.class.isAssignableFrom(field.getType())) {
				return
					new HazelcastAwareQueueFieldConfigBuilder().
							name(hamf.name()).
						build();
			}	
			else {
				logger.log(
						Level.WARNING,
						"Since type of field " + field.getName() + 
							" in class " + field.getDeclaringClass().getName() + 
							" is not assignable to Queue, queue field configuration will be ignored !");
				return null;
			}			
		}
		else {
			return null;
		}	
	}
	
	protected HazelcastAwareTopicFieldConfig findHazelcastAwareTopicFieldConfig(Field field) {
		field.setAccessible(true);
		HazelcastAwareTopicField hamf = field.getAnnotation(HazelcastAwareTopicField.class);
		if (hamf != null) {
			if (ITopic.class.isAssignableFrom(field.getType())) {
				return
					new HazelcastAwareTopicFieldConfigBuilder().
							name(hamf.name()).
						build();
			}	
			else {
				logger.log(
						Level.WARNING,
						"Since type of field " + field.getName() + 
							" in class " + field.getDeclaringClass().getName() + 
							" is not assignable to ITopic, topic field configuration will be ignored !");
				return null;
			}			
		}
		else {
			return null;
		}	
	}

	@Override
	public HazelcastAwareClassConfig getHazelcastAwareClassConfig(Class<?> clazz) {
		HazelcastAwareClassConfig classConfig = classConfigMap.get(clazz);
		if (classConfig == null) {
			classConfig = findHazelcastAwareClassConfig(clazz);
			if (classConfig != null) {
				classConfigMap.put(clazz, classConfig);
			}
			else {
				return null;
			}
		}
		return classConfig;
	}
	
	protected HazelcastAwareClassConfig findHazelcastAwareClassConfig(Class<?> clazz) {
		HazelcastAwareClass hac = clazz.getAnnotation(HazelcastAwareClass.class);
		if (hac != null) {
			List<Field> fields = ReflectionUtil.getAllFields(clazz);
			List<HazelcastAwareFieldConfig> fieldConfigs = new ArrayList<HazelcastAwareFieldConfig>();
			for (Field field : fields) {
				field.setAccessible(true);
				HazelcastAwareFieldConfig fieldConfig = getHazelcastAwareFieldConfig(field);
				if (fieldConfig != null) {
					fieldConfigs.add(fieldConfig);
				}
			}
			return 
				new HazelcastAwareClassConfigBuilder().
						clazz(clazz).
						instanceName(hac.instanceName()).
						fieldConfigs(fieldConfigs).
					build();
		}
		else {
			return null;
		}	
	}
	
}
