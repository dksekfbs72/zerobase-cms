package com.zerobase.domain.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Aes256UtilTest {


	@Test
	void test1() {
	    String encrypt = Aes256Util.encrypt("Hello World");
		assertEquals(Aes256Util.decrypt(encrypt), "Hello World");
	}

}