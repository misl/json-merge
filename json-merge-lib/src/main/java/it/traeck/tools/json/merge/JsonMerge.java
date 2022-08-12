package it.traeck.tools.json.merge;

import jakarta.json.*;
import java.util.Objects;

/**
 * Utility class.
 */
public class JsonMerge {

  /**
   * Merges multiple {@link jakarta.json.JsonObject} objects into a
   * single one. The first {@link jakarta.json.JsonObject} will be
   * merged with and empty {@link jakarta.json.JsonObject}. Each
   * following {@link jakarta.json.JsonObject} with be merged with
   * the result of the previous merge.
   * <p>
   * Please note that the order is important as overlap will be
   * overwritten. As a result latter {@link jakarta.json.JsonObject}
   * objects take precedence over the former ones.
   *
   * @param jsonObjects the json objects to be merged.
   * @return {@link jakarta.json.JsonObject} with merged results.
   */
  public static JsonObject merge( JsonObject... jsonObjects ) {
    Objects.requireNonNull( jsonObjects );

    JsonObject merged = null;
    for ( JsonObject jsonObject : jsonObjects ) {
      if ( null == merged ) {
        merged = jsonObject;
      } else {
        merged = mergeObject( merged, jsonObject );
      }
    }

    return merged;
  }

  private static JsonObject mergeObject( JsonObject jsonObject1, JsonObject jsonObject2 ) {
    JsonObjectBuilder objectBuilder = Json.createObjectBuilder( jsonObject1 );
    jsonObject2.forEach( ( key, value ) -> {
      switch ( value.getValueType() ) {
        case NULL:
          objectBuilder.addNull( key );
          break;
        case TRUE:
        case FALSE:
        case NUMBER:
        case STRING:
          objectBuilder.add( key, value );
          break;
        case ARRAY:
          if ( jsonObject1.containsKey( key ) &&
              JsonValue.ValueType.ARRAY.equals( jsonObject1.get( key ).getValueType() ) ) {
            JsonArray mergedArray = mergeArray( jsonObject1.getJsonArray( key ), value.asJsonArray() );
            objectBuilder.add( key, mergedArray );
          } else {
            objectBuilder.add( key, value );
          }
          break;
        case OBJECT:
          if ( jsonObject1.containsKey( key ) &&
              JsonValue.ValueType.OBJECT.equals( jsonObject1.get( key ).getValueType() ) ) {
            JsonObject mergedEntry = mergeObject( jsonObject1.getJsonObject( key ), value.asJsonObject() );
            objectBuilder.add( key, mergedEntry );
          } else {
            objectBuilder.add( key, value );
          }
          break;
      }
    } );
    return objectBuilder.build();
  }

  private static JsonArray mergeArray( JsonArray jsonArray1, JsonArray jsonArray2 ) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder( jsonArray1 );
    int index = 0;
    for( JsonValue value : jsonArray2) {
      if ( jsonArray1.size() <= index ) {
        arrayBuilder.add( value );
        continue;
      }
      JsonValue array1Value = jsonArray1.get( index );
      switch ( value.getValueType() ) {
        case NULL:
          // Do not overwrite the original value.
          break;
        case TRUE:
        case FALSE:
        case NUMBER:
        case STRING:
          arrayBuilder.set( index, value );
          break;
        case ARRAY:
          if ( JsonValue.ValueType.ARRAY.equals( array1Value.getValueType() ) ) {
            JsonArray mergedEntry = mergeArray( array1Value.asJsonArray(), value.asJsonArray() );
            arrayBuilder.set( index, mergedEntry );
          } else {
            arrayBuilder.set( index, value );
          }
          break;
        case OBJECT:
          if ( JsonValue.ValueType.OBJECT.equals( array1Value.getValueType() ) ) {
            JsonObject mergedEntry = mergeObject( array1Value.asJsonObject(), value.asJsonObject() );
            arrayBuilder.set( index, mergedEntry );
          } else {
            arrayBuilder.set( index, value );
          }
          break;
      }
      index++;
    }
    return arrayBuilder.build();
  }
}
