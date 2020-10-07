package com.kola.cleannotes.business.domain.util

interface EntityMapper<Entity, DomainModel>{
    fun mapFromEntity(entity: Entity): DomainModel
    fun maToEntity(domainModel: DomainModel):Entity
}