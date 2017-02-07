package de.diddiz.codegeneration.codetree;

public enum Type {
	Int(int.class, "int");

	private final Class<?> clazz;
	private final String code;

	private Type(Class<?> clazz, String code) {
		this.clazz = clazz;
		this.code = code;
	}

	public Class<?> getTypeClass() {
		return clazz;
	}

	public String toCode() {
		return code;
	}
}
