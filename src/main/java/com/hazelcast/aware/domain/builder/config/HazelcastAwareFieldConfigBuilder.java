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

package com.hazelcast.aware.domain.builder.config;

import java.lang.reflect.Field;

import com.hazelcast.aware.domain.builder.Builder;
import com.hazelcast.aware.domain.model.config.HazelcastAwareFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareListFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareMapFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareQueueFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareSetFieldConfig;
import com.hazelcast.aware.domain.model.config.HazelcastAwareTopicFieldConfig;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
public class HazelcastAwareFieldConfigBuilder implements Builder<HazelcastAwareFieldConfig> {

	private Class<?> ownerClass;
	private Field field;
	private String instanceName;
	private HazelcastAwareMapFieldConfig mapFieldConfig;
	private HazelcastAwareListFieldConfig listFieldConfig;
	private HazelcastAwareSetFieldConfig setFieldConfig;
	private HazelcastAwareQueueFieldConfig queueFieldConfig;
	private HazelcastAwareTopicFieldConfig topicFieldConfig;
	
	public HazelcastAwareFieldConfigBuilder ownerClass(Class<?> ownerClass) {
		this.ownerClass = ownerClass;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder field(Field field) {
		this.field = field;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder instanceName(String instanceName) {
		this.instanceName = instanceName;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder mapFieldConfig(HazelcastAwareMapFieldConfig mapFieldConfig) {
		this.mapFieldConfig = mapFieldConfig;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder listFieldConfig(HazelcastAwareListFieldConfig listFieldConfig) {
		this.listFieldConfig = listFieldConfig;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder setFieldConfig(HazelcastAwareSetFieldConfig setFieldConfig) {
		this.setFieldConfig = setFieldConfig;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder queueFieldConfig(HazelcastAwareQueueFieldConfig queueFieldConfig) {
		this.queueFieldConfig = queueFieldConfig;
		return this;
	}
	
	public HazelcastAwareFieldConfigBuilder topicFieldConfig(HazelcastAwareTopicFieldConfig topicFieldConfig) {
		this.topicFieldConfig = topicFieldConfig;
		return this;
	}
	
	@Override
	public HazelcastAwareFieldConfig build() {
		HazelcastAwareFieldConfig config = new HazelcastAwareFieldConfig();
		config.setOwnerClass(ownerClass);
		config.setField(field);
		config.setInstanceName(instanceName);
		config.setMapFieldConfig(mapFieldConfig);
		config.setListFieldConfig(listFieldConfig);
		config.setSetFieldConfig(setFieldConfig);
		config.setQueueFieldConfig(queueFieldConfig);
		config.setTopicFieldConfig(topicFieldConfig);
		return config;
	}
	
}
