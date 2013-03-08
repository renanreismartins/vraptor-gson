package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

public class VraptorGsonBuilder {

	protected GsonBuilder builder = new GsonBuilder();

	private boolean withoutRoot;

	private String alias;

	private Collection<JsonSerializer<?>> serializers;

	public VraptorGsonBuilder(Collection<JsonSerializer<?>> serializers) {
		this.serializers = serializers;
	}

	public boolean isWithoutRoot() {
		return withoutRoot;
	}

	public void setWithoutRoot(boolean withoutRoot) {
		this.withoutRoot = withoutRoot;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void indented() {
		builder.setPrettyPrinting();
	}

	public void setExclusionStrategies(ExclusionStrategy... strategies) {
		builder.setExclusionStrategies(strategies);
	}

	public Gson create() {
		for (JsonSerializer<?> adapter : serializers) {
			Class<?> adapterType = getAdapterType(adapter);
			boolean isHierarchical = adapter.getClass().isAnnotationPresent(HierarchicalAdapter.class);
			if (isHierarchical) {
				builder.registerTypeHierarchyAdapter(adapterType, adapter);				
			} else {
				builder.registerTypeAdapter(adapterType, adapter);
			}
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonSerializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		return (Class<?>) actualType;
	}
}