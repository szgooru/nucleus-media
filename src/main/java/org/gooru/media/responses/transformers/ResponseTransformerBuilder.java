package org.gooru.media.responses.transformers;

public final class ResponseTransformerBuilder {

    private ResponseTransformerBuilder() {
        throw new AssertionError();
    }

    public static ResponseTransformer build(Object message) {
        return new HttpResponseTransformer(message);
    }

}
