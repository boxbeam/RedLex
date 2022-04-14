package redempt.redlex.parser;

import redempt.redlex.data.Token;

import java.util.function.Function;

/**
 * Represents a parser able to process a single, specific token type
 * @author Redempt
 */
public interface ParserComponent {
	
	/**
	 * Creates a ParserComponent that operates on the String value of a token
	 * @param name The name of the token type to target
	 * @param func The function to parse the String value
	 * @return A ParserComponent
	 */
	public static ParserComponent mapString(String name, Function<String, Object> func) {
		return coerce(ComponentType.STRING_CONTENTS, name, func);
	}
	
	/**
	 * Creates a ParserComponent that operates on the parsed Objects of its child tokens
	 * @param name The name of the token type to target
	 * @param func The function to parse the result from the parsed Objects of child tokens
	 * @return A ParserComponent
	 */
	public static ParserComponent mapChildren(String name, Function<Object[], Object> func) {
		return coerce(ComponentType.CHILD_OBJECTS, name, func);
	}
	
	/**
	 * Creates a ParserComponent that operates directly on a token
	 * @param name The name of the token type to target
	 * @param func The function to parse the result from the Token object
	 * @return A ParserComponent
	 */
	public static ParserComponent mapToken(String name, Function<Token, Object> func) {
		return coerce(ComponentType.RAW_TOKEN, name, func);
	}
	
	static <T> ParserComponent coerce(ComponentType type, String name, Function<T, Object> func) {
		return new ParserComponent() {
			@Override
			public Object parse(Object context) {
				return func.apply((T) context);
			}
			
			@Override
			public ComponentType getType() {
				return type;
			}
			
			@Override
			public String getName() {
				return name;
			}
		};
	}
	
	/**
	 * Parses an Object
	 * @param context The context Object, depends on the type of the ParserComponent
	 * @return The parsed Object
	 */
	public Object parse(Object context);
	
	/**
	 * @return The type of the ParserComponent, which determines what context will be passed to it
	 */
	public ComponentType getType();
	
	/**
	 * @return The name of the token type targeted by this ParserComponent
	 */
	public String getName();
	
	enum ComponentType {
		
		CHILD_OBJECTS,
		STRING_CONTENTS,
		RAW_TOKEN
		
	}
	
}