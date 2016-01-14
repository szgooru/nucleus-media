package org.gooru.media.upload.routes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RouteConfiguration implements Iterable<RouteConfigurator> {

  private final Iterator<RouteConfigurator> internalIterator;

  public RouteConfiguration() {
    List<RouteConfigurator> configurators = new ArrayList<>();
    configurators.add(new RouteGlobalConfigurator());
    configurators.add(new RouteAuthConfigurator());
    configurators.add(new RouteFileUploadConfigurator());
    internalIterator = configurators.iterator();
  }

  @Override
  public Iterator<RouteConfigurator> iterator() {
    return new Iterator<RouteConfigurator>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public RouteConfigurator next() {
        return internalIterator.next();
      }

    };
  }

}
