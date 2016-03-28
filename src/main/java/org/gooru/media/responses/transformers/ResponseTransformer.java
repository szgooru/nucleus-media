package org.gooru.media.responses.transformers;


import java.util.Map;

public interface ResponseTransformer {

  void transform();

  String transformedBody();

  Map<String, String> transformedHeaders();

  int transformedStatus();

}
