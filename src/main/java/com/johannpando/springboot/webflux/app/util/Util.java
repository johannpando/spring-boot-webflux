package com.johannpando.springboot.webflux.app.util;

import reactor.core.publisher.Mono;

public final class Util {

	public <V> Mono<V> then(Mono<V> other) {
		return other;
	}
	
	/*public Mono<V> then2(Mono<V> other) {
		return other;
	}*/
}
