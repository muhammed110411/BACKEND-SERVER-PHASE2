package com.example.phase2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class Phase2ApplicationTests {

	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> Class.forName(Phase2Application.class.getName()));
	}

}
