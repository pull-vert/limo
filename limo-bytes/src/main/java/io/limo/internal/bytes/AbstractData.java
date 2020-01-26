package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;

public abstract class AbstractData implements Data {

	boolean isBigEndian = true;

	/**
	 * The data reader
	 */
	@NotNull
	Reader reader;

	@NotNull
	@Override
	public Reader getReader() {
		return reader;
	}

	@NotNull
	@Override
	public ByteOrder getByteOrder() {
		return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}
}
