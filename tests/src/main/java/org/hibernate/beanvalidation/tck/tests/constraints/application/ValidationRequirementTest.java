/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.application;

import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertCorrectConstraintTypes;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertCorrectConstraintViolationMessages;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertNumberOfViolations;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.beanvalidation.tck.beanvalidation.Sections;
import org.hibernate.beanvalidation.tck.tests.AbstractTCKTest;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ValidationRequirementTest extends AbstractTCKTest {

	@Deployment
	public static WebArchive createTestArchive() {
		return webArchiveBuilder()
				.withTestClassPackage( ValidationRequirementTest.class )
				.build();
	}

	@Test
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS, id = "c")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS, id = "e")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_OBJECTVALIDATION, id = "a")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_OBJECTVALIDATION, id = "b")
	public void testClassLevelConstraints() {
		System.out.println( "BV Code source: " + Validator.class.getProtectionDomain().getCodeSource() );
		Woman sarah = new Woman();
		sarah.setFirstName( "Sarah" );
		sarah.setLastName( "Jones" );
		sarah.setPersonalNumber( "000000-0000" );

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<Woman>> violations = validator.validate( sarah );

		assertNumberOfViolations(
				violations, 2
		); // SecurityCheck for Default in Person and Citizen
		assertCorrectConstraintTypes( violations, SecurityCheck.class, SecurityCheck.class );

		violations = validator.validate( sarah, TightSecurity.class );
		assertNumberOfViolations(
				violations, 1
		); // SecurityCheck for TightSecurity in Citizen
		assertCorrectConstraintTypes( violations, SecurityCheck.class );

		// just to make sure - validating against a group which does not have any constraints assigned to it
		violations = validator.validate( sarah, DummyGroup.class );
		assertNumberOfViolations( violations, 0 );

		sarah.setPersonalNumber( "740523-1234" );
		violations = validator.validate( sarah );
		assertNumberOfViolations( violations, 0 );

		violations = validator.validate( sarah, TightSecurity.class );
		assertNumberOfViolations( violations, 0 );
	}

	@Test
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS, id = "e")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "a")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "c")
	public void testFieldAccess() {
		SuperWoman superwoman = new SuperWoman();

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<SuperWoman>> violations = validator.validateProperty( superwoman, "firstName" );
		assertNumberOfViolations( violations, 0 );

		superwoman.setFirstName( null );
		violations = validator.validateProperty( superwoman, "firstName" );
		assertNumberOfViolations( violations, 1 );
		assertCorrectConstraintTypes( violations, NotNull.class );
	}

	@Test
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS, id = "e")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "a")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "d")
	public void testPropertyAccess() {
		SuperWoman superwoman = new SuperWoman();

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<SuperWoman>> violations = validator.validateProperty( superwoman, "lastName" );
		assertNumberOfViolations( violations, 0 );

		superwoman.setHiddenName( null );
		violations = validator.validateProperty( superwoman, "lastName" );
		assertNumberOfViolations( violations, 1 );
		assertCorrectConstraintTypes( violations, NotNull.class );
	}

	@Test
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "a")
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "b")
	public void testConstraintAppliedOnFieldAndProperty() {
		Building building = new Building( 10000000 );

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<Building>> violations = validator.validate( building );
		assertNumberOfViolations( violations, 2 );
		String expectedMessage = "Building costs are max {max} dollars.";
		assertCorrectConstraintViolationMessages( violations, expectedMessage, expectedMessage );
	}

	@Test(enabled = false)
//	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS,
//			id = "b",
//			note = "The spec is not clear about whether validation of static fields/properties should just be ignored or an exception should be thrown.")
	public void testIgnoreStaticFieldsAndProperties() {
		StaticFieldsAndProperties entity = new StaticFieldsAndProperties();

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<StaticFieldsAndProperties>> violations = validator.validate( entity );
		assertNumberOfViolations( violations, 0 );
	}

	@Test
	@SpecAssertion(section = Sections.CONSTRAINTDECLARATIONVALIDATIONPROCESS_REQUIREMENTS_PROPERTYVALIDATION, id = "e")
	public void testFieldAndPropertyVisibilityIsNotConstrained() {
		Visibility entity = new Visibility();

		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<Visibility>> violations = validator.validate( entity );
		assertNumberOfViolations( violations, 6 );
		assertCorrectConstraintTypes(
				violations, Min.class, Min.class, Min.class, DecimalMin.class, DecimalMin.class, DecimalMin.class
		);
		assertCorrectConstraintViolationMessages(
				violations,
				"publicField",
				"protectedField",
				"privateField",
				"publicProperty",
				"protectedProperty",
				"privateProperty"
		);

		entity.setValues( 100 );
		violations = validator.validate( entity );
		assertNumberOfViolations( violations, 0 );
	}

	static class StaticFieldsAndProperties {
		@NotNull
		static Object staticField = null;

		@NotNull
		static Object getStaticProperty() {
			return null;
		}
	}
}

interface DummyGroup {
}
