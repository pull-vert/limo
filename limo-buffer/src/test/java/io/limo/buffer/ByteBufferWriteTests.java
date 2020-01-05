/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.buffer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteBufferWriteTests {

	/**
	 * Allocate 13 bytes : 4 for int, 8 for long, 1 for byte
	 * ByteBuffer is natively Big Endian ordered
	 */
	private final ByteBuffer bb = ByteBuffer.allocateDirect(13);

	private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

	@AfterEach
	void after() {
		bb.clear();
	}

	private void verifyContent() {
		assertThat(bb.getInt(0)).isEqualTo(42);
		assertThat(bb.getLong(4)).isEqualTo(128L);
		assertThat(bb.get(12)).isEqualTo((byte) 0xa);
	}

	@Test
	@DisplayName("Direct write")
	void test1() {
		bb.putInt(42);
		bb.putLong(128L);
		bb.put((byte) 0xa);
		verifyContent();
	}

	@Test
	@DisplayName("Indexed write")
	void test2() {
		bb.putInt(0, 42);
		bb.putLong(4, 128L);
		bb.put(12, (byte) 0xa);
		verifyContent();
	}

	@Test
	@DisplayName("VarHandle read")
	void test3() {
		intHandle.set(bb, 0, 42);
		longHandle.set(bb, 4, 128L);
		bb.put(12, (byte) 0xa);
		verifyContent();
	}
}
