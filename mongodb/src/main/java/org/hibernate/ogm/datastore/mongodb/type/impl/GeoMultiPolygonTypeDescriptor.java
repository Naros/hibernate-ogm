/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.datastore.mongodb.type.impl;

import org.hibernate.ogm.datastore.mongodb.type.GeoMultiPolygon;

/**
 * Persists {@link GeoMultiPolygon} in the format expected by MongoDB.
 *
 * @author Guillaume Smet
 */
public class GeoMultiPolygonTypeDescriptor extends AbstractGeoJsonObjectTypeDescriptor<GeoMultiPolygon> {

	public static final GeoMultiPolygonTypeDescriptor INSTANCE = new GeoMultiPolygonTypeDescriptor();

	public GeoMultiPolygonTypeDescriptor() {
		super( GeoMultiPolygon.class );
	}
}
