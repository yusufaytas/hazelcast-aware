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

package com.hazelcast.aware.config.manager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.hazelcast.aware.config.provider.ConfigProvider;
import com.hazelcast.aware.config.provider.HazelcastAwareConfigProvider;
import com.hazelcast.aware.config.provider.annotation.AnnotationBasedConfigProvider;
import com.hazelcast.aware.config.provider.properties.PropertiesBasedConfigProvider;
import com.hazelcast.aware.config.provider.xml.XmlBasedConfigProvider;
import com.hazelcast.aware.domain.model.config.HazelcastAwareClassConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareFieldConfig;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
public class ConfigManagerImpl extends BaseConfigManager {

	protected Set<Class<?>> hazelcastAwareClasses;
	protected Set<Class<? extends HazelcastAwareConfigProvider>> hazelcastAwareConfigProviderClasses;
	
	@Override
	protected void init() {
		addConfigProviderIfAvailable(new AnnotationBasedConfigProvider());
		addConfigProviderIfAvailable(new XmlBasedConfigProvider());
		addConfigProviderIfAvailable(new PropertiesBasedConfigProvider());
		
		findHazelcastAwareClasses();
		findHazelcastAwareConfigProviderClasses();
	}
	
	protected void findHazelcastAwareClasses() {
		hazelcastAwareClasses = new HashSet<Class<?>>();
		for (ConfigProvider configProvider : configProviderList) {
			if (configProvider.isAvailable()) {
				Set<Class<?>> hzAwareClasses = configProvider.getHazelcastAwareClasses();
				if (hzAwareClasses != null) {
					hazelcastAwareClasses.addAll(hzAwareClasses);
				}	
			}
		}	
		logger.log(Level.INFO, "Found Hazelcast-Aware classes: " + hazelcastAwareClasses);
	}
	
	protected void findHazelcastAwareConfigProviderClasses() {
		hazelcastAwareConfigProviderClasses = new HashSet<Class<? extends HazelcastAwareConfigProvider>>();
		for (ConfigProvider configProvider : configProviderList) {
			if (configProvider.isAvailable()) {
				Set<Class<? extends HazelcastAwareConfigProvider>> hzAwareConfigProviderClasses = 
						configProvider.getHazelcastAwareConfigProviderClasses();
				if (hzAwareConfigProviderClasses != null) {
					hazelcastAwareConfigProviderClasses.addAll(hzAwareConfigProviderClasses);
				}	
			}
		}	
		logger.log(Level.INFO, "Found Hazelcast-Aware Config Provider classes: " + hazelcastAwareConfigProviderClasses);
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
	public HazelcastAwareFieldConfig getHazelcastAwareFieldConfig(Field field) {
		HazelcastAwareFieldConfig fieldConfig = null;
		for (ConfigProvider configProvider : configProviderList) {
			if (configProvider.isAvailable()) {
				if (fieldConfig == null) {
					fieldConfig = configProvider.getHazelcastAwareFieldConfig(field);
				}
				else {
					HazelcastAwareFieldConfig config = configProvider.getHazelcastAwareFieldConfig(field);
					if (config != null) {
						fieldConfig = fieldConfig.merge(config);
					}
				}
			}
		}
		return fieldConfig;
	}

	@Override
	public HazelcastAwareClassConfig getHazelcastAwareClassConfig(Class<?> clazz) {
		HazelcastAwareClassConfig classConfig = null;
		for (ConfigProvider configProvider : configProviderList) {
			if (configProvider.isAvailable()) {
				if (classConfig == null) {
					classConfig = configProvider.getHazelcastAwareClassConfig(clazz);
				}
				else {
					HazelcastAwareClassConfig config = configProvider.getHazelcastAwareClassConfig(clazz);
					if (config != null) {
						classConfig = classConfig.merge(config);
					}
				}
			}
		}
		return classConfig;
	}
	
}
