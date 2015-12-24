Nucleus Server
================

This is the HTTP server for Project Nucleus. 

This project contains just one main verticle which is responsible for spawning off of HTTP Server. This is the gateway to Nucleus components.

TODO
----
* Provide authorization/authentication for the end points. Based on success or failure, either provide response to caller or pass on auth data to other subsystems
* Provide any other throttling or gateway characteristics that we need

To understand build related stuff, take a look at **BUILD_README.md**.


How to do stuff
---------------

**Add new configuration**
* First add the key as constant in ConfigConstants.java
* Update the nucleus-server.json to provide a sample value

**Add new HTTP handler**
* If the HTTP end point is going to be inline, then skip to next point. Otherwise, add a constant to MessagebusEndpoints.java
* Add new route to RouteConstants.java
* If this route belongs to one of existing Route Configurator, add it there, provide handler and you are done. 
* Else create a new RouteConfigurator for that configuration
* Provide its implementation
* Apply the handle to specified route. If there is a need for separate delivery options, provide it
* Register the RouteConfigurator in RouteConfiguration
* That is it
