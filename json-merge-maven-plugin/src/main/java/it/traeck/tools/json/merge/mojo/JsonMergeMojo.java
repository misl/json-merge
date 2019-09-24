package it.traeck.tools.json.merge.mojo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import it.traeck.tools.json.merge.JsonMerge;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Goal which validates OpenAPI based API specification files.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class JsonMergeMojo extends AbstractMojo {

  public enum Format {JSON, YAML, JSONANDYAML}

  // --------------------------------------------------------------------------
  // Mojo parameters
  // --------------------------------------------------------------------------

  /**
   * Location of the OpenAPI based specification, as URL or file.
   */
  @Parameter(name = "filenames", required = true)
  List filenames;

  /**
   * Output folder where to write final OpenAPI specification file.
   */
  @Parameter(name = "outputPath", property = "json-merge.maven.plugin.outputPath",
      defaultValue = "${project.build.directory}/merged-json")
  String outputPath;

  /**
   * Name of the output file in which to write final OpenAPI specification.
   */
  @Parameter(name = "outputFilename", property = "json-merge.maven.plugin.outputFilename", defaultValue = "json")
  String outputFilename = "json";

  /**
   * Format of the output file.
   * <p>
   * Allowed values are: JSON, YAML, JSONANDYAML
   */
  @Parameter(name = "outputFormat", property = "openapi.validator.maven.plugin.outputFormat", defaultValue = "JSON")
  Format outputFormat = Format.JSON;

  @Parameter(defaultValue = "${project}", readonly = true)
  MavenProject project;

  @Parameter(name = "encoding", property = "json-merge.maven.plugin.encoding")
  String encoding;

  String projectEncoding = "UTF-8";

  @Parameter(name = "prettyPrint", property = "json-merge.maven.plugin.prettyPrint")
  Boolean prettyPrint = true;

  // --------------------------------------------------------------------------
  // Overriding AbstractMojo
  // --------------------------------------------------------------------------

  @Override
  public void execute() throws MojoExecutionException {
    try {
      List<JsonObject> jsonObjects = readObjects( this.filenames );
      JsonObject mergedObject = JsonMerge.merge( jsonObjects.toArray( new JsonObject[jsonObjects.size()] ) );

      determineEncoding();
      String resultingJson = null;
      String resultingYaml = null;
      if ( Format.JSON.equals( outputFormat ) || Format.JSONANDYAML.equals( outputFormat ) ) {
        resultingJson = writeJson( mergedObject );
      }
      if ( Format.YAML.equals( outputFormat ) || Format.JSONANDYAML.equals( outputFormat ) ) {
        resultingYaml = writeYaml( mergedObject );
      }

      Path path = Paths.get( outputPath, "temp" );
      final File parentFile = path.toFile().getParentFile();
      if ( parentFile != null ) {
        parentFile.mkdirs();
      }

      if ( resultingJson != null ) {
        path = Paths.get( outputPath, outputFilename + ".json" );
        getLog().info( String.format( "Writing: %s", path.toString() ) );
        Files.write( path, resultingJson.getBytes( Charset.forName( encoding ) ) );
      }
      if ( resultingYaml != null ) {
        path = Paths.get( outputPath, outputFilename + ".yaml" );
        getLog().info( String.format( "Writing: %s", path.toString() ) );
        Files.write( path, resultingYaml.getBytes( Charset.forName( encoding ) ) );
      }
    } catch ( IOException e ) {
      getLog().error( "Error writing Json merge results", e );
      throw new MojoExecutionException( "Failed to write Json merg results", e );
    } catch ( Exception e ) {
      getLog().error( "Error while trying to merge Json", e );
      throw new MojoExecutionException( e.getMessage(), e );
    }
  }

  private List<JsonObject> readObjects( List<String> filenames ) throws IOException {
    Objects.requireNonNull( filenames, "Filenames are required for merging." );

    List<JsonObject> jsonObjects = new ArrayList<>( filenames.size() );
    for ( String filename : filenames ) {
      jsonObjects.add( readObject( filename ) );
    }
    return jsonObjects;
  }

  private JsonObject readObject( String filename ) throws IOException, JsonParseException {
    Objects.requireNonNull( filename );

    getLog().info( String.format( "Reading : %s", filename ) );
    Path path = Paths.get( filename );

    ObjectMapper mapper = new ObjectMapper();
    if ( isYamlFile( path ) ) {
      mapper = new ObjectMapper( new YAMLFactory() );
    }
    mapper.registerModule( new JSR353Module() );
    InputStream stream = new BufferedInputStream( Files.newInputStream( path ) );
    return mapper.readValue( stream, JsonObject.class );
  }

  private static boolean isYamlFile( Path path ) {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher( "regex:^.*\\.[Yy][Aa]?[Mm][Ll]$" );
    // Assume json file unless .yaml or .yml extension.
    return matcher.matches( path );
  }

  private void determineEncoding() {
    if ( project != null ) {
      String pEnc = project.getProperties().getProperty( "project.build.sourceEncoding" );
      if ( StringUtils.isNotBlank( pEnc ) ) {
        projectEncoding = pEnc;
      }
    }
    if ( StringUtils.isBlank( encoding ) ) {
      encoding = projectEncoding;
    }
    getLog().info( String.format( "Using '%s' encoding to write output files.", encoding ) );
  }

  private String writeYaml( JsonObject jsonObject ) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper( new YAMLFactory() );
    mapper.registerModule( new JSR353Module() );
    if ( prettyPrint ) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( jsonObject );
    } else {
      return mapper.writeValueAsString( jsonObject );
    }
  }

  private String writeJson( JsonObject jsonObject ) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule( new JSR353Module() );
    if ( prettyPrint ) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( jsonObject );
    } else {
      return mapper.writeValueAsString( jsonObject );
    }
  }
}
