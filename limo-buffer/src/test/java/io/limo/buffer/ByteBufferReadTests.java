/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.buffer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteBufferReadTests {

	/**
	 * Allocate 12 bytes : 4 for int and 8 for long
	 * ByteBuffer is natively Big Endian ordered
	 */
	private final ByteBuffer bb = ByteBuffer.allocateDirect(12);

	private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

	@BeforeAll
	void before() {
		bb.putInt(42);
		bb.putLong(128L);
	}

	@Test
	@DisplayName("Direct read")
	void test1() {
		bb.rewind();
		assertThat(bb.getInt()).isEqualTo(42);
		assertThat(bb.getLong()).isEqualTo(128L);
	}

	@Test
	@DisplayName("Indexed read")
	void test2() {
		assertThat(bb.getInt(0)).isEqualTo(42);
		assertThat(bb.getLong(4)).isEqualTo(128L);
	}

	@Test
	@DisplayName("VarHandle read")
	void test3() {
		assertThat(intHandle.get(bb, 0)).isEqualTo(42);
		assertThat(longHandle.get(bb, 4)).isEqualTo(128L);
	}
}
