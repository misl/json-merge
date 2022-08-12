package it.traeck.tools.json.merge;

import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for JsonMerge
 */
public class JsonMergeTest {

  @Test
  public void testMergeSimpleObjects() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", 1 ).build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "b", "b" ).build();
    JsonObject jsonObject3 = Json.createObjectBuilder()
        .addNull( "c" ).build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2, jsonObject3 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 3 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.getInt( "a" ) ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "b" ) ).isTrue();
    assertThat( mergeObject.getString( "b" ) ).isEqualTo( "b" );
    assertThat( mergeObject.containsKey( "c" ) ).isTrue();
    assertThat( mergeObject.isNull( "c" ) ).isTrue();
  }

  @Test
  public void testMergeOverlap() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", 1 ).build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", "a" )
        .add( "b", "b" ).build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 2 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.getString( "a" ) ).isEqualTo( "a" );
    assertThat( mergeObject.containsKey( "b" ) ).isTrue();
    assertThat( mergeObject.getString( "b" ) ).isEqualTo( "b" );
  }

  @Test
  public void testMergeOverlapArrayOverNumber() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", 1 ).build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .add( "b", "b" ).build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 2 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.get( "a" ).getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    assertThat( mergeObject.getJsonArray( "a" ).size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "b" ) ).isTrue();
    assertThat( mergeObject.getString( "b" ) ).isEqualTo( "b" );
  }

  @Test
  public void testMergeObjectInObject() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createObjectBuilder()
            .add( "x", 1 )
            .add( "z", 3 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createObjectBuilder()
            .add( "y", 2 )
            .add( "z", 4 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.get( "a" ).getValueType() ).isEqualTo( JsonArray.ValueType.OBJECT );
    JsonObject a = mergeObject.getJsonObject( "a" );
    assertThat( a.getInt( "x" ) ).isEqualTo( 1 );
    assertThat( a.getInt( "y" ) ).isEqualTo( 2 );
    assertThat( a.getInt( "z" ) ).isEqualTo( 4 );
  }

  @Test
  public void testMergeObjectInNotObject() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", "String" )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createObjectBuilder()
            .add( "y", 2 )
            .add( "z", 4 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.get( "a" ).getValueType() ).isEqualTo( JsonArray.ValueType.OBJECT );
    JsonObject a = mergeObject.getJsonObject( "a" );
    assertThat( a.getInt( "y" ) ).isEqualTo( 2 );
    assertThat( a.getInt( "z" ) ).isEqualTo( 4 );
  }

  @Test
  public void testMergeObjectInObjectNotPresent() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", 3 )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "b", Json.createObjectBuilder()
            .add( "y", 2 )
            .add( "z", 4 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 2 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    assertThat( mergeObject.getInt( "a" ) ).isEqualTo( 3 );
    assertThat( mergeObject.containsKey( "b" ) ).isTrue();
    assertThat( mergeObject.get( "b" ).getValueType() ).isEqualTo( JsonArray.ValueType.OBJECT );
    JsonObject a = mergeObject.getJsonObject( "b" );
    assertThat( a.getInt( "y" ) ).isEqualTo( 2 );
    assertThat( a.getInt( "z" ) ).isEqualTo( 4 );
  }

  @Test
  public void testMergeArrays() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( "b" )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 1 );
    assertThat( jsonArray.getString( 0 ) ).isEqualTo( "b" );
  }

  @Test
  public void testMergeArraysWithNothing() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .addNull()
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isEqualTo( jsonObject1 );
  }

  @Test
  public void testMergeArraysShorterIntoLonger() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .add( 2 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 3 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 0 ) ).isEqualTo( 3 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 2 );
  }

  @Test
  public void testMergeArraysLongerIntoShorter() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 2 )
            .add( 3 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 0 ) ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 3 );
  }

  @Test
  public void testMergeObjectsInArray() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createObjectBuilder()
                .add( "x", 1 )
                .build() )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createObjectBuilder()
                .add( "y", 2 )
                .build() )
            .add( 3 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.get( 0 ).getValueType() ).isEqualTo( JsonValue.ValueType.OBJECT );
    JsonObject element = jsonArray.get( 0 ).asJsonObject();
    assertThat( element.size() ).isEqualTo( 2 );
    assertThat( element.getInt( "x" ) ).isEqualTo( 1 );
    assertThat( element.getInt( "y" ) ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 3 );
  }

  @Test
  public void testMergeObjectInArrayNotObject() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createObjectBuilder()
                .add( "y", 2 )
                .build() )
            .add( 3 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.get( 0 ).getValueType() ).isEqualTo( JsonValue.ValueType.OBJECT );
    JsonObject element = jsonArray.get( 0 ).asJsonObject();
    assertThat( element.size() ).isEqualTo( 1 );
    assertThat( element.getInt( "y" ) ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 3 );
  }

  @Test
  public void testMergeArraysInArray() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createArrayBuilder()
                .add( 1 )
                .build() )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createArrayBuilder()
                .add( 2 )
                .add( 3 )
                .build() )
            .add( 4 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.get( 0 ).getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray subArray = jsonArray.get( 0 ).asJsonArray();
    assertThat( subArray.size() ).isEqualTo( 2 );
    assertThat( subArray.getInt( 0 ) ).isEqualTo( 2 );
    assertThat( subArray.getInt( 1 ) ).isEqualTo( 3 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 4 );
  }

  @Test
  public void testMergeArraysInArrayNotArray() {
    // Prepare
    JsonObject jsonObject1 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( 1 )
            .build() )
        .build();
    JsonObject jsonObject2 = Json.createObjectBuilder()
        .add( "a", Json.createArrayBuilder()
            .add( Json.createArrayBuilder()
                .add( 2 )
                .build() )
            .add( 4 )
            .build() )
        .build();

    // Execute
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2 );

    // Verify
    assertThat( mergeObject ).isNotNull();
    assertThat( mergeObject.size() ).isEqualTo( 1 );
    assertThat( mergeObject.containsKey( "a" ) ).isTrue();
    JsonValue jsonValue = mergeObject.get( "a" );
    assertThat( jsonValue.getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray jsonArray = jsonValue.asJsonArray();
    assertThat( jsonArray.size() ).isEqualTo( 2 );
    assertThat( jsonArray.get( 0 ).getValueType() ).isEqualTo( JsonValue.ValueType.ARRAY );
    JsonArray subArray = jsonArray.get( 0 ).asJsonArray();
    assertThat( subArray.size() ).isEqualTo( 1 );
    assertThat( subArray.getInt( 0 ) ).isEqualTo( 2 );
    assertThat( jsonArray.getInt( 1 ) ).isEqualTo( 4 );
  }
}
