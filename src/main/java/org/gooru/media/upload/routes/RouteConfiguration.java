package org.gooru.media.upload.routes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RouteConfiguration implements Iterable<RouteConfigurator> {

  private List<RouteConfigurator> configurators = null;
  private Iterator<RouteConfigurator> internalIterator;
  
  @Override
  public Iterator<RouteConfigurator> iterator() {
    Iterator<RouteConfigurator> iterator = new Iterator<RouteConfigurator>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public RouteConfigurator next() {
        return internalIterator.next();
      }
      
    };
    return iterator;
  }
  
  public RouteConfiguration() {
    configurators = new ArrayList<RouteConfigurator>();
    configurators.add(new RouteGlobalConfigurator());
    configurators.add(new RouteAuthConfigurator());
    configurators.add(new RouteFileUploadConfigurator());
    internalIterator = configurators.iterator();
  }

}
