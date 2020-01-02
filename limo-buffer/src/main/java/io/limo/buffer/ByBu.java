/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.buffer;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface ByBu {

	default void test() {
		VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
		ByteBuffer bb = ByteBuffer.allocateDirect(100);

		for (int i = 0 ; i < 25 ; i++) {
			intHandle.set(bb, i * 4);
		}
	}
}
