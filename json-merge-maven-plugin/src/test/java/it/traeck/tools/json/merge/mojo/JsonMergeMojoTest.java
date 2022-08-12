package it.traeck.tools.json.merge.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonMergeMojoTest {

    @Test
    void shouldMergeSimpleJsonFiles() throws Exception {
        File testFile1 = File.createTempFile("test1", ".json");
        File testFile2 = File.createTempFile("test2", ".json");

        FileUtils.writeStringToFile(testFile1, "{\"a\":1,\"b\":2}", StandardCharsets.UTF_8);
        FileUtils.writeStringToFile(testFile2, "{\"c\":3,\"d\":4}", StandardCharsets.UTF_8);

        JsonMergeMojo mojo = new JsonMergeMojo();
        mojo.filenames = new ArrayList<String>();
        mojo.filenames.add(testFile1.getAbsolutePath());
        mojo.filenames.add(testFile2.getAbsolutePath());
        mojo.outputPath = FileUtils.getTempDirectoryPath();
        mojo.outputFilename = "testresult";
        mojo.prettyPrint = false;

        mojo.execute();

        File resultFile = new File(mojo.outputPath, mojo.outputFilename + ".json");
        String merged = FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8);

        assertEquals("{\"a\":1,\"b\":2,\"c\":3,\"d\":4}", merged);
    }

}
