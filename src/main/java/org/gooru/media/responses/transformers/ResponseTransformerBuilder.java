package org.gooru.media.responses.transformers;

public class ResponseTransformerBuilder {

  public ResponseTransformer build(Object message) {
    return new HttpResponseTransformer(message);
  }

}
