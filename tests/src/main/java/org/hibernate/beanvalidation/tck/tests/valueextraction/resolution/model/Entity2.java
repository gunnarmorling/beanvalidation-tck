/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.valueextraction.resolution.model;

import javax.validation.constraints.NotNull;

/**
 * @author Guillaume Smet
 */
public class Entity2 {

	@SuppressWarnings("unused")
	private Wrapper2<@NotNull String> wrapper;

	public Entity2(String value) {
		this.wrapper = new Wrapper2<String>( value );
	}
}
