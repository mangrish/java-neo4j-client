package io.innerloop.neo4j.client.spi.impl.resty.web;

/**
 * Simple abstraction for queries into complex datastructures.
 * Not really needed, but I like playing around with generics. :)
 *
 * @author beders
 *
 * @param <T> the resource to operate on
 */

public abstract class PathQuery<T,S> {
	abstract S eval(T resource) throws Exception;
}
