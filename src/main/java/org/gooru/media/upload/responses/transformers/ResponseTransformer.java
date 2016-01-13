package org.gooru.media.upload.responses.transformers;


import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface ResponseTransformer {
  
  void transform();
  String transformedBody();
  Map<String, String> transformedHeaders();
  int transformedStatus();
  
}
