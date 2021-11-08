/*******************************************************************************
 * Copyright (c) 2021 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.rdf4j.spring.uuidsource.predictable;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @since 4.0.0
 * @author Florian Kleedorfer
 */
@ConfigurationProperties(prefix = "rdf4j.spring.uuidsource.predictable")
public class PredictableUUIDSourceProperties {

	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
