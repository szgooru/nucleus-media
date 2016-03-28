package org.gooru.media.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.media.infra.RedisClient;
import org.gooru.media.infra.S3Client;


public class Initializers implements Iterable<Initializer> {
  private final Iterator<Initializer> internalIterator;

  public Initializers() {
    final List<Initializer> initializers = new ArrayList<>();
    initializers.add(RedisClient.instance());
    initializers.add(S3Client.instance());
    internalIterator = initializers.iterator();
  }

  @Override
  public Iterator<Initializer> iterator() {
    return new Iterator<Initializer>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public Initializer next() {
        return internalIterator.next();
      }

    };
  }

}
