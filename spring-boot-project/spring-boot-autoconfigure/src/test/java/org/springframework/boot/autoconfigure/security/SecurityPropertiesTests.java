/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SecurityProperties}.
 *
 * @author Dave Syer
 * @author Madhura Bhave
 */
class SecurityPropertiesTests {

	private SecurityProperties security = new SecurityProperties();

	private Binder binder;

	private MapConfigurationPropertySource source = new MapConfigurationPropertySource();

	@BeforeEach
	void setUp() {
		this.binder = new Binder(this.source);
	}

	@Test
	void validateDefaultFilterOrderMatchesMetadata() {
		assertThat(this.security.getFilter().getOrder()).isEqualTo(-100);
	}

	@Test
	void filterOrderShouldBind() {
		this.source.put("spring.security.filter.order", "55");
		this.binder.bind("spring.security", Bindable.ofInstance(this.security));
		assertThat(this.security.getFilter().getOrder()).isEqualTo(55);
	}

	@Test
	void userWhenNotConfiguredShouldUseDefaultNameAndGeneratedPassword() {
		SecurityProperties.User user = this.security.getUser();
		assertThat(user.getName()).isEqualTo("user");
		assertThat(user.getPassword()).isNotNull();
		assertThat(user.isPasswordGenerated()).isTrue();
		assertThat(user.getRoles()).isEmpty();
	}

	@Test
	void userShouldBindProperly() {
		this.source.put("spring.security.user.name", "foo");
		this.source.put("spring.security.user.password", "password");
		this.source.put("spring.security.user.roles", "ADMIN,USER");
		this.binder.bind("spring.security", Bindable.ofInstance(this.security));
		SecurityProperties.User user = this.security.getUser();
		assertThat(user.getName()).isEqualTo("foo");
		assertThat(user.getPassword()).isEqualTo("password");
		assertThat(user.isPasswordGenerated()).isFalse();
		assertThat(user.getRoles()).containsExactly("ADMIN", "USER");
	}

	@Test
	void passwordAutogeneratedIfEmpty() {
		this.source.put("spring.security.user.password", "");
		this.binder.bind("spring.security", Bindable.ofInstance(this.security));
		assertThat(this.security.getUser().isPasswordGenerated()).isTrue();
	}

}
