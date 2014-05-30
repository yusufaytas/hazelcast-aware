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

package com.hazelcast.aware.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hazelcast.aware.config.provider.annotation.HazelcastAwareClass;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ClassFilter;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
public class HazelcastAwareScannerImpl implements HazelcastAwareScanner {
	
	private Set<Class<?>> hazelcastAwareClasses;
	
	@Override
	public synchronized Set<Class<?>> getHazelcastAwareClasses() {
		if (hazelcastAwareClasses == null) {
			List<Class<?>> hazelcastAwareClassList = 
					CPScanner.scanClasses(
							new ClassFilter().
									annotation(HazelcastAwareClass.class));
			if (hazelcastAwareClassList != null) {
				hazelcastAwareClasses = new HashSet<Class<?>>(hazelcastAwareClassList);
			}
		}
		return hazelcastAwareClasses;
	}
	
}
