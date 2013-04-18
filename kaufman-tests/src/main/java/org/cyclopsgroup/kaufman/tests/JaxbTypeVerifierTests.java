package org.cyclopsgroup.kaufman.tests;

import java.io.IOException;

import org.junit.Test;

public abstract class JaxbTypeVerifierTests {
	private final JaxbTypeVerifier verifier;

	protected JaxbTypeVerifierTests(JaxbTypeVerifier verifier) {
		this.verifier = verifier;
	}

	public JaxbTypeVerifierTests() {
		this(JaxbTypeVerifier.newDefaultInstance());
	}

	@Test
	public void testTypesInSamePackage() throws IOException {
		verifier.verifyPackage(getClass().getPackage().getName());
	}
}
