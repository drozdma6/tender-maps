package cz.cvut.fit.bap.parser.controller.builder;

import cz.cvut.fit.bap.parser.domain.DomainEntity;

public interface Builder<K, E extends DomainEntity<K>> {
    E build();
}
