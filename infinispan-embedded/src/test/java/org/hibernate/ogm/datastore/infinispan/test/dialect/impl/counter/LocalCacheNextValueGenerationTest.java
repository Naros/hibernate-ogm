/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.datastore.infinispan.test.dialect.impl.counter;

import static org.fest.assertions.Fail.fail;

import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.ogm.cfg.OgmConfiguration;
import org.hibernate.ogm.datastore.infinispan.InfinispanProperties;
import org.hibernate.ogm.utils.TestForIssue;
import org.hibernate.ogm.utils.TestHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * At the moment counters only work for clustered caches, local caches will throw an exception if one tries to use
 * sequences.
 * <p>
 * This should work with Infinispan 9.2 Final. Check OGM-1376 for more information.
 *
 * @see <a href="https://hibernate.atlassian.net/browse/OGM-1376">OGM-1376</a>
 * @author Davide D'Alto
 */
@TestForIssue(jiraKey = { "OGM-1353", "OGM-1376" })
public class LocalCacheNextValueGenerationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testExceptionWithSequencesGenerator() throws Exception {
		thrown.expect( HibernateException.class );
		thrown.expectMessage( "OGM001108: Sequences or di generation is not supported for local caches" );

		startAndCloseFactory( EntityWithSequence.class );
	}

	@Test
	public void testExceptionWithTableGenerator() throws Exception {
		thrown.expect( HibernateException.class );
		thrown.expectMessage( "OGM001108: Sequences or di generation is not supported for local caches" );

		startAndCloseFactory( EntityWithTableGenerator.class );
	}

	private void startAndCloseFactory(Class<?> annotatedClass) {
		Map<String, String> defaultTestSettings = TestHelper.getDefaultTestSettings();
		defaultTestSettings.put( InfinispanProperties.CONFIGURATION_RESOURCE_NAME, "infinispan-local.xml" );

		Properties properties = new Properties();
		properties.putAll( defaultTestSettings );

		Configuration configuration = new OgmConfiguration();
		configuration.addAnnotatedClass( annotatedClass ).addProperties( properties );

		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings( configuration.getProperties() );
		StandardServiceRegistry serviceRegistry = builder.build();

		SessionFactory sessionFactory = null;
		try {
			sessionFactory = configuration.buildSessionFactory( serviceRegistry );
			fail( "expected exception was not raised" );
		}
		finally {
			if ( sessionFactory != null ) {
				sessionFactory.close();
			}
		}
	}

	@Entity
	@Table(name = "SEQUENCE_GENERATOR")
	private static class EntityWithSequence {

		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen2")
		@SequenceGenerator(name = "gen2", sequenceName = "SEQUENCE", initialValue = 0)
		Long id;
	}

	@Entity
	@Table(name = "TABLE_GENERATOR")
	private static class EntityWithTableGenerator {

		@Id
		@GeneratedValue(strategy = GenerationType.TABLE, generator = "gen2")
		@TableGenerator(name = "gen2", initialValue = 0, pkColumnValue = "TABLE_GENERATOR")
		Long id;
	}
}
