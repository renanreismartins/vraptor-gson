package br.com.caelum.vraptor.serialization.xstream;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class Exclusions implements ExclusionStrategy {

	private Serializee serializee;

	public Exclusions(Serializee serializee) {
		this.serializee = serializee;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		String fieldName = f.getName();
		Class<?> definedIn = f.getDeclaringClass();

		for (Entry<String, Class<?>> include : serializee.getIncludes().entries()) {
			if (isCompatiblePath(include, definedIn, fieldName)) {
				return false;
			}
		}
		for (Entry<String, Class<?>> exclude : serializee.getExcludes().entries()) {
			if (isCompatiblePath(exclude, definedIn, fieldName)) {
				return true;
			}
		}

		boolean skip = false;
		
		if (!serializee.isRecursive())
			skip = !isPrimitive(new Mirror().on(definedIn).reflect().field(fieldName).getType());
		
		return skip;
	}

	static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type.isEnum() || Number.class.isAssignableFrom(type) || type.equals(String.class)
				|| Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)
				|| Boolean.class.equals(type) || Character.class.equals(type);
	}

	private boolean isCompatiblePath(Entry<String, Class<?>> path, Class<?> definedIn, String fieldName) {
		return (path.getValue().equals(definedIn) && (path.getKey().equals(fieldName) || path.getKey().endsWith(
				"." + fieldName)));
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
