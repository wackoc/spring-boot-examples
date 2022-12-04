/*
 * Copyright By ZATI
 * Copyright By 3a3c88295d37870dfd3b25056092d1a9209824b256c341f2cdc296437f671617
 * All rights reserved.
 *
 * If you are not the intended user, you are hereby notified that any use, disclosure, copying, printing, forwarding or
 * dissemination of this property is strictly prohibited. If you have got this file in error, delete it from your system.
 */

package com.neo.repository;

import com.google.common.base.CaseFormat;
import com.neo.model.Address;
import com.neo.model.UserDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;

@Component
public class MyRepositoryFactory {

    private final Map<String, BaseRepository> repositoryMap;

    public MyRepositoryFactory(List<JpaRepositoryFactoryBean> repositoryFactoryBeans) {
        this.repositoryMap = new HashMap<>();
        String packageName = this.getClass().getPackage().getName();
        Optional.ofNullable(repositoryFactoryBeans).ifPresent(beans -> {
            beans.stream()
                .filter(b -> b.getObjectType().getPackage().getName().startsWith(packageName))
                .filter(b -> BaseRepository.class.isAssignableFrom(b.getRepositoryInformation().getRepositoryInterface()) )
                .forEach(b -> {
                    Repository repository = b.getObject();
                    RepositoryInformation repositoryInformation = b.getRepositoryInformation();
                    Class<BaseRepository> repositoryInterface = (Class<BaseRepository>) repositoryInformation.getRepositoryInterface();
                    PersistentEntity persistentEntity = b.getPersistentEntity();
                    String entityName = StringUtils.substringAfterLast(persistentEntity.getName(), ".");
                    String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityName);
                    repositoryMap.put(tableName, repositoryInterface.cast(repository));
                });
        });
    }
}
