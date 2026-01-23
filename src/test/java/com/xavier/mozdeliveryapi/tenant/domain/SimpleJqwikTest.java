package com.xavier.mozdeliveryapi.tenant.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

/**
 * Simple test to verify jqwik is working correctly.
 */
class SimpleJqwikTest {
    
    @Property
    void simplePropertyTest(@ForAll int number) {
        assertThat(number + 0).isEqualTo(number);
    }
    
    @Property(tries = 10)
    void stringLengthProperty(@ForAll String str) {
        assertThat(str.length()).isGreaterThanOrEqualTo(0);
    }
}